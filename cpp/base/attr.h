#ifndef ATTR_H_
#define ATTR_H_

#include "list.h"

typedef struct sd_attr_s {
    char* name;
    char* value;
    struct list_node node;
} sd_attr_t;

sd_attr_t* new_attr(const char* name, const char* value);
void delete_attr(sd_attr_t* a);

#endif