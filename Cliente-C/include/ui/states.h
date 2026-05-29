#ifndef STATES_H
#define STATES_H

#include "raylib.h"

typedef enum {
    MENU_SCREEN,
    GAME_SCREEN,
} Screen;

extern Screen CurrentScreen;

extern Font MainFont;

extern Texture2D Logo;

#endif