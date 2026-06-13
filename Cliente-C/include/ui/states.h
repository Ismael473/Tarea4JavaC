#ifndef STATES_H
#define STATES_H

#include "raylib.h"
#include <stdbool.h>

typedef enum {
    HANDSHAKE_SCREEN,
    MENU_SCREEN,
    GAME_SCREEN,
    ROOMS_SCREEN,
    SPECTATE_SCREEN
} AppScreen;

typedef struct {
    Font mainFont;
    Texture2D logo;
} AppAssets;

typedef struct {
    char uuid[37];
    char roomId[37];
    bool subscribed;
    bool subscriptionFinished;
    bool subscriptionSuccess;
    volatile char lastChar;
    int serialPort;
    bool controlThreadShouldClose;
} AppClient;

typedef struct {
    int port;
    char host[256];
} AppServer;

typedef struct {
    AppScreen currentScreen;
    AppAssets assets;
    AppClient client;
    AppServer server;
    bool shouldClose;
} AppGlobals;


extern AppGlobals App;

#endif
