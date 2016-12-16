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

void _A_run( _class_A *this ){
}

void _A_m( _class_A *this ){
}

typedef struct _St_B {
   Func *vt;
} _class_B;

_class_B *new_B(void);

int _B_run( _class_B *this, int _n ){
}

void _B_m( _class_B *this ){
}

