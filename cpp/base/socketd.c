#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "sds.h"
#include "hv/hloop.h"
#include "hv/hbase.h"
#include "const.h"
#include "socketd.h"

uint32_t swap_endian(uint32_t x) {
    union {
        uint32_t i;
        uint8_t c[4];
    } b;
    b.i = x;
    return (b.c[0] << 24) | (b.c[1] << 16) | (b.c[2] << 8) | b.c[3];
}

void print_package_info(const char* msg, struct sd_package_s* sd) {
    if (sd) {
        printf("****** %s ******\n", msg);
        printf("flag=%d\n", sd->frame.flag);
        printf("sid=%s\n", sd->frame.message.sid);
        printf("event=%s\n", sd->frame.message.event);
        //if (sd->frame.message.entity.meta) printf("meta=%s\n", sd->frame.message.entity.meta);
        //if (sd->frame.message.entity.data) printf("data=%s\n", sd->frame.message.entity.data);
    }
}

void init_package(sd_package_t* pkg) {
    if (pkg) {
        memset(pkg, 0, sizeof(sd_package_t));
        init_entity(&pkg->frame.message.entity);
    }
}

void init_entity(sd_entity_t* entity) {
    if (entity) {
        memset(entity, 0, sizeof(sd_entity_t));
        meta_list_init(entity);
    }
}

void free_entity_meta_and_data(sd_entity_t* entity) {
    meta_list_free(entity);

    if (entity->meta)
        sdsfree(entity->meta);

    if (entity->data)
        free(entity->data);

    entity->meta = 0;
    entity->metalen = 0;
    entity->data = 0;
    entity->datalen = 0;
}

bool is_entity_empty(sd_entity_t* e) {
    return (e->meta == 0 || e->metalen == 0) ||
        (e->data == 0 || e->datalen == 0);
}

/***
 * note: free memory of meta/data
 */
void string_entity_data(sd_entity_t* entity, const char* text) {
    if (!is_entity_empty(entity))
        free_entity_meta_and_data(entity);

    size_t datalen = 0;
    if (text && (datalen = strlen(text)) > 0) {
        entity->data = (char*)malloc(datalen + 1);
        entity->datalen = datalen;
        if (entity->data) {
            memcpy(entity->data, text, datalen);
            entity->data[datalen] = '\0';
        }
    }
}

uint32_t calc_send_buffer_len(struct sd_package_s* sd) {
    uint32_t len = 0;
    char* meta = sd->frame.message.entity.meta;
    uint32_t metalen = meta ? strlen(meta) : 0;

    len += sizeof(sd->len);
    len += sizeof(sd->frame.flag);
    len += strlen(sd->frame.message.sid) + 1 + 1;
    len += strlen(sd->frame.message.event) + 1 + 1;
    len += metalen + 1 + 1;
    len += sd->frame.message.entity.datalen;

    return len;
}

/**
 * note: after sd_decode(), free memory of meta/data
 */
sd_package_t* sd_decode(sd_package_t* sd, char* buf, uint32_t len) {
    char* p = buf;

    sd->len = len;
    p += sizeof(sd->len);
    
    sd->frame.flag = *((uint32_t*)p);
    sd->frame.flag = swap_endian(sd->frame.flag);
    p += sizeof(sd->frame.flag);

    if (sd->frame.flag == PING_FRAME || sd->frame.flag == PONG_FRAME) {
        print_package_info("Receive Message", sd);
        return sd;
    }

    char* sp = (char*)memchr(p, '\0', 64);
    if (sp) {
        if (*(++sp) == '\n') sp++;
        size_t len = sp - p;
        memset(&sd->frame.message.sid[0], 0, sizeof(sd->frame.message.sid));
        memcpy(&sd->frame.message.sid[0], p, len);
        p = sp;
    }

    sp = (char*)memchr(p, '\0', 512);
    if (sp) {
        if (*(++sp) == '\n') sp++;
        size_t len = sp - p;
        memset(sd->frame.message.event, 0, sizeof(sd->frame.message.event));
        memcpy(sd->frame.message.event, p, len);
        p = sp;
    }

    sp = (char*)memchr(p, '\0', 4096);
    if (sp) {
        if (*(++sp) == '\n') sp++;
        size_t len = sp - p;
        if (len > 0) {
            sd->frame.message.entity.metalen = len;
            sd->frame.message.entity.meta = sdsnewlen(p, len);
        }
        p = sp;
    }
    
    size_t datalen = len - (p - buf);
    if (datalen > 0) {
        sd->frame.message.entity.datalen = datalen;
        sd->frame.message.entity.data = (char*)malloc(datalen + 1);
        if (sd->frame.message.entity.data) {
            memset(sd->frame.message.entity.data, 0, datalen + 1);
            memcpy(sd->frame.message.entity.data, p, datalen);
        }
    }

    print_package_info("Receive Message", sd);

    return sd;
}

void sd_encode(struct sd_package_s* sd, char** pbuf, uint32_t* plen) {
    uint32_t len = calc_send_buffer_len(sd);
    char* p = (char*)malloc(len);
    if (p) memset(p, 0, len);

    *plen = len;
    *pbuf = p;

    *((uint32_t*)p) = swap_endian(sd->len);
    p += sizeof(uint32_t);

    *((uint32_t*)p) = swap_endian(sd->frame.flag);
    p += sizeof(uint32_t);

    strcpy(p, sd->frame.message.sid);
    p += strlen(sd->frame.message.sid);
    *p++ = '\0';
    *p++ = '\n';

    strcpy(p, sd->frame.message.event);
    p += strlen(sd->frame.message.event);
    *p++ = '\0';
    *p++ = '\n';

    if (sd->frame.message.entity.meta) {
        strcpy(p, sd->frame.message.entity.meta);
        p += strlen(sd->frame.message.entity.meta);
    }

    *p++ = '\0';
    *p++ = '\n';

    if (sd->frame.message.entity.data && sd->frame.message.entity.datalen) {
        memcpy(p, sd->frame.message.entity.data, sd->frame.message.entity.datalen);
    }

    print_package_info("Send Message", sd);
}

sd_entity_t* new_entity_default() {
    size_t len = sizeof(sd_entity_t);
    sd_entity_t* entity = (sd_entity_t*)malloc(len);
    memset(entity, 0, len);
    return entity;
}

sd_entity_t* new_entity_string(const char* text) {
    size_t len = sizeof(sd_entity_t);
    sd_entity_t* entity = (sd_entity_t*)malloc(len);
    memset(entity, 0, len);

    size_t datalen = 0;
    if (text && (datalen = strlen(text)) > 0) {
        entity->data = (char*)malloc(datalen);
        entity->datalen = datalen;
        memcpy(entity->data, text, datalen);
    }

    return entity;
}

void free_entity(sd_entity_t* e) {
    if (e->metalen && e->meta) {
        sdsfree(e->meta);
    }

    if (e->datalen && e->data) {
        free(e->data);
    }

    e->meta = 0;
    e->metalen = 0;

    e->data = 0;
    e->data = 0;

    free(e);
}

void sd_send_raw(uint32_t flag, const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_package_t reply = { 0 };
    init_package(&reply);

    reply.frame.flag = flag;
    strcpy(reply.frame.message.sid, sid);
    strcpy(reply.frame.message.event, event);

    sds metastr = format_meta_string(entity);
    if (metastr) {
        entity->metalen = sdslen(metastr);
        entity->meta = metastr;
    }
    reply.frame.message.entity = *entity;

    char* buf = 0;
    uint32_t len = 0;

    reply.len = calc_send_buffer_len(&reply);
    sd_encode(&reply, &buf, &len);

    hio_t* io = (hio_t*)(hio);
    hio_write(io, buf, len);

    free(buf);
}

void sd_send_connect(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(CONNECT_FRAME, sid, event, entity, hio);
}

void sd_send_connack(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(CONNACK_FRAME, sid, event, entity, hio);
}

void sd_send_close(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(CLOSE_FRAME, sid, event, entity, hio);
}

void sd_send_alarm(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(ALARM_FRAME, sid, event, entity, hio);
}

void sd_send_message(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(MESSAGE_FRAME, sid, event, entity, hio);
}

void sd_send_request(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(REQUEST_FRAME, sid, event, entity, hio);
}

void sd_send_subscribe(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(SUBSCRIBE_FRAME, sid, event, entity, hio);
}

void sd_send_replay(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(REPLAY_FRAME, sid, event, entity, hio);
}

void sd_send_endreplay(const char* sid, const char* event, sd_entity_t* entity, void* hio) {
    sd_send_raw(END_REPLAY_FRAME, sid, event, entity, hio);
}

sd_session_t* new_session(sd_channel_t* channel) {
    sd_session_t* s = malloc(sizeof(sd_session_t));
    memset(s, 0, sizeof(sd_session_t));
    s->channle = channel;
    return s;
}

void free_session(sd_session_t* session) {
    param_list_free(session);
    attr_list_free(session);
    free(session);
}

void close_session(sd_session_t* session, const char* sid, const char* event) {
    char pcode[64] = { 0 };
    sprintf(pcode, "%d", CLOSE1001_PROTOCOL_CLOSE);

    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    sd_put_meta(&entity, "code", pcode);
    sd_send_close(sid, event, &entity, hio);
    free_entity_meta_and_data(&entity);
}

void session_send_string(sd_session_t* session, const char* sid, const char* event, const char* data) {
    void* hio = sd_hio(session);
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    string_entity_data(&entity, data);
    sd_send_message(sid, event, &entity, hio);
    free_entity_meta_and_data(&entity);
}

sd_channel_t* new_channel() {
    void* p = malloc(sizeof(sd_channel_t));
    if (p) memset(p, 0, sizeof(sd_channel_t));
    return (sd_channel_t*)p;
}

void free_channel(sd_channel_t* channel) {
    free(channel);
}

void* sd_hio(sd_session_t* session) {
    if (session && session->channle) {
        return session->channle->hio;
    }
    return NULL;
}