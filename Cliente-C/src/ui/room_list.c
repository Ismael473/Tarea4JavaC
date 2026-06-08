#include "ui/room_list.h"
#include "ui/states.h"

#include <string.h>
#include <stdio.h>

void InitRoomList(RoomList *list, Rectangle bounds) {
    list->bounds = bounds;
    list->roomCount = 0;
    list->scrollOffset = 0;
    list->itemHeight = 40;
}

void AddRoom(RoomList *list, const char *name, const char *roomId, int playerCount, int spectatorCount) {
    if (list->roomCount >= MAX_ROOMS) {
        return;
    }

    Room *room = &list->rooms[list->roomCount++];
    if (name) {
        strncpy(room->name, name, sizeof(room->name) - 1);
        room->name[sizeof(room->name) - 1] = '\0';
    } else {
        room->name[0] = '\0';
    }
    if (roomId) {
        strncpy(room->roomId, roomId, sizeof(room->roomId) - 1);
        room->roomId[sizeof(room->roomId) - 1] = '\0';
    } else {
        room->roomId[0] = '\0';
    }
    room->playerCount = playerCount;
    room->spectatorCount = spectatorCount;
}

void ClearRoomList(RoomList *list) {
    list->roomCount = 0;
    list->scrollOffset = 0;
}

int DrawRoomList(RoomList *list) {
    Vector2 mousePos = GetMousePosition();

    bool mouseInBounds = CheckCollisionPointRec(mousePos, list->bounds);

    if (mouseInBounds) {
        list->scrollOffset += GetMouseWheelMove() * list->itemHeight;
    }

    float contentHeight = list->roomCount * list->itemHeight;
    float maxScroll = contentHeight - list->bounds.height;
    if (maxScroll < 0) maxScroll = 0;
    if (list->scrollOffset > 0) list->scrollOffset = 0;
    if (list->scrollOffset < -maxScroll) list->scrollOffset = -maxScroll;

    DrawRectangleRec(list->bounds, Fade(GRAY, 0.5f));

    BeginScissorMode(
        list->bounds.x,
        list->bounds.y,
        list->bounds.width,
        list->bounds.height
    );

    int selectedRoom = -1;
    for (int i = 0; i < list->roomCount; i++) {
        Rectangle itemRect = {
            list->bounds.x,
            list->bounds.y + i * list->itemHeight + list->scrollOffset,
            list->bounds.width,
            list->itemHeight
        };

        bool itemVisible = itemRect.y + itemRect.height > list->bounds.y && itemRect.y < list->bounds.y + list->bounds.height;

        if (!itemVisible) {
            continue;
        }

        bool hovered = CheckCollisionPointRec(mousePos, itemRect);

        if (hovered && IsMouseButtonPressed(MOUSE_LEFT_BUTTON)) {
            selectedRoom = i;
        }

        DrawRectangleRec(
            itemRect,
            hovered ? Fade(LIGHTGRAY, 0.7f) : Fade(LIGHTGRAY, 0.5f)
        );

        DrawTextEx(
            App.assets.mainFont,
            list->rooms[i].name,
            (Vector2){itemRect.x + 10, itemRect.y + 10},
            20,
            2,
            WHITE
        );

        char info[64];
        snprintf(info, sizeof(info), "%dj %de", list->rooms[i].playerCount, list->rooms[i].spectatorCount);

        Vector2 infoSize = MeasureTextEx(App.assets.mainFont, info, 20, 2);
        DrawTextEx(
            App.assets.mainFont,
            info,
            (Vector2){itemRect.x + itemRect.width - infoSize.x - 10, itemRect.y + 10},
            20,
            2,
            WHITE
        );
    }

    EndScissorMode();

    return selectedRoom;
}