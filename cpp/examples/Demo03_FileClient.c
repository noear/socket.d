/*
 * File tcp client
 *
 * @build   make
 * @client  bin/Demo02_FileClient
 */
#include <stdio.h>
#include "tcp_client.h"

int on_connack(sd_session_t* session, sd_message_t* message);

static sd_client_event_t onevent = {
    .onconnack = on_connack,
    .onclose = 0,
    .onmessage = 0,
    .onreplay = 0,
    .onerror = 0,
};

static char* read_file(const char* filename, uint32_t* datalen) {
    FILE* file;

    file = fopen(filename, "rb");
    if (file == NULL) {
        printf("Error opening file %s.\n", filename);
        return NULL;
    }

    fseek(file, 0, SEEK_END);
    size_t size = ftell(file);
    fseek(file, 0, SEEK_SET);

    // free buffer by caller
    char* buffer = (char*)malloc(size + 1);
    if (buffer == NULL) {
        printf("Failed to allocate memory\n");
        fclose(file);
        return NULL;
    }

    size_t result = fread(buffer, sizeof(char), size, file);
    if (result != size) {
        printf("Failed to read file\n");
        free(buffer);
        fclose(file);
        return NULL;
    }

    fclose(file);

    *datalen = (uint32_t)size;
    return buffer;
}

int on_connack(sd_session_t* session, sd_message_t* message) {
    void* hio = sd_hio(session);

    // send meta
    sd_entity_t entity = { 0 };
    init_entity(&entity);
    sd_put_meta(&entity, "user", "noear");
    sd_put_meta(&entity, "Trace-Id", "ebb183c8-0938-4647-9be4-2084eedf82c9");
    sd_send_message(session->sid, "/demo", &entity, hio);
    free_entity_meta_and_data(&entity);

    // send file
    sd_entity_t entity2 = { 0 };
    init_entity(&entity2);
    sd_put_meta(&entity2, META_DATA_DISPOSITION_FILENAME, "~/Downloads/test_send.txt");
    entity2.data = read_file("/Users/noear/Downloads/socketd-upload.txt", &entity2.datalen);
    if (entity2.data) {
        sd_send_message(session->sid, "/demo2", &entity2, hio);
        free_entity_meta_and_data(&entity2);
    }

    return 0;
}

int main(int argc, char** argv) {
    sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/admin?u=a&p=2");
    if (cli == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_regist_client(cli, onevent);
    sd_start_tcp_client(cli);
    sd_destory_tcp_client(cli);
    return 0;
}
