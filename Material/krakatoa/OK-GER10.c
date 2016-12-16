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

void _A_m1( _class_A *this, int _n ){
}

int _A_getK( _class_A *this ){
}

typedef struct _St_B {
   int _k;
   Func *vt;
} _class_B;

_class_B *new_B(void);

void _B_m2( _class_B *this, int _n ){
}

int _B_getK( _class_B *this ){
}

typedef struct _St_C {
   Func *vt;
} _class_C;

_class_C *new_C(void);

void _C_m3( _class_C *this, int _n ){
}

void _C_m4( _class_C *this, int _n ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

