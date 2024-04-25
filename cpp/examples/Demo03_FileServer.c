/*
 * File tcp server
 *
 * @build   make
 * @server  bin/Demo03_FileServer
 */
#include <stdio.h>
#include "tcp_server.h"

int on_open(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = 0,
    .onmessage = on_message,
    .onrequest = 0,
    .onerror = 0,
};

int on_open(sd_session_t* session, sd_message_t* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    assert(session != NULL);
    assert(message != NULL);

    if (message && message->entity.data) {
        const char* traceid = sd_meta(&message->entity, "Trace-Id");
        const char* filename = sd_meta(&message->entity, META_DATA_DISPOSITION_FILENAME);

        if (traceid) {
            printf("traceid=%s\n", traceid);
        }

        if (filename && strlen(filename) > 0) {
            FILE* file;
            void* data = message->entity.data;
            size_t datalen = message->entity.datalen;

            file = fopen("/Users/noear/Downloads/socketd-upload_2.txt", "wb");
            if (file == NULL) {
                printf("Error opening file.\n");
                return -1;
            }

            printf("Write file: /Users/noear/Downloads/socketd-upload_2.txt.\n");

            fwrite(data, sizeof(char), datalen, file);
            fclose(file);
        }
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
