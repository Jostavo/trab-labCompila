#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_A {
   Func *vt;
} _class_A;

_class_A *new_A(void);

typedef struct _St_B {
   Func *vt;
} _class_B;

_class_B *new_B(void);

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

