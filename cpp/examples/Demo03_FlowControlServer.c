/*
 * Flow Control tcp server
 *
 * @build   make
 * @server  bin/Demo03_FlowControlServer
 */
#include <stdio.h>
#include "tcp_server.h"

int on_open(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);
int on_subscribe(sd_session_t* session, sd_message_t* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = 0,
    .onmessage = on_message,
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

int on_message(sd_session_t* session, sd_message_t* message) {
    if (message && strncmp(message->event, "/demo", sizeof(message->event)) == 0) {
        sd_entity_t entity = { 0 };
        init_entity(&entity);
        string_entity_data(&entity, "此事件只支持订阅模式");
        sd_send_alarm(session->sid, "/demo", &entity, session->channle->hio);
        free_entity_meta_and_data(&entity);
        return 0;
    }

    return 0;
}

int on_subscribe(sd_session_t* session, sd_message_t* message) {
    if (message) {
        const char* video_id = sd_meta(&message->entity, "videoId");
        int range_start = sd_meta_as_int(&message->entity, META_RANGE_START);
        int range_size = sd_meta_as_int(&message->entity, META_RANGE_SIZE);
        const char* repsid = message->sid;

        printf("videoId=%s; Data-Range-Start=%d; Data-Range-Size=%d\n", 
            video_id, range_start, range_size);

        if (video_id == 0 || range_size == 0) {
            sd_entity_t entity = { 0 };
            init_entity(&entity);
            string_entity_data(&entity, "参数不合规");
            sd_send_alarm(repsid, "/demo", &entity, session->channle->hio);
            free_entity_meta_and_data(&entity);
            return 0;
        }

        for (int i = 0; i < range_size; i++) {
            const int bufsize = 10;
            char* buffer = malloc(bufsize);
            memset(buffer, '0', bufsize);

            sd_entity_t entity1 = { 0 };
            init_entity(&entity1);
            entity1.data = buffer;
            entity1.datalen = bufsize;            
            sd_send_replay(repsid, "/demo", &entity1, session->channle->hio);
            free_entity_meta_and_data(&entity1);
        }

        sd_entity_t entity2 = { 0 };
        init_entity(&entity2);
        sd_send_endreplay(repsid, "/demo", &entity2, session->channle->hio);
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
