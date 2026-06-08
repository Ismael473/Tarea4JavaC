cmake_minimum_required(VERSION 3.10)

project(SpaceInvaders C)

set(CMAKE_C_STANDARD 99)

include_directories(include)

add_executable(game
    src/main.c
    src/states.c
    src/ui/menu.c
    src/ui/handshake.c
    src/network/subscribe.c
    src/network/unsubscribe.c
    src/network/connection.c
    src/ui/button.c
    src/ui/stars.c
    src/ui/text.c
    src/ui/room_list.c
    src/ui/rooms.c
    src/ui/game.c
    src/ui/spectate.c
    lib/cJSON.c
)

file(COPY assets DESTINATION ${CMAKE_BINARY_DIR})

include_directories(include)

target_link_libraries(game
    raylib
    uuid
    m
    pthread
    dl
    rt
    X11
)
