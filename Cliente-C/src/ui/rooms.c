#include "ui/rooms.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/menu.h"
#include "ui/room_list.h"
#include "ui/button.h"
#include "ui/text.h"
#include "ui/spectate.h"
#include "network/connection.h"
#include "cJSON.h"
#include <string.h>
#include <stdio.h>

static RoomList roomList;
static bool fetchingRooms;
static bool joiningRoom;
static int selectedIndex;

void InitRooms() {
    InitStars();
    InitRoomList(&roomList, (Rectangle) {
        .x = 50,
        .y = 70,
        .width = GetScreenWidth() - 100,
        .height = GetScreenHeight() - 160
    });
    fetchingRooms = true;
    joiningRoom = false;
    selectedIndex = -1;
}

void UpdateRooms() {
    UpdateStars();

    if (IsKeyPressed(KEY_ESCAPE) && !joiningRoom) {
        InitMenu();
        App.currentScreen = MENU_SCREEN;
        return;
    }

    if (fetchingRooms) {
        cJSON *json = cJSON_CreateObject();
        cJSON_AddStringToObject(json, "type", "list_rooms");
        cJSON_AddStringToObject(json, "clientId", App.client.uuid);
        char *request = cJSON_PrintUnformatted(json);
        cJSON_Delete(json);

        if (request) {
            char response[CONN_BUFFER_SIZE];
            if (ConnectionSendAndReadResponse(request, response, sizeof(response))) {
                cJSON *root = cJSON_Parse(response);
                if (root) {
                    cJSON *rooms = cJSON_GetObjectItem(root, "rooms");
                    if (rooms && cJSON_IsArray(rooms)) {
                        int count = cJSON_GetArraySize(rooms);
                        for (int i = 0; i < count && i < MAX_ROOMS; i++) {
                            cJSON *item = cJSON_GetArrayItem(rooms, i);
                            if (!item) continue;

                            cJSON *roomId = cJSON_GetObjectItem(item, "roomId");
                            cJSON *playerCount = cJSON_GetObjectItem(item, "playerCount");
                            cJSON *spectatorCount = cJSON_GetObjectItem(item, "spectatorCount");

                            char name[64];
                            snprintf(name, sizeof(name), "Sala %d", i + 1);

                            AddRoom(&roomList, name,
                                    roomId ? roomId->valuestring : "",
                                    playerCount ? playerCount->valueint : 0,
                                    spectatorCount ? spectatorCount->valueint : 0);
                        }
                    }
                    cJSON_Delete(root);
                }
            }
            cJSON_free(request);
        }
        fetchingRooms = false;
    }

    if (joiningRoom) {
        cJSON *json = cJSON_CreateObject();
        cJSON_AddStringToObject(json, "type", "join_room");
        cJSON_AddStringToObject(json, "clientId", App.client.uuid);
        cJSON_AddStringToObject(json, "roomId", roomList.rooms[selectedIndex].roomId);
        char *request = cJSON_PrintUnformatted(json);
        cJSON_Delete(json);

        if (request) {
            char response[CONN_BUFFER_SIZE];
            if (ConnectionSendAndReadResponse(request, response, sizeof(response))) {
                strncpy(App.client.roomId, roomList.rooms[selectedIndex].roomId,
                        sizeof(App.client.roomId) - 1);
                App.client.roomId[sizeof(App.client.roomId) - 1] = '\0';
                InitSpectate();
                App.currentScreen = SPECTATE_SCREEN;
            }
            cJSON_free(request);
        }
        joiningRoom = false;
    }
}

void DrawRooms() {
    ClearBackground(BLACK);
    DrawStars();

    Text title = {
        .text = "Salas disponibles",
        .position = (Vector2){GetScreenWidth() / 2, 30},
        .fontSize = 20,
        .spacing = 2,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_TOP_CENTER,
        .opacity = 1.0f
    };

    DrawUIText(title);

    if (!fetchingRooms) {
        int selected = DrawRoomList(&roomList);

        if (selected != -1 && !joiningRoom) {
            selectedIndex = selected;
            joiningRoom = true;
        }
    } else {
        const char *msg = "Obteniendo salas...";
        Vector2 size = MeasureTextEx(App.assets.mainFont, msg, 20, 2);
        DrawTextEx(App.assets.mainFont, msg,
                   (Vector2){(GetScreenWidth() - size.x) / 2, GetScreenHeight() / 2},
                   20, 2, WHITE);
    }

    Button backButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, GetScreenHeight() - 80, 200, 60},
        .text = "Regresar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = joiningRoom ? GRAY : WHITE,
        .standbyColor = LIME,
        .hoverColor = GRAY
    };

    bool pressed = DrawButton(backButton);

    if (pressed && !joiningRoom) {
        InitMenu();
        App.currentScreen = MENU_SCREEN;
    }
}
