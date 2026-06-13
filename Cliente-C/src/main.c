#include "stdio.h"

#include "raylib.h"
#include "ui/states.h"
#include "ui/stars.h"
#include "ui/menu.h"
#include "ui/handshake.h"
#include "ui/rooms.h"
#include "ui/game.h"
#include "ui/spectate.h"

#include <uuid/uuid.h>
#include "network/connection.h"

#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <string.h>


int main() {
    SetConfigFlags(FLAG_MSAA_4X_HINT);
    InitWindow(800, 550, "SpaCE Invaders");
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

    App.client.lastChar = 0;
    App.client.serialPort = open("/dev/ttyACM0", O_RDONLY | O_NOCTTY);

    if (App.client.serialPort < 0) {
        printf("[SERIAL] Error abriendo puerto\n");
    }

    struct termios tty;

    if (tcgetattr(App.client.serialPort, &tty) != 0) {
        printf("[SERIAL] Error en tcgetattr\n");
    }

    cfsetispeed(&tty, B115200);
    cfsetospeed(&tty, B115200);
    cfmakeraw(&tty);

    tty.c_cflag |= CREAD | CLOCAL;
    tty.c_cc[VMIN] = 0;
    tty.c_cc[VTIME] = 1;

    if (tcsetattr(App.client.serialPort, TCSANOW, &tty) != 0) {
        printf("[SERIAL] Error en tcsetattr\n");
    }

    App.client.controlThreadShouldClose = true;

    InitStars();

    while (!WindowShouldClose() && !App.shouldClose) {

        switch (App.currentScreen)
        {
        case HANDSHAKE_SCREEN:
            UpdateHandshake();
            break;
        case MENU_SCREEN:
            UpdateMenu();
            break;
        case ROOMS_SCREEN:
            UpdateRooms();
            break;
        case GAME_SCREEN:
            UpdateGame();
            break;
        case SPECTATE_SCREEN:
            UpdateSpectate();
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
        case ROOMS_SCREEN:
            DrawRooms();
            break;
        case GAME_SCREEN:
            DrawGame();
            break;
        case SPECTATE_SCREEN:
            DrawSpectate();
            break;
        default:
            break;
        }

        EndDrawing();
    }

    ConnectionClose();

    UnloadTexture(App.assets.logo);
    UnloadFont(App.assets.mainFont);
    CloseWindow();
    return 0;
}
