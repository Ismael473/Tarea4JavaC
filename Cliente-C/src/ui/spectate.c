#include "ui/spectate.h"
#include "ui/stars.h"
#include "ui/states.h"
#include "ui/menu.h"
#include "ui/text.h"
#include "network/connection.h"
#include "cJSON.h"
#include "raylib.h"
#include <string.h>
#include <stdio.h>

#define MAX_SPRITE_ID 29
#define MAX_FRAME_ENTITIES 256
#define GAME_SCALE 2.0f

static int GAME_OFFSET_X;
static int GAME_OFFSET_Y;

static Texture2D sprites[MAX_SPRITE_ID];
static bool spritesLoaded = false;

typedef struct {
    int x;
    int y;
    int sprite;
    char animation[64];
} FrameEntity;

static struct {
    int score;
    int lives;
    char event[64];
    int eventTimer;
    bool started;
    int entityCount;
    FrameEntity entities[MAX_FRAME_ENTITIES];
} currentFrame;

static const char *spriteFiles[MAX_SPRITE_ID] = {
    "assets/sprites/player_cannon.PNG",
    "assets/sprites/aliens_sprites/crab_1_isolated.PNG",
    "assets/sprites/aliens_sprites/crab_2_isolated.PNG",
    "assets/sprites/aliens_sprites/octopus_1_isolated.PNG",
    "assets/sprites/aliens_sprites/octopus_2_isolated.PNG",
    "assets/sprites/aliens_sprites/squid_1_isolated.PNG",
    "assets/sprites/aliens_sprites/squid_2_isolated.PNG",
    "assets/sprites/bullets/bullet_player.PNG",
    "assets/sprites/bullets/bullet_a_0.PNG",
    "assets/sprites/bullets/bullet_a_1.PNG",
    "assets/sprites/bullets/bullet_a_2.PNG",
    "assets/sprites/bullets/bullet_a_3.PNG",
    "assets/sprites/bullets/bullet_b_0.PNG",
    "assets/sprites/bullets/bullet_b_1.PNG",
    "assets/sprites/bullets/bullet_b_2.PNG",
    "assets/sprites/bullets/bullet_b_3.PNG",
    "assets/sprites/bullets/bullet_c_0.PNG",
    "assets/sprites/bullets/bullet_c_1.PNG",
    "assets/sprites/bullets/bullet_c_2.PNG",
    "assets/sprites/bullets/bullet_c_3.PNG",
    "assets/sprites/bunker_sprites/bunker_sprite_bottom_left.PNG",
    "assets/sprites/bunker_sprites/bunker_sprite_bottom_right.PNG",
    "assets/sprites/bunker_sprites/bunker_sprite_full.PNG",
    "assets/sprites/bunker_sprites/bunker_sprite_top_left.PNG",
    "assets/sprites/bunker_sprites/bunker_sprite_top_right.PNG",
    "assets/sprites/explotions/player_explotion_0.PNG",
    "assets/sprites/explotions/player_explotion_1.PNG",
    "assets/sprites/explotions/alien_bullet_explotion.PNG",
    "assets/sprites/explotions/alien_explotion.PNG",
};

#define MAX_DEATH_EFFECTS 64
#define DEATH_EFFECT_DURATION 10

typedef struct {
    int x;
    int y;
    char type[32];
    int timer;
} DeathEffect;

static DeathEffect deathEffects[MAX_DEATH_EFFECTS];
static int deathEffectCount = 0;

static int ToScreenX(int gameX) {
    return GAME_OFFSET_X + (int)(gameX * GAME_SCALE);
}

static int ToScreenY(int gameY) {
    return GAME_OFFSET_Y + (int)(gameY * GAME_SCALE);
}

static void DrawSprite(Texture2D tex, int gameX, int gameY) {
    Vector2 pos = { (float)ToScreenX(gameX), (float)ToScreenY(gameY) };
    DrawTextureEx(tex, pos, 0, GAME_SCALE, WHITE);
}

void InitSpectate() {
    GAME_OFFSET_X = (GetScreenWidth() - (int)(256 * GAME_SCALE)) / 2 + 40;
    GAME_OFFSET_Y = (GetScreenHeight() - (int)(256 * GAME_SCALE)) / 2;
    for (int i = 0; i < MAX_SPRITE_ID; i++) {
        sprites[i] = LoadTexture(spriteFiles[i]);
        SetTextureFilter(sprites[i], TEXTURE_FILTER_POINT);
    }
    spritesLoaded = true;
    currentFrame.score = 0;
    currentFrame.lives = 3;
    currentFrame.event[0] = '\0';
    currentFrame.eventTimer = 0;
    currentFrame.started = false;
    currentFrame.entityCount = 0;
    deathEffectCount = 0;
}

void UpdateSpectate() {
    UpdateStars();

    const char *frame = ConnectionGetLatestFrame();
    if (!frame || frame[0] == '\0') return;

    cJSON *root = cJSON_Parse(frame);
    if (!root) return;

    cJSON *scoreItem = cJSON_GetObjectItem(root, "score");
    if (scoreItem) currentFrame.score = scoreItem->valueint;

    cJSON *livesItem = cJSON_GetObjectItem(root, "lives");
    if (livesItem) currentFrame.lives = livesItem->valueint;

    cJSON *eventItem = cJSON_GetObjectItem(root, "enter_game_state_event");
    if (eventItem && eventItem->valuestring) {
        if (eventItem->valuestring[0] != '\0') {
            strncpy(currentFrame.event, eventItem->valuestring, sizeof(currentFrame.event) - 1);
            currentFrame.event[sizeof(currentFrame.event) - 1] = '\0';
            currentFrame.eventTimer = 120;
            if (strcmp(eventItem->valuestring, "game_over") == 0) {
                currentFrame.started = false;
            }
        } else if (currentFrame.eventTimer > 0) {
            currentFrame.eventTimer--;
        }
    }

    cJSON *enemies = cJSON_GetObjectItem(root, "enemies");
    currentFrame.entityCount = 0;
    if (enemies && cJSON_IsArray(enemies)) {
        int arraySize = cJSON_GetArraySize(enemies);
        int count = arraySize < MAX_FRAME_ENTITIES ? arraySize : MAX_FRAME_ENTITIES;
        if (count > 0) currentFrame.started = true;
        for (int i = 0; i < count; i++) {
            cJSON *item = cJSON_GetArrayItem(enemies, i);
            if (!item) continue;

            cJSON *xItem = cJSON_GetObjectItem(item, "x");
            cJSON *yItem = cJSON_GetObjectItem(item, "y");
            cJSON *spriteItem = cJSON_GetObjectItem(item, "sprite");
            cJSON *animItem = cJSON_GetObjectItem(item, "animation");

            if (!xItem || !yItem || !spriteItem) continue;

            currentFrame.entities[i].x = xItem->valueint;
            currentFrame.entities[i].y = yItem->valueint;
            currentFrame.entities[i].sprite = spriteItem->valueint;
            if (animItem && animItem->valuestring) {
                strncpy(currentFrame.entities[i].animation, animItem->valuestring,
                        sizeof(currentFrame.entities[i].animation) - 1);
                currentFrame.entities[i].animation[sizeof(currentFrame.entities[i].animation) - 1] = '\0';
                if (animItem->valuestring[0] != '\0' && deathEffectCount < MAX_DEATH_EFFECTS) {
                    DeathEffect *de = &deathEffects[deathEffectCount++];
                    de->x = xItem->valueint;
                    de->y = yItem->valueint;
                    strncpy(de->type, animItem->valuestring, sizeof(de->type) - 1);
                    de->type[sizeof(de->type) - 1] = '\0';
                    de->timer = DEATH_EFFECT_DURATION;
                }
            } else {
                currentFrame.entities[i].animation[0] = '\0';
            }
            currentFrame.entityCount++;
        }
    }

    for (int i = deathEffectCount - 1; i >= 0; i--) {
        deathEffects[i].timer--;
        if (deathEffects[i].timer <= 0) {
            deathEffects[i] = deathEffects[--deathEffectCount];
        }
    }

    cJSON_Delete(root);

    if (strcmp(currentFrame.event, "game_over") == 0 && currentFrame.eventTimer <= 0) {
        cJSON *leave = cJSON_CreateObject();
        cJSON_AddStringToObject(leave, "type", "leave_room");
        cJSON_AddStringToObject(leave, "clientId", App.client.uuid);
        char *leaveStr = cJSON_PrintUnformatted(leave);
        cJSON_Delete(leave);
        if (leaveStr) {
            char resp[1024];
            ConnectionSendAndReadResponse(leaveStr, resp, sizeof(resp));
            cJSON_free(leaveStr);
        }
        App.currentScreen = MENU_SCREEN;
        InitMenu();
    }
}

void DrawSpectate() {
    ClearBackground(BLACK);
    DrawStars();

    for (int i = 0; i < currentFrame.entityCount; i++) {
        FrameEntity *ent = &currentFrame.entities[i];
        int spriteId = ent->sprite;
        if (spriteId < 0 || spriteId >= MAX_SPRITE_ID) continue;

        bool isDying = false;
        for (int j = 0; j < deathEffectCount; j++) {
            if (deathEffects[j].x == ent->x && deathEffects[j].y == ent->y &&
                strcmp(deathEffects[j].type, ent->animation) == 0) {
                isDying = true;
                break;
            }
        }
        if (isDying) continue;

        Texture2D *tex = &sprites[spriteId];
        if (tex->id == 0) continue;

        DrawSprite(*tex, ent->x, ent->y);
    }

    for (int i = 0; i < deathEffectCount; i++) {
        DeathEffect *de = &deathEffects[i];
        int spriteId;
        if (strcmp(de->type, "player_death_animation") == 0) {
            int frame = (DEATH_EFFECT_DURATION - de->timer) % 2;
            spriteId = 25 + frame;
        } else {
            spriteId = 28;
        }
        if (spriteId < 0 || spriteId >= MAX_SPRITE_ID) continue;

        Texture2D *tex = &sprites[spriteId];
        if (tex->id == 0) continue;

        DrawSprite(*tex, de->x, de->y);
    }

    char scoreBuf[32];
    snprintf(scoreBuf, sizeof(scoreBuf), "Puntaje: %d", currentFrame.score);
    Text scoreLabel = {
        .text = scoreBuf,
        .position = (Vector2){20, 20},
        .fontSize = 20,
        .spacing = 2,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_TOP_LEFT,
        .opacity = 1.0f
    };
    DrawUIText(scoreLabel);

    char livesBuf[32];
    snprintf(livesBuf, sizeof(livesBuf), "Vidas: %d", currentFrame.lives);
    Text livesLabel = {
        .text = livesBuf,
        .position = (Vector2){GetScreenWidth() - 20, 20},
        .fontSize = 20,
        .spacing = 2,
        .color = WHITE,
        .anchor = TEXT_ANCHOR_TOP_RIGHT,
        .opacity = 1.0f
    };
    DrawUIText(livesLabel);

    Text spectatingLabel = {
        .text = "ESPECTANDO",
        .position = (Vector2){GetScreenWidth() / 2, 46},
        .fontSize = 12,
        .spacing = 2,
        .color = (Color){ 200, 200, 50, 255 },
        .anchor = TEXT_ANCHOR_TOP_CENTER,
        .opacity = 1.0f
    };
    DrawUIText(spectatingLabel);

    if (currentFrame.eventTimer > 0) {
        if (strcmp(currentFrame.event, "game_over") == 0) {
            const char *msg = "GAME OVER";
            int fontSize = 40;
            int textWidth = MeasureTextEx(App.assets.mainFont, msg, fontSize, 3).x;
            Text gameOverText = {
                .text = msg,
                .position = (Vector2){(GetScreenWidth() - textWidth) / 2, GetScreenHeight() / 2 - 40},
                .fontSize = fontSize,
                .spacing = 3,
                .color = RED,
                .anchor = TEXT_ANCHOR_TOP_LEFT,
                .opacity = 1.0f
            };
            DrawUIText(gameOverText);
        }

        if (strcmp(currentFrame.event, "win_animation") == 0) {
            const char *msg = "Ronda completada!";
            int fontSize = 24;
            int textWidth = MeasureTextEx(App.assets.mainFont, msg, fontSize, 2).x;
            Text winText = {
                .text = msg,
                .position = (Vector2){(GetScreenWidth() - textWidth) / 2, GetScreenHeight() / 2},
                .fontSize = fontSize,
                .spacing = 2,
                .color = GREEN,
                .anchor = TEXT_ANCHOR_TOP_LEFT,
                .opacity = 1.0f
            };
            DrawUIText(winText);
        }

        if (strcmp(currentFrame.event, "main_menu") == 0) {
            const char *msg = "Presiona una tecla para comenzar";
            int fontSize = 16;
            int textWidth = MeasureTextEx(App.assets.mainFont, msg, fontSize, 2).x;
            Text menuText = {
                .text = msg,
                .position = (Vector2){(GetScreenWidth() - textWidth) / 2, GetScreenHeight() / 2},
                .fontSize = fontSize,
                .spacing = 2,
                .color = YELLOW,
                .anchor = TEXT_ANCHOR_TOP_LEFT,
                .opacity = 1.0f
            };
            DrawUIText(menuText);
        }
    }

    if (!currentFrame.started && currentFrame.eventTimer == 0) {
        const char *msg = "Esperando jugadores...";
        int fontSize = 16;
        int textWidth = MeasureTextEx(App.assets.mainFont, msg, fontSize, 2).x;
        Text menuText = {
            .text = msg,
            .position = (Vector2){(GetScreenWidth() - textWidth) / 2, GetScreenHeight() / 2},
            .fontSize = fontSize,
            .spacing = 2,
            .color = YELLOW,
            .anchor = TEXT_ANCHOR_TOP_LEFT,
            .opacity = 1.0f
        };
        DrawUIText(menuText);
    }
}
