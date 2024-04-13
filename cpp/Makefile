# Makefile of socketd

include Makefile.vars

CC = gcc
CFLAGS = -Wall
INCLUDE = -I./base -I./examples -I./3rd/libhv/include -I./3rd/sds -I./3rd/uuid4/src
LIBDIR = -L./3rd/libhv/lib
HV_LIB = -lhv

TARGET1 = ./bin/connect_ack_tcpserver
TARGET2 = ./bin/connect_ack_tcpclient

.PHONY:all
all:prepare $(TARGET1) $(TARGET2)

SRCS =  ./3rd/uuid4/src/uuid4.c \
	./3rd/sds/sds.c \
	./base/socketd.c

OBJS = $(SRCS:.c=.o)

$(TARGET1):$(OBJS) ./examples/connect_ack_tcpserver.c ./base/tcp_server.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

$(TARGET2):$(OBJS) ./examples/connect_ack_tcpclient.c ./base/tcp_client.c
	@echo TARGET:$@
	@echo OBJECTS:$^
	$(CC) $(CFLAGS) $(INCLUDE) $(LIBDIR) $(HV_LIB) -o $@ $^

%.o:%.c
	$(CC) $(CFLAGS) $(INCLUDE) -o $@ -c $<

clean:
	rm -rf $(TARGET1) $(TARGET2) $(OBJS)

prepare:
	$(MKDIR) bin
