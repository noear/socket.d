#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "sds.h"
#include "hv/hbase.h"
#include "list.h"
#include "socketd.h"
#include "attr.h"

sd_attr_t* new_attr(const char* name, const char* value) {
	if (name == NULL || value == NULL)
		return NULL;

	sd_attr_t* m = malloc(sizeof(sd_attr_t));
	if (m) {
		memset(m, 0, sizeof(sd_attr_t));
		m->name = sdsnew(name);
		m->value = sdsnew(value);
	}
	return m;
}

void delete_attr(sd_attr_t* p) {
	if (p) {
		if (p->name)	sdsfree(p->name);
		if (p->value)	sdsfree(p->value);
		free(p);
	}
}

void attr_list_init(sd_session_t* session) {
	list_init(&session->attrlist);
}

void sd_put_attr_raw(sd_session_t* session, sd_attr_t* attr) {
	list_add(&attr->node, &session->attrlist);
}

sd_attr_t* sd_put_attr(sd_session_t* session, const char* name, const char* value) {
	sd_attr_t* p = new_attr(name, value);
	sd_put_attr_raw(session, p);
	return p;
}

sd_attr_t* sd_put_attr_as_int(sd_session_t* session, const char* name, int value) {
	char s[100] = { 0 };
	sprintf(s, "%d", value);
	return sd_put_attr(session, name, s);
}

const char* sd_attr(sd_session_t* session, const char* name) {
	struct list_node* node;
	sd_attr_t* cur;
	list_for_each(node, &session->attrlist) {
		cur = list_entry(node, sd_attr_t, node);
		if (strcmp(name, cur->name) == 0)
			return cur->value;
	}
	return NULL;
}

const char* sd_attr_or_default(sd_session_t* session, const char* name, const char* dv) {
	const char* a = sd_attr(session, name);
	if (a == NULL) {
		sd_put_attr(session, name, dv);
		return dv;
	}
	return a;
}

int sd_attr_or_default_as_int(sd_session_t* session, const char* name, int dv) {
	const char* a = sd_attr(session, name);
	if (a == NULL) {
		char s[100] = { 0 };
		sprintf(s, "%d", dv);
		sd_attr_t* aitem = sd_put_attr(session, name, s);
		return atoi(aitem->value);
	}
	return atoi(a);
}

// TODO: free list
void attr_list_free(sd_session_t* session) {
	struct list_node* node;
	sd_attr_t* cur;
	list_for_each(node, &session->attrlist) {
		cur = list_entry(node, sd_attr_t, node);
		//delete_attr(cur);
		if (cur == NULL) printf("error attr_list_free()\n");
	}
}
