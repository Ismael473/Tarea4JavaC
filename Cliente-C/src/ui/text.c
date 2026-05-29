#include "ui/text.h"
#include "ui/states.h"

static float ClampOpacity(float opacity) {
    if (opacity < 0.0f) return 0.0f;
    if (opacity > 1.0f) return 1.0f;
    return opacity;
}

Vector2 MeasureUIText(Text text) {
    Vector2 size = MeasureTextEx(
        App.assets.mainFont,
        text.text,
        text.fontSize,
        text.spacing
    );

    return size;
}

void DrawUIText(Text text) {
    Vector2 size = MeasureUIText(text);

    Vector2 pos = text.position;

    switch (text.anchor) {
        case TEXT_ANCHOR_TOP_CENTER:
            pos.x -= size.x / 2;
            break;
        case TEXT_ANCHOR_TOP_RIGHT:
            pos.x -= size.x;
            break;
        case TEXT_ANCHOR_CENTER_LEFT:
            pos.y -= size.y / 2;
            break;
        case TEXT_ANCHOR_CENTER:
            pos.x -= size.x / 2;
            pos.y -= size.y / 2;
            break;
        case TEXT_ANCHOR_CENTER_RIGHT:
            pos.x -= size.x;
            pos.y -= size.y / 2;
            break;
        case TEXT_ANCHOR_BOTTOM_LEFT:
            pos.y -= size.y;
            break;
        case TEXT_ANCHOR_BOTTOM_CENTER:
            pos.x -= size.x / 2;
            pos.y -= size.y;
            break;
        case TEXT_ANCHOR_BOTTOM_RIGHT:
            pos.x -= size.x;
            pos.y -= size.y;
            break;
        default:
            break;
    }

    DrawTextEx(
        App.assets.mainFont,
        text.text,
        pos,
        text.fontSize,
        text.spacing,
        Fade(text.color, ClampOpacity(text.opacity))
    );
}