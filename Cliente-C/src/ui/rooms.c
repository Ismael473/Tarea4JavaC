#include "ui/rooms.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/menu.h"
#include "ui/room_list.h"
#include "ui/button.h"
#include "ui/text.h"

static RoomList roomList;

void InitRooms() {
    InitStars();
    InitRoomList(&roomList, (Rectangle) {
        .x = 50,
        .y = 70,
        .width = GetScreenWidth() - 100,
        .height = GetScreenHeight() - 160
    });

    AddRoom(&roomList, "Sala 1", 5);
    AddRoom(&roomList, "Sala 2", 3);
}

void UpdateRooms() {
    UpdateStars();
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

    int selectedRoom = DrawRoomList(&roomList);

    if (selectedRoom != -1) {
        printf("Sala seleccionada: %s\n", roomList.rooms[selectedRoom].name);
    }

    Button backButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, GetScreenHeight() - 80, 200, 60},
        .text = "Regresar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = WHITE,
        .standbyColor = LIME,
        .hoverColor = GRAY
    };

    bool pressed = DrawButton(backButton);

    if (pressed) {
        InitMenu();
        App.currentScreen = MENU_SCREEN;
    }
}