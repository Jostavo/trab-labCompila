#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_A {
   int _n;
   Func *vt;
} _class_A;

_class_A *new_A(void);

int _A_get( _class_A *this ){
}

void _A_set( _class_A *this, int _n ){
}

void _A_m1( _class_A *this ){
}

typedef struct _St_B {
   Func *vt;
} _class_B;

_class_B *new_B(void);

void _B_m2( _class_B *this ){
}

typedef struct _St_C {
   Func *vt;
} _class_C;

_class_C *new_C(void);

void _C_m1( _class_C *this ){
}

void _C_teste( _class_C *this ){
}

typedef struct _St_D {
   Func *vt;
} _class_D;

_class_D *new_D(void);

void _D_m1( _class_D *this ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

