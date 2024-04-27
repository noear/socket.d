#ifndef SOCKETD_H_
#define SOCKETD_H_

#include "sd_entity_metas.h"
#include "meta.h"
#include "param.h"
#include "attr.h"

/**
 * 10 Connect Frame
 * 11 Connack Frame
 * 20 Ping Frame
 * 21 Pong Frame
 * 30 Close Frame
 * 31 Alarm Frame
 * 40 Message Frame
 * 41 Request Frame
 * 42 Subscribe Frame
 * 48 Reply Frame
 * 49 End Reply Frame
 */

#define CONNECT_FRAME 10
#define CONNACK_FRAME 11
#define PING_FRAME 20
#define PONG_FRAME 21
#define CLOSE_FRAME 30
#define ALARM_FRAME 31
#define MESSAGE_FRAME 40
#define REQUEST_FRAME 41
#define SUBSCRIBE_FRAME 42
#define REPLAY_FRAME 48
#define END_REPLAY_FRAME 49

typedef struct sd_entity_s {
    struct list_head metalist;

    uint32_t metalen;
    uint32_t datalen;
    char* meta;
    char* data;
} sd_entity_t;

typedef struct sd_message_s {
    char sid[64];
    char event[512];
    sd_entity_t entity;
} sd_message_t;

typedef struct sd_frame_s {
    uint32_t flag;
    sd_message_t message;
} sd_frame_t;

typedef struct sd_package_s {
    uint32_t len;
    sd_frame_t frame;
} sd_package_t;

typedef struct sd_channel_s sd_channel_t;

typedef struct sd_session_s {
    char sid[64];
    char uri[2048];   //handshake uri
    char path[128];  //handshake path
    sd_channel_t* channle;
    struct list_head paramlist; //handshake parameter list
    struct list_head attrlist;  //attribute list
} sd_session_t;

typedef struct sd_channel_s {
    int fd;
    void* hio;
    void* attachment;
    char remote_address[64];
    char local_address[64];
    sd_session_t* session;
} sd_channel_t;

void init_package(sd_package_t* pkg);
void init_entity(sd_entity_t* entity);
void free_entity_meta_and_data(sd_entity_t* entity);
void string_entity_data(sd_entity_t* entity, const char* text);

sd_package_t* sd_decode(sd_package_t* sd, char* buf, uint32_t len);
void sd_encode(struct sd_package_s* sd, char** pbuf, uint32_t* plen);

void sd_send_connect(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_connack(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_close(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_alarm(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_message(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_request(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_subscribe(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_replay(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_endreplay(const char* sid, const char* event, sd_entity_t* entity, void* hio);

/*session opt*/
sd_session_t* new_session(sd_channel_t* channel);
void free_session(sd_session_t* session);
void close_session(sd_session_t* session, const char* sid, const char* event);
void session_send_string(sd_session_t* session, const char* sid, const char* event, const char* data);

/*channel opt*/
sd_channel_t* new_channel();
void free_channel(sd_channel_t* channel);

/*meta opt*/
void meta_list_init(sd_entity_t* entity);
void meta_list_free(sd_entity_t* entity);
void sd_put_meta(sd_entity_t* entity, const char* name, const char* value);
const char* sd_meta(sd_entity_t* entity, const char* name);
int sd_meta_as_int(sd_entity_t* entity, const char* name);
long long sd_meta_as_long(sd_entity_t* entity, const char* name);
double sd_meta_as_double(sd_entity_t* entity, const char* name);
void parse_meta_string(sd_entity_t* entity, const char* meta);
char* format_meta_string(sd_entity_t* entity);

/*param opt*/
void param_list_init(sd_session_t* session);
void param_list_free(sd_session_t* session);
void sd_put_param(sd_session_t* session, const char* name, const char* value);
const char* sd_param(sd_session_t* session, const char* name);
void parse_handshake_param(sd_session_t* session, sd_message_t* msg);

/*attr opt*/
void attr_list_init(sd_session_t* session);
void attr_list_free(sd_session_t* session);
sd_attr_t* sd_put_attr(sd_session_t* session, const char* name, const char* value);
sd_attr_t* sd_put_attr_as_int(sd_session_t* session, const char* name, int value);
const char* sd_attr(sd_session_t* session, const char* name);
const char* sd_attr_or_default(sd_session_t* session, const char* name, const char* dv);
int sd_attr_or_default_as_int(sd_session_t* session, const char* name, int dv);

// helper
void* sd_hio(sd_session_t* session);
uint32_t swap_endian(uint32_t x);
void print_package_info(const char* msg, struct sd_package_s* sd);

#endif