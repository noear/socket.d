/*
 * MQ Client
 *
 * @build   make
 * @client  bin/Demo05_MqClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = on_message,
    .onreplay = 0,
    .onerror = 0,
};

int mq_subscribe(sd_session_t* session, const char* sid, const char* topic) {
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    sd_put_meta(&entity, "topic", topic);
    sd_send_message(sid, "mq.sub", &entity, hio);
    free_entity_meta_and_data(&entity);

    return 0;
}

int mq_publish(sd_session_t* session, const char* sid, const char* topic, const char* data) {
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    sd_put_meta(&entity, "topic", topic);
    sd_put_meta(&entity, "id", "cf8a85a9-3b36-4c3e-99bb-abfe96b85c80");
    string_entity_data(&entity, data);
    sd_send_message(sid, "mq.push", &entity, hio);
    free_entity_meta_and_data(&entity);

    return 0;
}

int on_connack(sd_session_t* session, sd_message_t* message) {
    mq_subscribe(session, message->sid, "user.created");
    mq_subscribe(session, message->sid, "user.updated");
    mq_publish(session, message->sid, "user.created", "test");

    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.data) {
        const char* topic = sd_meta(&message->entity, "topic");
        if (topic) {
            printf("Recv: topic=%s\n", topic);
            if (strcmp(topic, "user.created") == 0) {
                if (message->entity.data) printf("data=%s\n", message->entity.data);
            }
            else if (strcmp(topic, "user.updated") == 0) {
                if (message->entity.data) printf("data=%s\n", message->entity.data);
            }
        }
    }

    return 0;
}

int main(int argc, char** argv) {
    sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602");
    if (cli == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_regist_client(cli, onevent);
    sd_start_tcp_client(cli);
    sd_destory_tcp_client(cli);
    return 0;
}
