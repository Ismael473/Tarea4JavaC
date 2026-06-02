#include "network/unsubscribe.h"
#include "ui/states.h"

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
    char request[BUFFER_SIZE];

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

    snprintf(
        request,
        sizeof(request),
        "{\"type\":\"unsubscribe\",\"clientId\":\"%s\"}\n",
        App.client.uuid
    );

    printf("Sending unsubscribe: %s", request);

    if (send(sock, request, strlen(request), 0) < 0) {
        perror("send");
        close(sock);
        return false;
    }

    close(sock);
    return true;
}
