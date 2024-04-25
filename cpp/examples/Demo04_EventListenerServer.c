/*
 * Send server
 *
 * @build   make
 * @server  bin/Demo04_EventListenerServer
 */
#include <stdio.h>
#include "tcp_server.h"

int on_open(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = 0,
    .onmessage = on_message,
    .onerror = 0,
};

static void session_send_string(sd_session_t* session, const char* sid, const char* event, const char* data) {
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    string_entity_data(&entity, data);
    sd_send_message(sid, event, &entity, hio);
    free_entity_meta_and_data(&entity);
}

int on_open(sd_session_t* session, sd_message_t* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.data) {
        session_send_string(session, message->sid, "/demo1", "Me too!");
        session_send_string(session, message->sid, "/demo2", "Me too!");
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
