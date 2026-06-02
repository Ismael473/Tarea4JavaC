#include "ui/game.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/text.h"
#include "raylib.h"

void InitGame() {
}

void UpdateGame() {
    UpdateStars();
}

void DrawGame() {
    ClearBackground(BLACK);
    DrawStars();

    Text scoreLabel = {
        .text = "Puntaje: 0",
        .position = (Vector2){20, 20},
        .fontSize = 20,
        .spacing = 2,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_TOP_LEFT,
        .opacity = 1.0f
    };

    DrawUIText(scoreLabel);

    Text livesLabel = {
        .text = "Vidas: 3",
        .position = (Vector2){GetScreenWidth() - 20, 20},
        .fontSize = 20,
        .spacing = 2,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_TOP_RIGHT,
        .opacity = 1.0f
    };

    DrawUIText(livesLabel);
}
