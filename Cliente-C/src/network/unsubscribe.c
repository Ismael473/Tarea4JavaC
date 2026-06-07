#include "network/unsubscribe.h"
#include "ui/states.h"
#include "cJSON.h"

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>

#define BUFFER_SIZE 1024

bool UnsubscribeFromServer() {
    if (!App.client.subscribed) {
        return false;
    }

    int sock;
    struct sockaddr_in serverAddr;

    sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock < 0) {
        perror("socket");
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

    cJSON *json = cJSON_CreateObject();
    cJSON_AddStringToObject(json, "type", "unsubscribe");
    cJSON_AddStringToObject(json, "clientId", App.client.uuid);

    char *request = cJSON_PrintUnformatted(json);
    cJSON_Delete(json);

    printf("Sending unsubscribe: %s\n", request);

    if (send(sock, request, strlen(request), 0) < 0) {
        perror("send");
        cJSON_free(request);
        close(sock);
        return false;
    }

    if (send(sock, "\n", 1, 0) < 0) {
        perror("send newline");
        cJSON_free(request);
        close(sock);
        return false;
    }

    cJSON_free(request);
    close(sock);
    return true;
}
