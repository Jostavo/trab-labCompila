#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_Person {
   char *_course;
   int _number;
   int _age;
   char *_name;
   Func *vt;
} _class_Person;

_class_Person *new_Person(void);

char *_Person_getCourse( _class_Person *this ){
}

void _Person_setCourse( _class_Person *this, char *_course ){
}

int _Person_getNumber( _class_Person *this ){
}

void _Person_setNumber( _class_Person *this, int _number ){
}

void _Person_init( _class_Person *this, char *_nameint _age ){
}

char *_Person_getName( _class_Person *this ){
}

int _Person_getAge( _class_Person *this ){
}

void _Person_print( _class_Person *this ){
}

