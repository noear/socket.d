#ifndef SOCKETD_H_
#define SOCKETD_H_

#include "sd_entity_metas.h"

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

void free_meta_and_data(sd_package_t* pkg);
void free_entity_meta_and_data(sd_entity_t* entity);

void populate_entity_data(sd_entity_t* entity, const char* text);

sd_package_t* sd_decode(sd_package_t* sd, char* buf, uint32_t len);
void sd_encode(struct sd_package_s* sd, char** pbuf, uint32_t* plen);

void sd_send_connect(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_connack(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_message(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_request(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_replay(const char* sid, const char* event, sd_entity_t* entity, void* hio);
void sd_send_endreplay(const char* sid, const char* event, sd_entity_t* entity, void* hio);

// helper
uint32_t swap_endian(uint32_t x);
void print_package_info(const char* msg, struct sd_package_s* sd);

#endif