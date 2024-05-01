/*
 * MQ Server
 *
 * @build   make
 * @server  bin/Demo05_MqServer
 */
#include <stdio.h>
#include "tcp_server.h"

/*user define begin*/
struct list_head userlist;

typedef struct user_s {
    sd_session_t* session;
    struct list_node node;
} user_t;

user_t* new_user(sd_session_t* session) {
    user_t* u = malloc(sizeof(user_t));
    if (u) {
        memset(u, 0, sizeof(user_t));
        u->session = session;
    }
    return u;
}

void delete_user(user_t* u) {
    free(u);
    u = NULL;
}

void user_list_init() {
    list_init(&userlist);
}

void add_user(sd_session_t* session) {
    user_t* u = new_user(session);
    list_add(&u->node, &userlist);
}

void remove_user(sd_session_t* session) {
    struct list_node* node;
    user_t* cur;
    list_for_each(node, &userlist) {
        cur = list_entry(node, user_t, node);
        if (cur->session == session) {
            list_del(&cur->node);
            delete_user(cur);
            break;
        }
    }
}

void for_each_user_list(const char* topic, sd_message_t* message) {
    struct list_node* node;
    user_t* cur;
    list_for_each(node, &userlist) {
        cur = list_entry(node, user_t, node);
        const char* value = sd_attr(cur->session, topic);
        if (value && strcmp(value, "1") == 0) {
            void* hio = sd_hio(cur->session);
            sd_send_message(message->sid, "mq.broadcast", &message->entity, hio);
        }
    }
}
/*user define end*/

int on_open(sd_session_t* session, sd_message_t* message);
int on_close(sd_session_t* session, sd_message_t* message);
int on_message(sd_session_t* session, sd_message_t* message);
int on_mq_subscribe(sd_session_t* session, sd_message_t* message);
int on_mq_publish(sd_session_t* session, sd_message_t* message);

static sd_server_event_t onevent = {
    .onopen = on_open,
    .onclose = on_close,
    .onmessage = on_message,
    .onrequest = 0,
    .onsubscribe = 0,
    .onerror = 0,
};

static event_handler_t message_event_handler_table[] = {
    {"mq.sub", on_mq_subscribe},
    {"mq.push", on_mq_publish},
};

int on_open(sd_session_t* session, sd_message_t* message) {
    printf("accept connfd=%d [%s] <= [%s]\n",
        session->channle->fd,
        session->channle->local_address,
        session->channle->remote_address);

    add_user(session);
    return 0;
}

int on_close(sd_session_t* session, sd_message_t* message) {
    printf("on_close()\n");

    printf("remove user\n");
    remove_user(session);
    return 0;
}

int on_message(sd_session_t* session, sd_message_t* message) {
    const char* event = message->event;
    if (event && strlen(event) > 0) {
        int n = sizeof(message_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, message_event_handler_table[i].name) == 0) {
                message_event_handler_table[i].fn(session, message);
                break;
            }
        }
    }

    return 0;
}

int on_mq_subscribe(sd_session_t* session, sd_message_t* message) {
    printf("on_mq_subscribe()\n");

    const char* topic = sd_meta(&message->entity, "topic");
    if (topic && strlen(topic) > 0) {
        printf("set %s=1\n", topic);
        sd_put_attr(session, topic, "1");
    }

    return 0;
}

int on_mq_publish(sd_session_t* session, sd_message_t* message) {
    printf("on_mq_publish()\n");

    const char* topic = sd_meta(&message->entity, "topic");
    const char* id = sd_meta(&message->entity, "id");
    if (topic && strlen(topic) > 0 && id && strlen(id) > 0) {
        //开始给订阅用户广播
        for_each_user_list(topic, message);
    }

    return 0;
}

void sd_init(sd_server_t fd) {
    user_list_init();
}

int main(int argc, char** argv) {
	int port = 8602;

	printf("Starting server on %d ...\n", port);
    sd_server_t server = sd_create_tcp_server(port);
    if (server == 0) {
        printf("sd_create_tcp_server() error!\n");
        return -1;
    }
    sd_init(server);
    sd_regist_server(server, onevent);
    sd_start_tcp_server(server);
    sd_destory_tcp_server(server);

    return 0;
}
