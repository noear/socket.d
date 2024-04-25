#ifndef TCP_SERVER_H_
#define TCP_SERVER_H_

#include "hv/hmutex.h"
#include "hv/hloop.h"
#include "hv/hsocket.h"
#include "socketd.h"

typedef struct tcp_server_s {
    // connect: host:port
    char host[256];
    int  port;
    int  connect_timeout; // ms
    // flags
    unsigned char   connected;
    // privdata
    hloop_t* loop;
    hio_t* io;
    htimer_t* reconn_timer;
    // thread-safe
    hmutex_t    mutex_;
    // call back
    haccept_cb accept_fn;
} tcp_server_t;

typedef tcp_server_t* sd_server_t;

typedef struct sd_server_event_s {
    int (*onopen)(sd_session_t*, sd_message_t*);
    int (*onclose)(sd_session_t*, sd_message_t*);
    int (*onmessage)(sd_session_t*, sd_message_t*);
    int (*onrequest)(sd_session_t*, sd_message_t*);
    int (*onsubscribe)(sd_session_t*, sd_message_t*);
    int (*onerror)(sd_session_t*, sd_message_t*);
} sd_server_event_t;

struct event_handler_s {
    const char* name;
    int (*fn)(sd_session_t*, sd_message_t*);
};

typedef struct event_handler_s event_handler_t;
typedef struct event_handler_s path_handler_t;
typedef struct event_handler_s interceptor_handler_t;

sd_server_t sd_create_tcp_server(int port);
void sd_start_tcp_server(sd_server_t fd);
void sd_destory_tcp_server(sd_server_t fd);

void sd_regist_server(sd_server_t fd, sd_server_event_t e);

#endif
