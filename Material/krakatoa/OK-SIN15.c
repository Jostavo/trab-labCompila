#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_Test {
   Func *vt;
} _class_Test;

_class_Test *new_Test(void);

char *_Test_fakeClone( _class_Test *this, char *_s ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

