#include "ui/menu.h"
#include "raylib.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/button.h"
#include "ui/text.h"
#include "ui/rooms.h"
#include "ui/game.h"
#include "network/connection.h"
#include "cJSON.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "control/control.h"


static bool playRequested = false;
static bool creatingRoom = false;

void InitMenu() {
    playRequested = false;
    creatingRoom = false;
}

void UpdateMenu() {
    UpdateStars();

    if (creatingRoom) {
        cJSON *json = cJSON_CreateObject();
        cJSON_AddStringToObject(json, "type", "create_room");
        cJSON_AddStringToObject(json, "clientId", App.client.uuid);
        char *createReq = cJSON_PrintUnformatted(json);
        cJSON_Delete(json);

        char response[4096];
        if (createReq && ConnectionSendAndReadResponse(createReq, response, sizeof(response))) {
            printf("create_room response: %s\n", response);
            fflush(stdout);

            cJSON *root = cJSON_Parse(response);
            if (root) {
                cJSON *roomId = cJSON_GetObjectItem(root, "roomId");
                if (roomId) {
                    strncpy(App.client.roomId, roomId->valuestring, sizeof(App.client.roomId) - 1);
                    App.client.roomId[sizeof(App.client.roomId) - 1] = '\0';
                }
                cJSON_Delete(root);
            }

            InitGame();
            creatingRoom = false;
            App.currentScreen = GAME_SCREEN;
        } else {
            creatingRoom = false;
            playRequested = false;
            printf("Failed to create room\n");
            fflush(stdout);
        }

        if (createReq) cJSON_free(createReq);
    }

    if (playRequested && !creatingRoom) {
        creatingRoom = true;
    }
}

void DrawMenu() {
    ClearBackground(BLACK);
    DrawStars();

    DrawTextureEx(
        App.assets.logo,
        (Vector2){(GetScreenWidth() - App.assets.logo.width * 0.4f) / 2, (GetScreenHeight() - App.assets.logo.height * 0.4f - 160) / 2},
        0.0f,
        0.4f,
        WHITE
    );

    Button PlayButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, (GetScreenHeight() / 2) + 20, 200, 60},
        .text = creatingRoom ? "..." : "Jugar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = creatingRoom ? GRAY : WHITE,
        .standbyColor = creatingRoom ? DARKGRAY : LIME,
        .hoverColor = GRAY
    };

    bool pressed = DrawButton(PlayButton);

    if (pressed && !creatingRoom) {
        playRequested = true;
        App.client.controlThreadShouldClose = false;
        ControllerInit();
    }

    Button SpecButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, (GetScreenHeight() / 2) + 90, 200, 60},
        .text = "Espectar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = creatingRoom ? GRAY : WHITE,
        .standbyColor = LIME,
        .hoverColor = GRAY
    };

    pressed = DrawButton(SpecButton);

    if (pressed && !creatingRoom) {
        App.currentScreen = ROOMS_SCREEN;
        InitRooms();
    }

    Button ExitButton = {
        .bounds = {GetScreenWidth() - 50, 10, 40, 40},
        .text = "X",
        .fontSize = 20,
        .spacing = 2,
        .textColor = WHITE,
        .standbyColor = RED,
        .hoverColor = DARKGRAY
    };

    pressed = DrawButton(ExitButton);

    if (pressed && !creatingRoom) {
        App.shouldClose = true;
    }

    Text uuid = {
        .text = App.client.uuid,
        .position = (Vector2){10, GetScreenHeight() - 10},
        .fontSize = 10,
        .spacing = 1,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_BOTTOM_LEFT,
        .opacity = 0.5f
    };

    DrawUIText(uuid);
}
