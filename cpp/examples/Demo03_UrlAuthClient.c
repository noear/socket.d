/*
 * Url Auth Client
 *
 * @build   make
 * @client  bin/Demo03_UrlAuthClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);
int on_close(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = on_close,
    .onmessage = 0,
    .onerror = 0,
};

int on_connack(sd_session_t* session, sd_message_t* message) {
    // send hello message
    session_send_string(session, message->sid, "/demo", "Hi!");
    return 0;
}

int on_close(sd_session_t* session, sd_message_t* message) {
    if (message && message->entity.meta) {
        const char* code = sd_meta(&message->entity, "code");
        if (code) {
            printf("Client closing. code=%s\n", code);
        }
    }
        
    return 0;
}

int main(int argc, char** argv) {
    //会成功
    //sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/?u=noear&p=2");
    //if (cli == 0) {
    //    printf("sd_create_tcp_client() error!\n");
    //    return -1;
    //}
    //sd_regist_client(cli, onevent);
    //sd_start_tcp_client(cli);
    //sd_destory_tcp_client(cli);

    //TODO: support muti client
    //会失败
    sd_client_t cli2 = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/?u=solon&p=1");
    if (cli2 == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_regist_client(cli2, onevent);
    sd_start_tcp_client(cli2);
    sd_destory_tcp_client(cli2);

    return 0;
}
