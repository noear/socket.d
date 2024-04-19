/*
 * @build   make
 */
#include <stdio.h>
#include "tcp_server.h"

int main(int argc, char** argv) {
	int port = 8602;

	printf("Starting server on %d ...\n", port);
    sd_server_t server = sd_create_tcp_server(port);
    if (server == 0) {
        printf("sd_create_tcp_server() error!\n");
        return -1;
    }
    sd_start_tcp_server(server);
    sd_destory_tcp_server(server);

    return 0;
}
