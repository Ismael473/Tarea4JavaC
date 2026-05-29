#ifndef TEXT_H
#define TEXT_H

#include "raylib.h"

typedef enum {
    TEXT_ANCHOR_TOP_LEFT,
    TEXT_ANCHOR_TOP_CENTER,
    TEXT_ANCHOR_TOP_RIGHT,
    TEXT_ANCHOR_CENTER_LEFT,
    TEXT_ANCHOR_CENTER,
    TEXT_ANCHOR_CENTER_RIGHT,
    TEXT_ANCHOR_BOTTOM_LEFT,
    TEXT_ANCHOR_BOTTOM_CENTER,
    TEXT_ANCHOR_BOTTOM_RIGHT
} TextAnchor;

typedef struct {
    const char *text;
    Vector2 position;
    float fontSize;
    float spacing;
    Color color;
    float opacity;
    TextAnchor anchor;
} Text;

Vector2 MeasureUIText(Text text);
void DrawUIText(Text text);

#endif