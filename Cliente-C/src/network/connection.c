#include "network/connection.h"
#include "ui/states.h"
#include "cJSON.h"

#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <pthread.h>
#include <errno.h>

#define DISCOVERY_PORT 5001
#define SERVER_PORT 5000
#define DISCOVERY_MSG "DISCOVER_SPACEINVADERS"
#define LINE_BUF 16384

static int connSock = -1;
static pthread_t readerThread;
static volatile bool readerRunning = false;
static volatile bool readerStarted = false;
static char latestFrame[CONN_BUFFER_SIZE];
static pthread_mutex_t frameMutex = PTHREAD_MUTEX_INITIALIZER;

static char latestResponse[LINE_BUF];
static pthread_mutex_t responseMutex = PTHREAD_MUTEX_INITIALIZER;
static pthread_cond_t responseCond = PTHREAD_COND_INITIALIZER;
static volatile bool waitingForResponse = false;

static void Debug(const char *msg) {
    printf("[CONN] %s\n", msg);
    fflush(stdout);
}

static bool UdpDiscovery(void) {
    int sock;
    int broadcastEnabled = 1;
    struct sockaddr_in broadcastAddr;
    struct sockaddr_in fromAddr;
    socklen_t fromAddrLen = sizeof(fromAddr);
    char buffer[LINE_BUF];

    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock < 0) { perror("socket"); return false; }

    if (setsockopt(sock, SOL_SOCKET, SO_BROADCAST, &broadcastEnabled, sizeof(broadcastEnabled)) < 0) {
        perror("setsockopt"); close(sock); return false;
    }

    struct timeval timeout = { .tv_sec = 5, .tv_usec = 0 };
    if (setsockopt(sock, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) < 0) {
        perror("setsockopt SO_RCVTIMEO"); close(sock); return false;
    }

    memset(&broadcastAddr, 0, sizeof(broadcastAddr));
    broadcastAddr.sin_family = AF_INET;
    broadcastAddr.sin_port = htons(DISCOVERY_PORT);
    broadcastAddr.sin_addr.s_addr = inet_addr("255.255.255.255");

    if (sendto(sock, DISCOVERY_MSG, strlen(DISCOVERY_MSG), 0,
               (struct sockaddr*)&broadcastAddr, sizeof(broadcastAddr)) < 0) {
        perror("sendto"); close(sock); return false;
    }

    int received = recvfrom(sock, buffer, sizeof(buffer) - 1, 0,
                            (struct sockaddr*)&fromAddr, &fromAddrLen);
    if (received < 0) { perror("recvfrom"); close(sock); return false; }

    buffer[received] = '\0';
    snprintf(App.server.host, sizeof(App.server.host), "%s", inet_ntoa(fromAddr.sin_addr));
    App.server.port = SERVER_PORT;
    Debug("Discovered server");
    close(sock);
    return true;
}

static int TcpConnect(void) {
    struct sockaddr_in serverAddr;
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) { perror("socket TCP"); return -1; }

    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(App.server.port);

    if (inet_pton(AF_INET, App.server.host, &serverAddr.sin_addr) <= 0) {
        perror("inet_pton"); close(sock); return -1;
    }

    if (connect(sock, (struct sockaddr*)&serverAddr, sizeof(serverAddr)) < 0) {
        perror("connect"); close(sock); return -1;
    }
    return sock;
}

static int SetReadTimeout(int fd, int seconds) {
    struct timeval tv = { .tv_sec = seconds, .tv_usec = 0 };
    return setsockopt(fd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
}

static bool SendString(int fd, const char *str) {
    if (!str) return false;
    int len = strlen(str);
    if (send(fd, str, len, 0) < 0) return false;
    if (send(fd, "\n", 1, 0) < 0) return false;
    return true;
}

static int ReadLine(int fd, char *buf, int size) {
    int i = 0;
    while (i < size - 1) {
        char c;
        int n = recv(fd, &c, 1, 0);
        if (n <= 0) {
            if (n == 0) Debug("ReadLine: connection closed");
            else Debug("ReadLine: recv error");
            return -1;
        }
        if (c == '\n') break;
        buf[i++] = c;
    }
    buf[i] = '\0';
    return i;
}

bool ConnectionEstablish(void) {
    Debug("Starting connection establish...");
    if (!UdpDiscovery()) { Debug("UDP discovery failed"); return false; }

    connSock = TcpConnect();
    if (connSock < 0) { Debug("TCP connect failed"); return false; }

    SetReadTimeout(connSock, 5);

    cJSON *json = cJSON_CreateObject();
    cJSON_AddStringToObject(json, "type", "subscribe");
    cJSON_AddStringToObject(json, "clientId", App.client.uuid);
    char *request = cJSON_PrintUnformatted(json);
    cJSON_Delete(json);

    if (!request) { Debug("cJSON_Print failed"); close(connSock); connSock = -1; return false; }

    bool sent = SendString(connSock, request);
    cJSON_free(request);
    if (!sent) { Debug("send subscribe failed"); close(connSock); connSock = -1; return false; }

    char response[LINE_BUF];
    if (ReadLine(connSock, response, sizeof(response)) < 0) {
        Debug("read subscribe response failed");
        close(connSock); connSock = -1; return false;
    }

    printf("[CONN] Subscribe response: %s\n", response);
    fflush(stdout);

    cJSON *root = cJSON_Parse(response);
    if (!root) { Debug("parse subscribe response failed"); close(connSock); connSock = -1; return false; }

    cJSON *status = cJSON_GetObjectItem(root, "status");
    bool ok = status != NULL && strcmp(status->valuestring, "ok") == 0;
    cJSON_Delete(root);

    if (!ok) { Debug("subscribe status not ok"); close(connSock); connSock = -1; return false; }

    SetReadTimeout(connSock, 0);
    App.client.subscribed = true;
    Debug("Connection established successfully");
    ConnectionStartReader();
    return true;
}

bool ConnectionSend(const char *json) {
    if (connSock < 0) { Debug("Send: no connection"); return false; }
    return SendString(connSock, json);
}

bool ConnectionSendAndReadResponse(const char *json, char *response, int responseSize) {
    if (connSock < 0) { Debug("SendAndRead: no connection"); return false; }

    if (!SendString(connSock, json)) {
        Debug("SendAndRead: send failed");
        return false;
    }

    // Signal the reader thread to capture the next response
    pthread_mutex_lock(&responseMutex);
    latestResponse[0] = '\0';
    waitingForResponse = true;
    pthread_mutex_unlock(&responseMutex);

    // Wait for the reader thread to signal that a response arrived
    pthread_mutex_lock(&responseMutex);
    struct timespec deadline;
    clock_gettime(CLOCK_REALTIME, &deadline);
    deadline.tv_sec += 10;

    bool gotResponse = false;
    while (waitingForResponse) {
        int rc = pthread_cond_timedwait(&responseCond, &responseMutex, &deadline);
        if (rc == ETIMEDOUT) {
            Debug("SendAndRead: timed out waiting for response");
            waitingForResponse = false;
            break;
        }
    }
    gotResponse = !waitingForResponse;

    if (gotResponse) {
        strncpy(response, latestResponse, responseSize - 1);
        response[responseSize - 1] = '\0';
    }
    pthread_mutex_unlock(&responseMutex);

    if (!gotResponse) {
        Debug("SendAndRead: too many attempts");
        return false;
    }

    // Check if it's an error response
    cJSON *root = cJSON_Parse(response);
    if (root) {
        cJSON *type = cJSON_GetObjectItem(root, "type");
        if (type != NULL && strcmp(type->valuestring, "error") == 0) {
            Debug("SendAndRead: server returned error");
            cJSON_Delete(root);
            return false;
        }
        cJSON_Delete(root);
    }

    return true;
}

static void *ReaderThreadFunc(void *arg) {
    Debug("Reader thread started");
    char line[LINE_BUF];
    while (readerRunning) {
        int n = ReadLine(connSock, line, sizeof(line));
        if (n < 0) {
            Debug("Reader thread: connection lost");
            break;
        }

        cJSON *root = cJSON_Parse(line);
        if (!root) continue;

        cJSON *enemies = cJSON_GetObjectItem(root, "enemies");

        if (waitingForResponse && !enemies) {
            pthread_mutex_lock(&responseMutex);
            strncpy(latestResponse, line, sizeof(latestResponse) - 1);
            latestResponse[sizeof(latestResponse) - 1] = '\0';
            waitingForResponse = false;
            pthread_cond_signal(&responseCond);
            pthread_mutex_unlock(&responseMutex);
            cJSON_Delete(root);
            continue;
        }


        if (enemies) {
            pthread_mutex_lock(&frameMutex);
            strncpy(latestFrame, line, sizeof(latestFrame) - 1);
            latestFrame[sizeof(latestFrame) - 1] = '\0';
            pthread_mutex_unlock(&frameMutex);
        }

        cJSON_Delete(root);
    }
    Debug("Reader thread exiting");
    return NULL;
}

void ConnectionStartReader(void) {
    if (readerRunning) return;
    readerRunning = true;
    if (pthread_create(&readerThread, NULL, ReaderThreadFunc, NULL) == 0) {
        readerStarted = true;
        Debug("Reader thread launched");
    } else {
        Debug("Failed to create reader thread");
        readerRunning = false;
    }
}

const char* ConnectionGetLatestFrame(void) {
    static char copy[CONN_BUFFER_SIZE];
    pthread_mutex_lock(&frameMutex);
    strncpy(copy, latestFrame, sizeof(copy) - 1);
    copy[sizeof(copy) - 1] = '\0';
    pthread_mutex_unlock(&frameMutex);
    return copy;
}

void ConnectionClose(void) {
    if (connSock < 0) return;
    Debug("Closing connection...");

    cJSON *json = cJSON_CreateObject();
    cJSON_AddStringToObject(json, "type", "unsubscribe");
    cJSON_AddStringToObject(json, "clientId", App.client.uuid);
    char *request = cJSON_PrintUnformatted(json);
    cJSON_Delete(json);
    if (request) {
        SendString(connSock, request);
        cJSON_free(request);
    }

    if (readerStarted) {
        readerRunning = false;
        shutdown(connSock, SHUT_RD);
        pthread_join(readerThread, NULL);
    }

    close(connSock);
    connSock = -1;
    readerStarted = false;
    App.client.subscribed = false;
    Debug("Connection closed");
}
