#ifndef TCP_CLIENT_H_
#define TCP_CLIENT_H_

#include "hv/hmutex.h"
#include "hv/hloop.h"
#include "hv/hsocket.h"

typedef struct sd_session_s {
    char sid[64];
    char* uri;   //handshake uri
    char* path;  //handshake path
} sd_session_t;

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
    sd_session_t session;

    hconnect_cb on_connect;
    hclose_cb on_close;
} tcp_client_t;

typedef tcp_client_t* sd_client_t;

sd_client_t sd_create_tcp_client(const char* surl);
void sd_start_tcp_client(sd_client_t fd);
void sd_destory_tcp_client(sd_client_t fd);

#endif