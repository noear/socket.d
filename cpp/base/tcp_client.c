#include "hv/hloop.h"
#include "hv/hbase.h"
#include "uuid4.h"
#include "sds.h"
#include "tcp_client.h"

static unpack_setting_t socketd_unpack_setting = {
    .mode = UNPACK_BY_LENGTH_FIELD,
    .package_max_length = DEFAULT_PACKAGE_MAX_LENGTH,
    .body_offset = 4,
    .length_field_offset = 0,
    .length_field_bytes = 4,
    .length_adjustment = -4,
    .length_field_coding = ENCODE_BY_BIG_ENDIAN,
};

static struct sd_client_event_s client_event = {
    .onconnack = 0,
    .onclose = 0,
    .onmessage = 0,
    .onreplay = 0,
    .onerror = 0,
};

int on_event1(sd_session_t* session, sd_message_t* message) {
    return 0;
}

event_handler_t replay_event_handler_table[] = {
    {"/m1", on_event1},
};

// sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3
void parse_client_config(const char* surl, tcp_client_t* config) {
    config->url = sdsnew(surl);

    char* p1 = strstr(surl, "://");
    if (p1 == NULL) return;
    config->schema = sdsnewlen(surl, p1 - surl);

    p1 += 3;
    char* p2 = strchr(p1, ':');
	if (p2 == NULL) return;
    config->host = sdsnewlen(p1, p2 - p1);

    p2 += 1;
    config->port = atoi(p2);
    if (config->port <= 0) config->port = 8602;
}

void generate_id(char* buf, size_t len) {
    uuid4_init();
    uuid4_generate(buf);
}

void sd_no_support(uint32_t n) {
    printf("******No support flag(%d)******", n);
    exit(0);
}

void on_connect_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

// TODO: Socket.D=1.0
void on_connack_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (client_event.onconnack) {
        client_event.onconnack(channel->session, &sd->frame.message);
    }
}

void on_ping_handler(sd_channel_t* channel, sd_package_t* sd) {
    char buf[8] = { 0 };
    uint32_t* p = (uint32_t*)&buf[0];
    *p = swap_endian(8);
    p++;
    *p = swap_endian(PONG_FRAME);

    print_package_info("Send Message", sd);

    hio_write((hio_t*)channel, &buf[0], 8);
}

void on_pong_handler(sd_channel_t* channel, sd_package_t* sd) {
    char buf[8] = { 0 };
    uint32_t* p = (uint32_t*)&buf[0];
    *p = swap_endian(8);
    p++;
    *p = swap_endian(PING_FRAME);


    sd->frame.flag = PING_FRAME;
    print_package_info("Send Message", sd);

    hio_write((hio_t*)channel, &buf[0], 8);
}

void on_close_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (client_event.onclose) {
        int ret = client_event.onclose(channel->session, &sd->frame.message);
        if (ret != 0)   return;
    }

    void* hio = channel->hio;
    hloop_stop(hevent_loop(hio));
}

void on_alarm_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_message_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (client_event.onmessage) {
        client_event.onmessage(channel->session, &sd->frame.message);
    }
}

void on_request_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_subscribe_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void on_reply_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (client_event.onreplay) {
        client_event.onreplay(channel->session, &sd->frame.message);
        return;
    }

    const char* event = sd->frame.message.event;
    if (event && strlen(event) > 0) {
        int n = sizeof(replay_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, replay_event_handler_table[i].name) == 0) {
                replay_event_handler_table[i].fn(channel->session, &sd->frame.message);
                break;
            }
        }
    }
}

void on_endreply_handler(sd_channel_t* channel, sd_package_t* sd) {
    if (client_event.onreplay) {
        client_event.onreplay(channel->session, &sd->frame.message);
        return;
    }

    const char* event = sd->frame.message.event;
    if (event && strlen(event) > 0) {
        int n = sizeof(replay_event_handler_table) / sizeof(event_handler_t);
        for (int i = 0; i < n; i++) {
            if (strcmp(event, replay_event_handler_table[i].name) == 0) {
                replay_event_handler_table[i].fn(channel->session, &sd->frame.message);
                break;
            }
        }
    }
}

void on_default_handler(sd_channel_t* channel, sd_package_t* sd) {
    sd_no_support(sd->frame.flag);
}

void client_on_handler(sd_channel_t* channel, sd_package_t* sd) {
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

void hand_shake(hio_t* io, sd_channel_t* channel, const char* url) {
    if (channel && channel->session == NULL) {
        sd_session_t* session = new_session(channel);        
        param_list_init(session);
        attr_list_init(session);
        generate_id(session->sid, UUID4_LEN);
        channel->session = session;
    }

    if (channel && channel->session) {
        sd_entity_t entity = { 0 };
        init_entity(&entity);
        string_entity_data(&entity, url);
        sd_send_connect(channel->session->sid, url, &entity, io);
        free_entity_meta_and_data(&entity);
    }
}

void client_on_recv(hio_t* io, void* buf, int readbytes) {
    printf("on_recv fd=%d readbytes=%d\n", hio_fd(io), readbytes);

    tcp_client_t* cli = (tcp_client_t*)hevent_userdata(io);

    sd_package_t sd = { 0 };
    init_package(&sd);
    sd_decode(&sd, (char*)buf, readbytes);
    client_on_handler(cli->channel, &sd);
    free_entity_meta_and_data(&sd.frame.message.entity);
}

void client_on_connect(hio_t* io) {
    printf("client on connect: connfd=%d\n", hio_fd(io));
    tcp_client_t* cli = (tcp_client_t*)hevent_userdata(io);
    cli->connected = 1;

    sd_channel_t* channel = new_channel((void*)io);
    channel->hio = io;
    channel->fd = hio_fd(io);
    cli->channel = channel;

    hand_shake(io, channel, cli->url);

    hio_setcb_read(io, client_on_recv);
    hio_read(io);
}

void client_on_close(hio_t* io) {
    printf("onclose: connfd=%d error=%d\n", hio_fd(io), hio_error(io));
    tcp_client_t* cli = (tcp_client_t*)hevent_userdata(io);
    
    if (cli && cli->channel) {
        if (cli->channel->session) {
            free_session(cli->channel->session);
            cli->channel->session = NULL;
        }

        hevent_set_userdata(io, NULL);
        free_channel(cli->channel);
        cli->channel = 0;
    }
}

void client_on_timer(htimer_t* timer) {

}

// hloop_new -> malloc(tcp_client_t)
tcp_client_t* tcp_client_new(hloop_t* loop) {
    if (loop == NULL) {
        loop = hloop_new(HLOOP_FLAG_AUTO_FREE);
        if (loop == NULL) return NULL;
    }
    tcp_client_t* cli = NULL;
    HV_ALLOC_SIZEOF(cli);
    if (cli == NULL) return NULL;
    memset((void*)cli, 0, sizeof(tcp_client_t));
    cli->loop = loop;
    hmutex_init(&cli->mutex_);
    return cli;
}

// hloop_free -> free(tcp_client_t)
void tcp_client_free(tcp_client_t* cli) {
    if (!cli) return;
    if (cli->host) {
        sdsfree(cli->host);
        cli->host = 0;
    }
    if (cli->url) {
        sdsfree(cli->url);
        cli->url = 0;
    }
    if (cli->schema) {
        sdsfree(cli->schema);
        cli->schema = 0;
    }
    hmutex_destroy(&cli->mutex_);
    HV_FREE(cli);
}

void tcp_client_run(tcp_client_t* cli) {
    if (!cli || !cli->loop) return;
    hloop_run(cli->loop);
}

void tcp_client_stop(tcp_client_t* cli) {
    if (!cli || !cli->loop) return;
    hloop_stop(cli->loop);
}

int tcp_client_connect(tcp_client_t* cli) {
    if (!cli) return -1;

    hio_t* io = hio_create_socket(cli->loop, cli->host, cli->port, HIO_TYPE_TCP, HIO_CLIENT_SIDE);
    if (io == NULL) return -1;
    if (cli->connect_timeout > 0) {
        hio_set_connect_timeout(io, cli->connect_timeout);
    }
    cli->io = io;
    hevent_set_userdata(io, cli);

    // unpack
    hio_set_unpack(io, &socketd_unpack_setting);

    hio_setcb_connect(io, cli->on_connect);
    hio_setcb_close(io, cli->on_close);
    return hio_connect(io);
}

// sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3
tcp_client_t* sd_create_tcp_client(const char* surl) {
    tcp_client_t* cli = tcp_client_new(0);
    if (!cli) return 0;

    parse_client_config(surl, cli);
    cli->connect_timeout = 1000;
    cli->on_connect = client_on_connect;
    cli->on_close = client_on_close;

    tcp_client_connect(cli);

    htimer_t* htimer = htimer_add(cli->loop, client_on_timer, 50, INFINITE);
    hevent_set_userdata(htimer, cli);

    return cli;
}

void sd_start_tcp_client(sd_client_t fd) {
    tcp_client_t* cli = fd;
    tcp_client_run(cli);
}

void sd_destory_tcp_client(sd_client_t fd) {
    tcp_client_t* cli = fd;
    tcp_client_free(cli);
}

void sd_regist_client(sd_client_t fd, sd_client_event_t e) {
    client_event = e;
}
