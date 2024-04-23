#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "sds.h"
#include "hv/hbase.h"
#include "list.h"
#include "socketd.h"
#include "param.h"

sd_param_t* new_param(const char* name, const char* value) {
	if (name == NULL || value == NULL)
		return NULL;

	sd_param_t* m = malloc(sizeof(sd_param_t));
	if (m) {
		memset(m, 0, sizeof(sd_param_t));
		m->name = sdsnew(name);
		m->value = sdsnew(value);
	}
	return m;
}

void delete_param(sd_param_t* p) {
	if (p) {
		if (p->name)	sdsfree(p->name);
		if (p->value)	sdsfree(p->value);
		free(p);
	}
}

void param_list_init(sd_session_t* session) {
	list_init(&session->paramlist);
}

void sd_put_param_raw(sd_session_t* session, sd_param_t* param) {
	list_add(&param->node, &session->paramlist);
}

void sd_put_param(sd_session_t* session, const char* name, const char* value) {
	sd_param_t* p = new_param(name, value);
	sd_put_param_raw(session, p);
}

const char* sd_param(sd_session_t* session, const char* name) {
	struct list_node* node;
	sd_param_t* cur;
	list_for_each(node, &session->paramlist) {
		cur = list_entry(node, sd_param_t, node);
		if (strcmp(name, cur->name) == 0)
			return cur->value;
	}
	return NULL;
}

// TODO: free list
void param_list_free(sd_session_t* session) {
	struct list_node* node;
	sd_param_t* cur;
	list_for_each(node, &session->paramlist) {
		cur = list_entry(node, sd_param_t, node);
		//delete_param(cur);
		if (cur == NULL) printf("error param_list_free()\n");
	}
}

// tcp://127.0.0.1:8602/?token=1b0VsGusEkddgr3d
// sd:tcp://127.0.0.1:8602/admin?u=noear&p=2
void parse_handshake_param(sd_session_t* session, sd_message_t* msg) {
    if (msg->entity.data && msg->entity.datalen) {
		char* buf = malloc(msg->entity.datalen + 1);
		memset(buf, 0, msg->entity.datalen + 1);
		strncpy(buf, msg->entity.data, msg->entity.datalen);

		strncpy(session->uri, msg->entity.data, msg->entity.datalen);

        char* pos = strstr(buf, "//");
        if (pos) {
            pos += 2;
            char* pathbegin = strchr(pos, '/');
            if (pathbegin) {
                char* pathend = strchr(pathbegin, '?');
                if (pathend) {
                    strncpy(session->path, pathbegin, pathend - pathbegin);
                }
            }
        }

        char* name = strchr(buf, '?');
        while (name) {
            char* value = strchr(++name, '=');
            if (value) {
                *value++ = '\0';
                char* end = strchr(value, '&');
                if (end) *end = '\0';
                sd_put_param(session, name, value);
				printf("param -> %s = %s\n", name, value);
                name = end;
            }
        }

		free(buf);
    }
}
