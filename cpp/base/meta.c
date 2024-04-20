#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>
#include "sds.h"
#include "hv/hbase.h"
#include "list.h"
#include "socketd.h"
#include "meta.h"

sd_meta_t* new_meta_len(const char* name, int namelen, const char* value, int valuelen) {
	if (name == NULL || value == NULL)
		return NULL;

	sd_meta_t* m = malloc(sizeof(sd_meta_t));
	if (m) {
		memset(m, 0, sizeof(sd_meta_t));
		m->name = sdsnewlen(name, namelen);
		m->value = sdsnewlen(value, valuelen);
	}

	return m;
}

sd_meta_t* new_meta(const char* name, const char* value) {
	if (name == NULL || value == NULL)
		return NULL;

	sd_meta_t* m = malloc(sizeof(sd_meta_t));
	if (m) {
		memset(m, 0, sizeof(sd_meta_t));
		m->name = sdsnew(name);
		m->value = sdsnew(value);
	}
	return m;
}

void delete_meta(sd_meta_t* m) {
	if (m) {
		if (m->name)	sdsfree(m->name);
		if (m->value)	sdsfree(m->value);
		free(m);
		m = NULL;
	}
}

void meta_list_init(sd_entity_t* entity) {
	list_init(&entity->metalist);
}

// TODO: free list
void meta_list_free(sd_entity_t* entity) {
	struct list_node* node;
	sd_meta_t* cur;
	list_for_each(node, &entity->metalist) {
		cur = list_entry(node, sd_meta_t, node);
		//delete_meta(cur);
		if (cur == NULL) printf("error meta_list_free()\n");
	}
}

void sd_put_meta_raw(sd_entity_t* entity, sd_meta_t* m) {
	list_add(&m->node, &entity->metalist);
}

void sd_put_meta(sd_entity_t* entity, const char* name, const char* value) {
	sd_meta_t* m = new_meta(name, value);
	sd_put_meta_raw(entity, m);
}

void sd_delete_meta(sd_meta_t* m) {
	list_del(&m->node);
	delete_meta(m);
}

void check_meta_list_valid(sd_entity_t* entity) {
	if (entity && entity->meta && list_empty(&entity->metalist)) {
		parse_meta_string(entity, entity->meta);
	}
}

const char* sd_meta(sd_entity_t* entity, const char* name) {
	check_meta_list_valid(entity);

	struct list_node* node;
	sd_meta_t* cur;
	list_for_each(node, &entity->metalist) {
		cur = list_entry(node, sd_meta_t, node);
		if (strcmp(name, cur->name) == 0) return cur->value;
	}
	return NULL;
}

int sd_meta_as_int(sd_entity_t* entity, const char* name) {
	const char* value = sd_meta(entity, name);
	if (value == NULL) return 0;
	return atoi(value);
}

long long sd_meta_as_long(sd_entity_t* entity, const char* name) {
	const char* value = sd_meta(entity, name);
	if (value == NULL) return 0L;
	return atoll(value);
}

double sd_meta_as_double(sd_entity_t* entity, const char* name) {
	const char* value = sd_meta(entity, name);
	if (value == NULL) return 0;
	return atof(value);
}

void parse_meta_string(sd_entity_t* entity, const char* meta) {
	int len = strlen(meta);
	char* str = malloc(len + 1);
	memset(str, 0, len + 1);
	strcpy(str, meta);
	char* p = "&"; //split
	char* s = NULL;
	for (s = strtok(str, p); s != NULL; s = strtok(NULL, p)) {
		char* q = strchr(s, '=');
		if (q) {
			int keylen = q - s;
			int valuelen = strlen(q + 1);
			sd_meta_t* meta = new_meta_len(s, keylen, q + 1, valuelen);
			sd_put_meta_raw(entity, meta);
		}
	}
	free(str);
}

// note: free memory by sdsfree()
char* format_meta_string(sd_entity_t* entity) {
	if (entity && list_empty(&entity->metalist))
		return NULL;

	sds buf = sdsempty();

	size_t i = 0;
	struct list_node* node;
	sd_meta_t* cur;
	list_for_each(node, &entity->metalist) {
		cur = list_entry(node, sd_meta_t, node);
		if (i++ > 0) buf = sdscat(buf, "&");
		buf = sdscatfmt(buf, "%s=%s", cur->name, cur->value);
	}

	return buf;
}