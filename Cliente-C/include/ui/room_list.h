#ifndef ROOM_LIST_H
#define ROOM_LIST_H

#include "raylib.h"
#include <stdbool.h>

#define MAX_ROOMS 10

typedef struct {
    char name[256];
    char roomId[37];
    int playerCount;
    int spectatorCount;
} Room;

typedef struct {
    Rectangle bounds;
    Room rooms[MAX_ROOMS];
    int roomCount;
    float scrollOffset;
    float itemHeight;
} RoomList;

void InitRoomList(RoomList *list, Rectangle bounds);
void AddRoom(RoomList *list, const char *name, const char *roomId, int playerCount, int spectatorCount);
void ClearRoomList(RoomList *list);
int DrawRoomList(RoomList *list);

#endif