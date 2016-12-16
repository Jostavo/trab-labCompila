#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_A {
   int _i;
   int _j;
   Func *vt;
} _class_A;

_class_A *new_A(void);

void _A_init_A( _class_A *this ){
}

void _A_call_p( _class_A *this ){
}

void _A_call_q( _class_A *this ){
}

void _A_r( _class_A *this ){
}

void _A_s( _class_A *this ){
}

typedef struct _St_B {
   int _i;
   int _j;
   Func *vt;
} _class_B;

_class_B *new_B(void);

void _B_init_B( _class_B *this ){
}

void _B_call_p( _class_B *this ){
}

void _B_call_q( _class_B *this ){
}

void _B_r( _class_B *this ){
}

void _B_s( _class_B *this ){
}

typedef struct _St_C {
   int _i;
   int _j;
   Func *vt;
} _class_C;

_class_C *new_C(void);

void _C_init_C( _class_C *this ){
}

void _C_call_p( _class_C *this ){
}

void _C_call_q( _class_C *this ){
}

void _C_r( _class_C *this ){
}

void _C_s( _class_C *this ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

