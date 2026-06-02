#include "ui/button.h"
#include "ui/states.h"

bool DrawButton(Button button) {
    bool hovered = CheckCollisionPointRec(GetMousePosition(), button.bounds);

    bool pressed = hovered && IsMouseButtonPressed(MOUSE_LEFT_BUTTON);

    Color bgColor;
    if (pressed) {
        bgColor = button.hoverColor;
    } else if (hovered) {
        bgColor = (Color){
            button.standbyColor.r / 2,
            button.standbyColor.g / 2,
            button.standbyColor.b / 2,
            button.standbyColor.a
        };
    } else {
        bgColor = button.standbyColor;
    }

    DrawRectangleRec(button.bounds, bgColor);

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