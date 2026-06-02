#include "ui/menu.h"
#include "raylib.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/button.h"
#include "ui/text.h"
#include "ui/rooms.h"
#include <stdlib.h>
#include <stdio.h>

void InitMenu() {
}

void UpdateMenu() {
    UpdateStars();
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
        .text = "Jugar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = WHITE,
        .standbyColor = LIME,
        .hoverColor = GRAY
    };

    bool pressed = DrawButton(PlayButton);

    if (pressed) {
        App.currentScreen = GAME_SCREEN;
    }

    Button SpecButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, (GetScreenHeight() / 2) + 90, 200, 60},
        .text = "Espectar",
        .fontSize = 20,
        .spacing = 2,
        .textColor = WHITE,
        .standbyColor = LIME,
        .hoverColor = GRAY
    };

    pressed = DrawButton(SpecButton);

    if (pressed) {
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

    if (pressed) {
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
