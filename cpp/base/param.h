#ifndef PARAM_H_
#define PARAM_H_

#include "list.h"

typedef struct sd_param_s {
    char* name;
    char* value;
    struct list_node node;
} sd_param_t;

sd_param_t* new_param(const char* name, const char* value);
void delete_param(sd_param_t* p);

#endif