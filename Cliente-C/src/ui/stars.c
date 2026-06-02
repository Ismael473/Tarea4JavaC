#include "ui/stars.h"
#include "raylib.h"
#include <stdlib.h>

#define STAR_COUNT 200

typedef struct {
    Vector2 Pos;
    float Speed;
    float Size;
    Color Color;
} Star;

static Star Stars[STAR_COUNT];

static Color StarColors[] = {
    WHITE,
    LIGHTGRAY,
};

static float RandomFloat(float min, float max)
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

void UpdateStars() {
    float dt = GetFrameTime();

    for (int i = 0; i < STAR_COUNT; i++) {
        Stars[i].Pos.y += Stars[i].Speed * dt;

        if (Stars[i].Pos.y > GetScreenHeight()) {
            Stars[i].Pos.y = 0;
            Stars[i].Pos.x = GetRandomValue(0, GetScreenWidth());
        }
    }
}

void DrawStars() {
    for (int i = 0; i < STAR_COUNT; i++) {
        float Alpha = Stars[i].Size / 3.0f;

        DrawCircleV(
            Stars[i].Pos,
            Stars[i].Size,
            Fade(Stars[i].Color, Alpha)
        );
    }
}
