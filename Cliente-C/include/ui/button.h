#ifndef BUTTON_H
#define BUTTON_H

#include "raylib.h"
#include <stdbool.h>

typedef struct {
    Rectangle bounds;
    const char* text;
    Color standbyColor;
    Color hoverColor;
    Color textColor;
    float fontSize;
    float spacing;
} Button;

bool DrawButton(Button button);

#endif