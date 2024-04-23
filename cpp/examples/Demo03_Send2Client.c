/*
 * Send2 tcp client
 *
 * @build   make
 * @client  bin/Demo03_Send2Client
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);
int on_replay(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = on_message,
    .onreplay = on_replay,
    .onerror = 0,
};

int on_connack(sd_session_t* session, sd_message_t* message) {
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    string_entity_data(&entity, "Hello world");
    sd_send_request(session->sid, "/demo", &entity, hio);
    printf("Send and request: %s", entity.data);
    free_entity_meta_and_data(&entity);

    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.data) {
        printf("client::sid=%s;event=%s;entity=%s\n",
            message->sid, message->event, message->entity.data);

        //加个附件计数
         int count = sd_attr_or_default_as_int(session, "count", 0);
        sd_put_attr_as_int(session, "count", ++count);
        if (count > 5)  return 0;

        void* hio = sd_hio(session);
        sd_entity_t entity = { 0 };
        init_entity(&entity);
        string_entity_data(&entity, "Hi!");
        sd_send_message(session->sid, "/demo", &entity, hio);
        printf("Send message(%d): %s\n", count, entity.data);
        free_entity_meta_and_data(&entity);
    }
    
    return 0;
}

int on_replay(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.data) {
        printf("client::sid=%s;event=%s;entity=%s\n",
            message->sid, message->event, message->entity.data);

        void* hio = sd_hio(session);
        sd_entity_t entity = { 0 };
        init_entity(&entity);
        string_entity_data(&entity, "Hi!");
        sd_send_message(session->sid, "/demo", &entity, hio);
        printf("Send message(%d): %s\n", 0, entity.data);
        free_entity_meta_and_data(&entity);
    }

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
