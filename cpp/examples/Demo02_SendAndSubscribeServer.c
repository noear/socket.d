/*
 * Send and subscribe tcp server
 *
 * @build   make
 * @server  bin/Demo02_SendAndSubscribeServer
 */
#include <stdio.h>
#include "tcp_server.h"

int on_open(sd_session_t* session, sd_message_t* message);
int on_subscribe(sd_session_t* session, sd_message_t* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = 0,
    .onmessage = 0,
    .onrequest = 0,
    .onsubscribe = on_subscribe,
    .onerror = 0,
};

int on_open(sd_session_t* session, sd_message_t* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    return 0;
}

int on_subscribe(sd_session_t* session, sd_message_t* message) {
    assert(session != NULL);
    assert(message != NULL);

    if (message && message->entity.data) {
        sd_entity_t entity1 = { 0 };
        init_entity(&entity1);      
        string_entity_data(&entity1, "And you too.");
        sd_send_replay(session->sid, "/demo", &entity1, session->channle->hio);
        free_entity_meta_and_data(&entity1);

        sd_entity_t entity2 = { 0 };
        init_entity(&entity2);
        string_entity_data(&entity2, "Hello world.");
        sd_send_replay(session->sid, "/demo", &entity2, session->channle->hio);
        free_entity_meta_and_data(&entity2);

        sd_entity_t entity3 = { 0 };
        init_entity(&entity3);
        string_entity_data(&entity3, "Welcome.");
        sd_send_endreplay(session->sid, "/demo", &entity3, session->channle->hio);
        free_entity_meta_and_data(&entity3);
    }

    return 0;
}

int main(int argc, char** argv) {
	int port = 8602;

	printf("Starting server on %d ...\n", port);
    sd_server_t server = sd_create_tcp_server(port);
    if (server == 0) {
        printf("sd_create_tcp_server() error!\n");
        return -1;
    }
    sd_regist_server(server, onevent);
    sd_start_tcp_server(server);
    sd_destory_tcp_server(server);

    return 0;
}
