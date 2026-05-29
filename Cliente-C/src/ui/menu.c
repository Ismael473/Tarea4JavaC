#include "ui/menu.h"
#include "raylib.h"
#include "ui/states.h"
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
        Logo,
        (Vector2){180, 100},
        0.0f,
        0.4f,
        WHITE
    );

    Rectangle PlayButton = {
        300, 200, 200, 60
    };

    bool hover = CheckCollisionPointRec(
        GetMousePosition(),
        PlayButton
    );

    if (hover && IsMouseButtonPressed(MOUSE_LEFT_BUTTON)) {
        printf("Hola");
    }

    DrawRectangleRec(
        PlayButton,
        hover ? DARKGREEN : GRAY
    );
}