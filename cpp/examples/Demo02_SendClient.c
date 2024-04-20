/*
 * Send client
 *
 * @build   make
 * @client  bin/Demo02_SendClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = 0,
    .onerror = 0,
};

int on_connack(sd_session_t* session, sd_message_t* message) {
    // send hello message
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    sd_put_meta(&entity, "user", "noear");
    sd_put_meta(&entity, "opt", "send");
    string_entity_data(&entity, "Hello world");
    sd_send_message(session->sid, "/demo_send", &entity, hio);
    printf("on_connack: send %s", entity.data);
    free_entity_meta_and_data(&entity);

    return 0;
}

int main(int argc, char** argv) {
    sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/admin?u=noear&p=2");
    if (cli == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_regist_client(cli, onevent);
    sd_start_tcp_client(cli);
    sd_destory_tcp_client(cli);
    return 0;
}
