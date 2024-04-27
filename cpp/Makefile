# Makefile of socketd

include Makefile.vars

CC = gcc
CFLAGS = -Wall
INCLUDE = -I./base -I./examples -I./3rd/libhv/include -I./3rd/sds -I./3rd/uuid4/src
LIBDIR = -L./3rd/libhv/lib
HV_LIB = -lhv

HelloworldServer = ./bin/Demo01_HelloworldServer
HelloworldClient = ./bin/Demo01_HelloworldClient
SendServer = ./bin/Demo02_SendServer
SendClient = ./bin/Demo02_SendClient
SendAndRequestServer = ./bin/Demo02_SendAndRequestServer
SendAndRequestClient = ./bin/Demo02_SendAndRequestClient
SendAndSubscribeServer = ./bin/Demo02_SendAndSubscribeServer
SendAndSubscribeClient = ./bin/Demo02_SendAndSubscribeClient
FileServer = ./bin/Demo03_FileServer
FileClient = ./bin/Demo03_FileClient
FlowControlServer = ./bin/Demo03_FlowControlServer
FlowControlClient = ./bin/Demo03_FlowControlClient
Send2Server = ./bin/Demo03_Send2Server
Send2Client = ./bin/Demo03_Send2Client
UrlAuthServer = ./bin/Demo03_UrlAuthServer
UrlAuthClient = ./bin/Demo03_UrlAuthClient
EventListenerServer = ./bin/Demo04_EventListenerServer
EventListenerClient = ./bin/Demo04_EventListenerClient
PathListenerServer = ./bin/Demo04_PathListenerServer
PathListenerClient = ./bin/Demo04_PathListenerClient


.PHONY:all
all:prepare $(HelloworldServer) $(HelloworldClient) \
	$(SendServer) $(SendClient) \
	$(SendAndRequestServer) $(SendAndRequestClient) \
	$(SendAndSubscribeServer) $(SendAndSubscribeClient) \
	$(FileServer) $(FileClient) \
	$(FlowControlServer) $(FlowControlClient) \
	$(Send2Server) $(Send2Client) \
	$(UrlAuthServer) $(UrlAuthClient) \
	$(EventListenerServer) $(EventListenerClient) \
	$(PathListenerServer) $(PathListenerClient)


SRCS =  ./3rd/uuid4/src/uuid4.c \
	./3rd/sds/sds.c \
	./base/socketd.c \
	./base/meta.c \
	./base/param.c \
	./base/attr.c

OBJS = $(SRCS:.c=.o)

$(HelloworldServer):$(OBJS) ./examples/Demo01_HelloworldServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(HelloworldClient):$(OBJS) ./examples/Demo01_HelloworldClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendServer):$(OBJS) ./examples/Demo02_SendServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendClient):$(OBJS) ./examples/Demo02_SendClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendAndRequestServer):$(OBJS) ./examples/Demo02_SendAndRequestServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendAndRequestClient):$(OBJS) ./examples/Demo02_SendAndRequestClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendAndSubscribeServer):$(OBJS) ./examples/Demo02_SendAndSubscribeServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(SendAndSubscribeClient):$(OBJS) ./examples/Demo02_SendAndSubscribeClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(FileServer):$(OBJS) ./examples/Demo03_FileServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(FileClient):$(OBJS) ./examples/Demo03_FileClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(FlowControlServer):$(OBJS) ./examples/Demo03_FlowControlServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(FlowControlClient):$(OBJS) ./examples/Demo03_FlowControlClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(Send2Server):$(OBJS) ./examples/Demo03_Send2Server.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(Send2Client):$(OBJS) ./examples/Demo03_Send2Client.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(UrlAuthServer):$(OBJS) ./examples/Demo03_UrlAuthServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(UrlAuthClient):$(OBJS) ./examples/Demo03_UrlAuthClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(EventListenerServer):$(OBJS) ./examples/Demo04_EventListenerServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(EventListenerClient):$(OBJS) ./examples/Demo04_EventListenerClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(PathListenerServer):$(OBJS) ./examples/Demo04_PathListenerServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(PathListenerClient):$(OBJS) ./examples/Demo04_PathListenerClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^


%.o:%.c
	$(CC) $(CFLAGS) $(INCLUDE) -o $@ -c $<

clean:
	rm -rf $(OBJS) $(HelloworldServer) $(HelloworldClient) \
	$(SendServer) $(SendClient) \
	$(SendAndRequestServer) $(SendAndRequestClient) \
	$(SendAndSubscribeServer) $(SendAndSubscribeClient) \
	$(FileServer) $(FileClient) \
	$(FlowControlServer) $(FlowControlClient) \
	$(Send2Server) $(Send2Client) \
	$(UrlAuthServer) $(UrlAuthClient) \
	$(EventListenerServer) $(EventListenerClient) \
	$(PathListenerServer) $(PathListenerClient)


prepare:
	$(MKDIR) bin
