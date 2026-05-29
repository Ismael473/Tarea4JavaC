#include "network/subscribe.h"
#include "ui/states.h"

#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <sys/time.h>

#define DISCOVERY_PORT 5001
#define SERVER_PORT 5000
#define DISCOVERY_MESSAGE "DISCOVER_SPACEINVADERS"
#define BUFFER_SIZE 1024

bool DiscoverServer() {
    int sock;
    int broadcastEnabled = 1;

    struct sockaddr_in broadcastAddr;
    struct sockaddr_in fromAddr;
    socklen_t fromAddrLen = sizeof(fromAddr);

    char buffer[BUFFER_SIZE];

    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock < 0) {
        perror("socket");
        return false;
    }

    if (setsockopt(
        sock,
        SOL_SOCKET,
        SO_BROADCAST,
        &broadcastEnabled,
        sizeof(broadcastEnabled)
    ) < 0) {
        perror("setsockopt");
        close(sock);
        return false;
    }

    struct timeval timeout;
    timeout.tv_sec = 5;
    timeout.tv_usec = 0;

    if (setsockopt(
        sock,
        SOL_SOCKET,
        SO_RCVTIMEO,
        &timeout,
        sizeof(timeout)
    ) < 0) {
        perror("setsockopt SO_RCVTIMEO");
        close(sock);
        return false;
    }

    memset(&broadcastAddr, 0, sizeof(broadcastAddr));
    broadcastAddr.sin_family = AF_INET;
    broadcastAddr.sin_port = htons(DISCOVERY_PORT);
    broadcastAddr.sin_addr.s_addr = inet_addr("255.255.255.255");

    if(sendto(
        sock,
        DISCOVERY_MESSAGE,
        strlen(DISCOVERY_MESSAGE),
        0,
        (struct sockaddr *)&broadcastAddr,
        sizeof(broadcastAddr)
    ) < 0) {
        perror("sendto");
        close(sock);
        return false;
    }

    int recieved = recvfrom(
        sock,
        buffer,
        sizeof(buffer) - 1,
        0,
        (struct sockaddr *)&fromAddr,
        &fromAddrLen
    );

    if (recieved < 0) {
        perror("recvfrom");
        close(sock);
        return false;
    }

    buffer[recieved] = '\0';

    snprintf(App.server.host, sizeof(App.server.host), "%s", inet_ntoa(fromAddr.sin_addr));

    App.server.port = SERVER_PORT;

    printf("Discovered server at %s:%d\n", App.server.host, App.server.port);
    close(sock);

    return true;
}

bool SubscribeToServer() {
    if (!DiscoverServer()) {
        return false;
    }

    int sock;
    struct sockaddr_in serverAddr;
    char request[BUFFER_SIZE];
    char response[BUFFER_SIZE];

    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("socket TCP");
        return false;
    }

    memset(&serverAddr, 0, sizeof(serverAddr));
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(App.server.port);

    if (inet_pton(AF_INET, App.server.host, &serverAddr.sin_addr) <= 0) {
        perror("inet_pton");
        close(sock);
        return false;
    }

    if (connect(sock, (struct sockaddr *)&serverAddr, sizeof(serverAddr)) < 0) {
        perror("connect");
        close(sock);
        return false;
    }

    snprintf(
        request,
        sizeof(request),
        "{\"type\":\"subscribe\",\"clientId\":\"%s\"}\n",
        App.client.uuid
    );

    printf("Sending handshake: %s", request);

    if (send(sock, request, strlen(request), 0) < 0) {
        perror("send");
        close(sock);
        return false;
    }

    int received = recv(sock, response, sizeof(response) - 1, 0);
    if (received < 0) {
        perror("recv");
        close(sock);
        return false;
    }

    if (received == 0) {
        printf("Server closed connection\n");
        close(sock);
        return false;
    }

    response[received] = '\0';

    printf("Server response: %s\n", response);

    close(sock);

    return strstr(response, "\"status\":\"ok\"") != NULL;
}