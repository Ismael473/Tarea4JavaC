#include "control/control.h"
#include "ui/states.h"
#include <pthread.h>
#include <fcntl.h>
#include <unistd.h>

static pthread_mutex_t serialMutex = PTHREAD_MUTEX_INITIALIZER;

void* SerialThread(){
    while (!App.client.controlThreadShouldClose){
        char c;
        int n = read(App.client.serialPort, &c, 1);

        if (n > 0 && c != '\n' && c != '\r'){
            pthread_mutex_lock(&serialMutex);
            App.client.lastChar = c;
            pthread_mutex_unlock(&serialMutex);
        }
    }
    return NULL;
}

void ControllerInit(){
    pthread_t thId;
    pthread_create(&thId, NULL, SerialThread, NULL);
    pthread_detach(thId);
}

char ControllerGetInput(){
    pthread_mutex_lock(&serialMutex);
    char c = App.client.lastChar;
    App.client.lastChar = 0;
    pthread_mutex_unlock(&serialMutex);
    return c;
}
