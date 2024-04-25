#include "hv/hloop.h"
#include "hv/hsocket.h"
#include "hv/hbase.h"
#include "hv/herr.h"
#include "sds.h"
#include "tcp_server.h"

static unpack_setting_t socketd_unpack_setting = {
    .mode = UNPACK_BY_LENGTH_FIELD,
    .package_max_length = DEFAULT_PACKAGE_MAX_LENGTH,
    .body_offset = 4,
    .length_field_offset = 0,
    .length_field_bytes = 4,
    .length_adjustment = -4,
    .length_field_coding = ENCODE_BY_BIG_ENDIAN,
};

static struct sd_server_event_s server_event = {
    .onopen = 0,
    .onclose = 0,
    .onmessage = 0,
    .onrequest = 0,
    .onerror = 0,
};

static void on_tcp_accept(hio_t* io);

int on_message1(sd_session_t* session, sd_message_t* message) {
    return 0;
}

int on_request1(sd_session_t* session, sd_message_t* message) {
    return 0;
}

event_handler_t message_event_handler_table[] = {
    {"/m1", on_message1},
};

event_handler_t request_event_handler_table[] = {
    {"/r1", on_request1},
};

// hloop_create_tcp_server -> on_accept -> hio_read -> on_recv -> hio_write

// hloop_new -> malloc(tcp_server_t)
tcp_server_t* tcp_server_new(hloop_t* loop) {
    if (loop == NULL) {
        loop = hloop_new(HLOOP_FLAG_AUTO_FREE);
        if (loop == NULL) return NULL;
    }
    tcp_server_t* srv = NULL;
    HV_ALLOC_SIZEOF(srv);
    if (srv == NULL) return NULL;
    srv->loop = loop;
    hmutex_init(&srv->mutex_);
    return srv;
}

// hloop_free -> free(tcp_server_t)
void tcp_server_free(tcp_server_t* srv) {
    if (!srv) return;
    hmutex_destroy(&srv->mutex_);
    HV_FREE(srv);
}

void tcp_server_run(tcp_server_t* srv) {
    if (!srv || !srv->loop) return;
    hloop_run(srv->loop);
}

void tcp_server_stop(tcp_server_t* srv) {
    if (!srv || !srv->loop) return;
    hloop_stop(srv->loop);
}

void tcp_server_init(tcp_server_t* srv, int port) {
    strcpy(srv->host, "0.0.0.0");
    srv->port = port;
    srv->accept_fn = on_tcp_accept;
}

void tcp_server_create(tcp_server_t* srv) {
    hio_t* listenio = hloop_create_tcp_server(srv->loop, srv->host, srv->port, srv->accept_fn);
    srv->io = listenio;
}

static void on_tcp_close(hio_t* io) {
    printf("on_close fd=%d error=%d\n", hio_fd(io), hio_error(io));

    sd_channel_t* channel = (sd_channel_t*)hio_context(io);
    if (channel) {
        if (channel->session) {
            free_session(channel->session);
            channel->session = NULL;
        }

        hio_set_context(io, NULL);
        free_channel(channel);
        channel = NULL;
    }
}

void sd_no_support(uint32_t n) {
    printf("******No support flag(%d)******", n);
    exit(0);
}

void on_connect_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (channel->session == NULL) {
        sd_session_t* session = new_session(channel);        
        param_list_init(session);
        attr_list_init(session);
        channel->session = session;
    }

    strcpy(channel->session->sid, sd->frame.message.sid);
    parse_handshake_param(channel->session, &sd->frame.message);

    if (server_event.onopen) {
        server_event.onopen(channel->session, &sd->frame.message);
    }

    sd_send_connack(sd->frame.message.sid, sd->frame.message.event, &sd->frame.message.entity, channel->hio);
}

void on_connack_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_ping_handler(sd_channel_t* channel, sd_package_t* sd) {
    char buf[8] = { 0 };
    uint32_t* p = (uint32_t*)&buf[0];
    *p = swap_endian(8);
    p++;
    *p = swap_endian(PONG_FRAME);

    print_package_info("Send Message", sd);

    hio_write((hio_t*)channel->hio, &buf[0], 8);
}

void on_pong_handler(sd_channel_t* channel, sd_package_t* sd) {
    char buf[8] = { 0 };
    uint32_t* p = (uint32_t*)&buf[0];
    *p = swap_endian(8);
    p++;
    *p = swap_endian(PING_FRAME);


    sd->frame.flag = PING_FRAME;
    print_package_info("Send Message", sd);

    hio_write((hio_t*)channel->hio, &buf[0], 8);
}

void on_close_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_alarm_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_message_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (server_event.onmessage) {
        server_event.onmessage(channel->session, &sd->frame.message);
        return;
    }

    const char* event = sd->frame.message.event;
    if (event && strlen(event) > 0) {
        int n = sizeof(message_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, message_event_handler_table[i].name) == 0) {
                message_event_handler_table[i].fn(channel->session, &sd->frame.message);
                break;
            }
        }
    }
}

void on_request_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (server_event.onrequest) {
        server_event.onrequest(channel->session, &sd->frame.message);
        return;
    }

    const char* event = sd->frame.message.event;
    if (event && strlen(event) > 0) {
        int n = sizeof(request_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, request_event_handler_table[i].name) == 0) {
                request_event_handler_table[i].fn(channel->session, &sd->frame.message);
                break;
            }
        }
    }
}

void on_subscribe_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (server_event.onsubscribe) {
        server_event.onsubscribe(channel->session, &sd->frame.message);
    }
}

void on_reply_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_endreply_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_default_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_handler(sd_channel_t* channel, sd_package_t* sd) {
    switch (sd->frame.flag) {
    case CONNECT_FRAME: on_connect_handler(channel, sd); break;
    case CONNACK_FRAME: on_connack_handler(channel, sd); break;
    case PING_FRAME: on_ping_handler(channel, sd); break;
    case PONG_FRAME: on_pong_handler(channel, sd); break;
    case CLOSE_FRAME: on_close_handler(channel, sd); break;
    case ALARM_FRAME: on_alarm_handler(channel, sd); break;
    case MESSAGE_FRAME: on_message_handler(channel, sd); break;
    case REQUEST_FRAME: on_request_handler(channel, sd); break;
    case SUBSCRIBE_FRAME: on_subscribe_handler(channel, sd); break;
    case REPLAY_FRAME: on_reply_handler(channel, sd); break;
    case END_REPLAY_FRAME: on_endreply_handler(channel, sd); break;
    default: on_default_handler(channel, sd); break;
    }
}

static void on_tcp_recv(hio_t* io, void* buf, int readbytes) {
    printf("on_recv fd=%d readbytes=%d\n", hio_fd(io), readbytes);
    char localaddrstr[SOCKADDR_STRLEN] = { 0 };
    char peeraddrstr[SOCKADDR_STRLEN] = { 0 };
    printf("[%s] <=> [%s]\n",
        SOCKADDR_STR(hio_localaddr(io), localaddrstr),
        SOCKADDR_STR(hio_peeraddr(io), peeraddrstr));
    printf("< %.*s", readbytes, (char*)buf);

    sd_channel_t* channel = (sd_channel_t*)hio_context(io);
    assert(channel != NULL);
    channel->hio = io;

    sd_package_t sd = { 0 };
    init_package(&sd);
    sd_decode(&sd, (char*)buf, readbytes);

    on_handler(channel, &sd);

    free_entity_meta_and_data(&sd.frame.message.entity);
}

static void on_tcp_accept(hio_t* io) {
    printf("on_accept connfd=%d\n", hio_fd(io));

    char localaddrstr[SOCKADDR_STRLEN] = { 0 };
    char peeraddrstr[SOCKADDR_STRLEN] = { 0 };

    printf("accept connfd=%d [%s] <= [%s]\n", hio_fd(io), SOCKADDR_STR(hio_localaddr(io), localaddrstr), SOCKADDR_STR(hio_peeraddr(io), peeraddrstr));

    int fd = hio_fd(io);
    const char* localaddr = SOCKADDR_STR(hio_localaddr(io), localaddrstr);
    const char* peeraddr = SOCKADDR_STR(hio_peeraddr(io), peeraddrstr);

    // free sd_channel_t on_close()
    sd_channel_t* channel = new_channel();
    channel->hio = io;
    channel->fd = fd;
    strcpy(channel->local_address, localaddr);
    strcpy(channel->remote_address, peeraddr);
    hio_set_context(io, (void*)channel);

    // call back function
    hio_setcb_close(io, on_tcp_close);
    hio_setcb_read(io, on_tcp_recv);

    // unpack
    hio_set_unpack(io, &socketd_unpack_setting);

    hio_read_start(io);
}

sd_server_t sd_create_tcp_server(int port) {
    tcp_server_t* srv = tcp_server_new(0);
    if (srv == 0) {
        return 0;
    }

    tcp_server_init(srv, port);
    tcp_server_create(srv);
	return (sd_server_t)srv;
}

void sd_start_tcp_server(sd_server_t fd) {
	tcp_server_t* srv = (tcp_server_t*)fd;
	tcp_server_run(srv);
}

void sd_destory_tcp_server(sd_server_t fd) {
	tcp_server_t* srv = (tcp_server_t*)fd;
	tcp_server_free(srv);
}

void sd_regist_server(sd_server_t fd, sd_server_event_t e) {
    server_event = e;
}