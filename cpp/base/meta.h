#ifndef META_H_
#define META_H_

#include "list.h"

typedef struct sd_meta_s {
    char* name;
    char* value;
    struct list_node node;
} sd_meta_t;

sd_meta_t* new_meta(const char* name, const char* value);
void delete_meta(sd_meta_t* m);

#endif