/*
 * @build   make
 */
#include <stdio.h>
#include "tcp_client.h"

int main(int argc, char** argv) {
    sd_client_t cli = sd_create_tcp_client("sd:tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3");
    if (cli == 0) {
        printf("sd_create_tcp_client() error!\n");
        return -1;
    }
    sd_start_tcp_client(cli);
    sd_destory_tcp_client(cli);
    return 0;
}
