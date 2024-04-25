#ifndef TCP_CLIENT_H_
#define TCP_CLIENT_H_

#include "hv/hmutex.h"
#include "hv/hloop.h"
#include "hv/hsocket.h"
#include "socketd.h"

typedef struct tcp_client_s {
    // connect: host:port
    char* host;
    int  port;
    int  connect_timeout; // ms
    // flags
    unsigned char   connected;
    // privdata
    hloop_t* loop;
    hio_t* io;
    // thread-safe
    hmutex_t    mutex_;
    // ...
    char* url;
    char* schema;
    sd_channel_t* channel;

    hconnect_cb on_connect;
    hclose_cb on_close;
} tcp_client_t;

typedef tcp_client_t* sd_client_t;

typedef struct sd_client_event_s {
    int (*onconnack)(sd_session_t*, sd_message_t*);
    int (*onclose)(sd_session_t*, sd_message_t*);
    int (*onmessage)(sd_session_t*, sd_message_t*);
    int (*onreplay)(sd_session_t*, sd_message_t*);
    int (*onerror)(sd_session_t*, sd_message_t*);
} sd_client_event_t;

struct event_handler_s {
    const char* name;
    int (*fn)(sd_session_t*, sd_message_t*);
};

typedef struct event_handler_s event_handler_t;
typedef struct event_handler_s path_handler_t;
typedef struct event_handler_s interceptor_handler_t;

sd_client_t sd_create_tcp_client(const char* surl);
void sd_start_tcp_client(sd_client_t fd);
void sd_destory_tcp_client(sd_client_t fd);
void sd_regist_client(sd_client_t fd, sd_client_event_t e);

#endif