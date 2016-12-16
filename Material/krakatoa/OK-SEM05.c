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

void _A_set( _class_A *this, int _pn ){
}

int _A_get( _class_A *this ){
}

typedef struct _St_B {
   Func *vt;
} _class_B;

_class_B *new_B(void);

void _B_set( _class_B *this, int _pn ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

B _Program_m( _class_Program *this, A _a ){
}

A _Program_p( _class_Program *this, int _i ){
}

void _Program_run( _class_Program *this ){
}

