/*
 * helloworld tcp server
 *
 * @build   make
 * @server  bin/helloworld_tcpserver
 */
#include <stdio.h>
#include "tcp_server.h"

int on_open(const sd_session_t* session, const void* message);
int on_message(const sd_session_t* session, const void* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = 0,
    .onmessage = on_message,
    .onerror = 0,
};

int on_open(const sd_session_t* session, const void* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    return 0;
}

int on_message(const sd_session_t* session, const void* message) {
    const sd_message_t* msg = (const sd_message_t*)message;
    if (msg && msg->entity.data) {
        printf("%s, You too!\n", msg->entity.data);
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
