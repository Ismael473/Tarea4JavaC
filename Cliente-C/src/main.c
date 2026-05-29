#include "stdio.h"

#include "raylib.h"
#include "ui/states.h"
#include "ui/menu.h"


int main() {
    SetConfigFlags(FLAG_MSAA_4X_HINT);
    InitWindow(800, 450, "SpaCE Invaders");
    SetTargetFPS(60);

    MainFont = LoadFontEx(
        "assets/fonts/PressStart2P-Regular.ttf",
        64,
        0,
        0
    );

    Logo = LoadTexture("assets/ui/logo.png");

    InitStars();

    while (!WindowShouldClose()) {
        switch (CurrentScreen)
        {
        case MENU_SCREEN:
            UpdateMenu();
            break;

        default:
            break;
        }

        /*
        Dibujar el siguiente frame
        */

        BeginDrawing();

        switch (CurrentScreen)
        {
        case MENU_SCREEN:
            DrawMenu();
            break;
        
        default:
            break;
        }

        EndDrawing();
    }

    CloseWindow();
    return 0;
}