/*
 * Event Listener Client
 *
 * @build   make
 * @client  bin/Demo04_EventListenerClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);

int on_event_demo(sd_session_t* session, sd_message_t* message);
int on_event_demo2(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = on_message,
    .onerror = 0,
};

static event_handler_t message_event_handler_table[] = {
    {"/demo", on_event_demo},
    {"/demo2", on_event_demo2},
};

int on_event_demo(sd_session_t* session, sd_message_t* message) {
    printf("on::%s::%s\n", message->event, message->entity.data);
    return 0;
}

int on_event_demo2(sd_session_t* session, sd_message_t* message) {
    printf("on::%s::%s\n", message->event, message->entity.data);
    return 0;
}

int on_connack(sd_session_t* session, sd_message_t* message) {
    session_send_string(session, message->sid, "/order", "Hi");
    session_send_string(session, message->sid, "/user", "Hi");
    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    const char* event = message->event;
    if (event && strlen(event) > 0) {
        printf("client::event=%s;sid=%s;", message->event, message->sid);
        if (message->entity.data)
            printf("data=%s\n", message->entity.data);

        int n = sizeof(message_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, message_event_handler_table[i].name) == 0) {
                message_event_handler_table[i].fn(session, message);
                break;
            }
        }
    }

    return 0;
}

int main(int argc, char** argv) {
    sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/?u=a&p=2");
    if (cli == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_regist_client(cli, onevent);
    sd_start_tcp_client(cli);
    sd_destory_tcp_client(cli);
    return 0;
}
