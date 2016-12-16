#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_A {
   int _k;
   Func *vt;
} _class_A;

_class_A *new_A(void);

int _A_get_A( _class_A *this ){
}

void _A_init( _class_A *this ){
}

typedef struct _St_B {
   int _k;
   Func *vt;
} _class_B;

_class_B *new_B(void);

int _B_get_B( _class_B *this ){
}

void _B_init( _class_B *this ){
}

typedef struct _St_C {
   int _k;
   Func *vt;
} _class_C;

_class_C *new_C(void);

int _C_get_C( _class_C *this ){
}

void _C_init( _class_C *this ){
}

typedef struct _St_D {
   int _k;
   Func *vt;
} _class_D;

_class_D *new_D(void);

int _D_get_D( _class_D *this ){
}

void _D_init( _class_D *this ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

