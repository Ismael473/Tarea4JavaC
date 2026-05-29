#include "ui/button.h"
#include "ui/states.h"

bool DrawButton(Button button) {
    bool hovered = CheckCollisionPointRec(GetMousePosition(), button.bounds);

    bool pressed = hovered && IsMouseButtonDown(MOUSE_LEFT_BUTTON);

    DrawRectangleRec(
        button.bounds,
        pressed ? button.hoverColor : button.standbyColor
    );

    Vector2 textSize = MeasureTextEx(
        App.assets.mainFont,
        button.text,
        button.fontSize,
        button.spacing
    );

    Vector2 textPos = {
        button.bounds.x + (button.bounds.width - textSize.x) / 2,
        button.bounds.y + (button.bounds.height - textSize.y) / 2
    };

    DrawTextEx(
        App.assets.mainFont,
        button.text,
        textPos,
        button.fontSize,
        button.spacing,
        button.textColor
    );

    return pressed;
}