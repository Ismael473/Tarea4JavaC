#ifndef CONNECTION_H
#define CONNECTION_H

#include <stdbool.h>

#define CONN_BUFFER_SIZE 16384

bool ConnectionEstablish(void);
bool ConnectionSend(const char *json);
bool ConnectionSendAndReadResponse(const char *json, char *response, int responseSize);
void ConnectionStartReader(void);
const char* ConnectionGetLatestFrame(void);
void ConnectionClose(void);

#endif
