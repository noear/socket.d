#ifndef TCP_SERVER_H_
#define TCP_SERVER_H_

#include "hv/hmutex.h"
#include "hv/hloop.h"
#include "hv/hsocket.h"

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

typedef struct sd_channel_s sd_channel_t;

typedef struct sd_session_s {
    char sid[64];
    char* uri;   //handshake uri
    char* path;  //handshake path
    sd_channel_t* channle;
} sd_session_t;

typedef struct sd_channel_s {
    int fd;
    void* hio;
    void* attachment;
    char remote_address[64];
    char local_address[64];
    sd_session_t session;
} sd_channel_t;


typedef tcp_server_t* sd_server_t;

sd_server_t sd_create_tcp_server(int port);
void sd_start_tcp_server(sd_server_t fd);
void sd_destory_tcp_server(sd_server_t fd);

#endif
