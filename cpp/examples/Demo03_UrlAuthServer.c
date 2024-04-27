/*
 * Url Auth Server
 *
 * @build   make
 * @server  bin/Demo03_UrlAuthServer
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

int on_open(sd_session_t* session, sd_message_t* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    const char* user = sd_param(session, "u");
    if (user == 0 || strcmp(user, "noear") != 0) { //如果不是 noear，关闭会话
        close_session(session, message->sid, message->event);
    }

    return 0;
}

int on_message(sd_session_t* session, sd_message_t* msg) {
    if (msg && msg->entity.data) {
        printf("Recv: %s\n", msg->entity.data);
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
