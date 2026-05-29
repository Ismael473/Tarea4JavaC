#include "ui/menu.h"
#include "raylib.h"
#include "ui/states.h"
#include "ui/button.h"
#include "ui/text.h"
#include "ui/rooms.h"
#include <stdlib.h>
#include <stdio.h>

#define STAR_COUNT 200

typedef struct {
    Vector2 Pos;
    float Speed;
    float Size;
    Color Color;
} Star; 

static Star Stars[STAR_COUNT];

Color StarColors[] = {
    WHITE,
    LIGHTGRAY,
};

float RandomFloat(float min, float max)
{
    return min +
        ((float)rand() / RAND_MAX)
        * (max - min);
}

void InitMenu() {
    InitStars();
}

void InitStars() {
    for (int i = 0; i < STAR_COUNT; i++) {
        Stars[i].Pos = (Vector2) {
            GetRandomValue(0, GetScreenWidth()),
            GetRandomValue(0, GetScreenHeight())
        };

        Stars[i].Size = (float) RandomFloat(1.0f, 1.5f);
        Stars[i].Speed = Stars[i].Size * 50.0f;
        
        int ColorIndex = GetRandomValue(0, 1);
        Stars[i].Color = StarColors[ColorIndex];
    }
}

void UpdateMenu() {
    float dt = GetFrameTime();

    for (int i = 0; i < STAR_COUNT; i++) {
        Stars[i].Pos.y += Stars[i].Speed * dt;

        if (Stars[i].Pos.y > GetScreenHeight()) {
            Stars[i].Pos.y = 0;
            Stars[i].Pos.x = GetRandomValue(0, GetScreenWidth());
        }
    }
}

void DrawMenu() {
    ClearBackground(BLACK);
    for (int i = 0; i < STAR_COUNT; i++) {
        float Alpha = Stars[i].Size / 3.0f;

        DrawCircleV(
            Stars[i].Pos,
            Stars[i].Size,
            Fade(Stars[i].Color, Alpha)
        );
    }

    DrawTextureEx(
        App.assets.logo,
        (Vector2){(GetScreenWidth() - App.assets.logo.width * 0.4f) / 2, (GetScreenHeight() - App.assets.logo.height * 0.4f - 100) / 2},
        0.0f,
        0.4f,
        WHITE
    );

    Button PlayButton = {
        .bounds = {(GetScreenWidth() / 2) - 100, (GetScreenHeight() / 2) + 50, 200, 60},
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
        .bounds = {(GetScreenWidth() / 2) - 100, (GetScreenHeight() / 2) + 120, 200, 60},
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
