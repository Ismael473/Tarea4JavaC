#include "stdio.h"

#include "raylib.h"
#include "ui/states.h"
#include "ui/menu.h"
#include "ui/handshake.h"

#include <uuid/uuid.h>


int main() {
    SetConfigFlags(FLAG_MSAA_4X_HINT);
    InitWindow(800, 450, "SpaCE Invaders");
    SetTargetFPS(60);

    uuid_t uuid;
    uuid_generate(uuid);
    uuid_unparse(uuid, App.client.uuid);

    App.assets.mainFont = LoadFontEx(
        "assets/fonts/PressStart2P-Regular.ttf",
        64,
        0,
        0
    );

    App.assets.logo = LoadTexture("assets/ui/logo.png");

    while (!WindowShouldClose()) {
        switch (App.currentScreen)
        {
        case HANDSHAKE_SCREEN:
            UpdateHandshake();
            break;
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

        switch (App.currentScreen)
        {
        case HANDSHAKE_SCREEN:
            DrawHandshake();
            break;
        case MENU_SCREEN:
            DrawMenu();
            break;
        
        default:
            break;
        }

        EndDrawing();
    }

    UnloadTexture(App.assets.logo);
    UnloadFont(App.assets.mainFont);
    CloseWindow();
    return 0;
}
