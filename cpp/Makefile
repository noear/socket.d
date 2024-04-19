# Makefile of socketd

include Makefile.vars

CC = gcc
CFLAGS = -Wall
INCLUDE = -I./base -I./examples -I./3rd/libhv/include -I./3rd/sds -I./3rd/uuid4/src
LIBDIR = -L./3rd/libhv/lib
HV_LIB = -lhv

TARGET1 = ./bin/Demo01_HelloworldServer
TARGET2 = ./bin/Demo01_HelloworldClient
TARGET3 = ./bin/Demo02_SendServer
TARGET4 = ./bin/Demo02_SendClient
TARGET5 = ./bin/Demo02_SendAndRequestServer
TARGET6 = ./bin/Demo02_SendAndRequestClient

.PHONY:all
all:prepare $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(TARGET5) $(TARGET6)

SRCS =  ./3rd/uuid4/src/uuid4.c \
	./3rd/sds/sds.c \
	./base/socketd.c \
	./base/meta.c \
	./base/param.c

OBJS = $(SRCS:.c=.o)

$(TARGET1):$(OBJS) ./examples/Demo01_HelloworldServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET2):$(OBJS) ./examples/Demo01_HelloworldClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET3):$(OBJS) ./examples/Demo02_SendServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET4):$(OBJS) ./examples/Demo02_SendClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET5):$(OBJS) ./examples/Demo02_SendAndRequestServer.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET6):$(OBJS) ./examples/Demo02_SendAndRequestClient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

%.o:%.c
	$(CC) $(CFLAGS) $(INCLUDE) -o $@ -c $<

clean:
	rm -rf $(OBJS) $(TARGET1) $(TARGET2) $(TARGET3) $(TARGET4) $(TARGET5) $(TARGET6) 

prepare:
	$(MKDIR) bin
