#include "ui/rooms.h"
#include "ui/room_list.h"

static RoomList roomList;

void InitRooms() {
    InitRoomList(&roomList, (Rectangle) {
        .x = 50,
        .y = 100,
        .width = GetScreenWidth() - 100,
        .height = GetScreenHeight() - 150
    });

    AddRoom(&roomList, "Sala 1", 5);
    AddRoom(&roomList, "Sala 2", 3);
}

void UpdateRooms() {
}

void DrawRooms() {
    ClearBackground(BLACK);
    int selectedRoom = DrawRoomList(&roomList);

    if (selectedRoom != -1) {
        printf("Sala seleccionada: %s\n", roomList.rooms[selectedRoom].name);
    }
}