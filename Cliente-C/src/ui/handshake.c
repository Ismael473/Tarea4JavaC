#include "raylib.h"
#include <pthread.h>
#include <math.h>

#include "ui/states.h"
#include "ui/menu.h"
#include "ui/handshake.h"
#include "network/subscribe.h"

pthread_t subscriptionThread;
bool subscriptionAttempted = false;

static void *SubscriptionThread(void *arg) {
    bool ok = SubscribeToServer();

    App.client.subscriptionSuccess = ok;
    App.client.subscriptionFinished = true;

    return NULL;
}

void UpdateHandshake() {
    if (!subscriptionAttempted) {
        subscriptionAttempted = true;
        pthread_create(&subscriptionThread, NULL, SubscriptionThread, NULL);
    }

    if(App.client.subscriptionFinished) {
        pthread_join(subscriptionThread, NULL);

        if(App.client.subscriptionSuccess) {
            App.client.subscribed = true;
            InitMenu();
            App.currentScreen = MENU_SCREEN;
        } else {
            subscriptionAttempted = false;
            App.client.subscriptionFinished = false;
        }
    }
}

void DrawHandshake() {
    ClearBackground(BLACK);

    const char *Text = "Connecting to the server...";

    float Time = GetTime();
    float Alpha = (sinf(Time * 3.0f) + 1.0f) / 2.0f;

    float FontSize = 20.0f;
    float Spacing = 2.0f;

    Vector2 TextSize = MeasureTextEx(App.assets.mainFont, Text, FontSize, Spacing);
    Vector2 TextPos = { (GetScreenWidth() - TextSize.x) / 2, (GetScreenHeight() - TextSize.y) / 2 };
    DrawTextEx(
        App.assets.mainFont,
        Text,
        TextPos,
        FontSize,
        Spacing,
        Fade(WHITE, Alpha)
    );
}
