/*
 * Send and subscribe tcp client
 *
 * @build   make
 * @client  bin/Demo02_SendAndSubscribeClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);
int on_replay(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = 0,
    .onreplay = on_replay,
    .onerror = 0,
};

int on_connack(sd_session_t* session, sd_message_t* message) {
    void* hio = sd_hio(session);

    // send and subscribe
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    string_entity_data(&entity, "hello wrold!");
    sd_send_subscribe(session->sid, "/demo", &entity, hio);
    printf("Send and subscribe: %s\n", entity.data);
    free_entity_meta_and_data(&entity);

    return 0;
}

int on_replay(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.data) {
        printf("Replay: %s\n", message->entity.data);
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
