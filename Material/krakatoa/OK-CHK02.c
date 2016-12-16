#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_A {
   int _n;
   int _b;
   char *_s;
   Func *vt;
} _class_A;

_class_A *new_A(void);

void _A_m( _class_A *this ){
}

int _A_m_returns_boolean( _class_A *this ){
}

void _A_m_integer( _class_A *this, int _n ){
}

void _A_m_integer_boolean_String( _class_A *this, int _nint _bchar *_s ){
}

int _A_m_integer_returns_boolean( _class_A *this, int _n ){
}

int _A_m_integer_boolean_String_return( _class_A *this, int _nint _bchar *_s ){
}

typedef struct _St_B {
   Func *vt;
} _class_B;

_class_B *new_B(void);

typedef struct _St_C {
   char *_name;
   int _letter;
   int _n;
   int _time;
   Func *vt;
} _class_C;

_class_C *new_C(void);

void _C_method( _class_C *this ){
}

int _C_method_returns_boolean( _class_C *this ){
}

void _C_method_integer( _class_C *this, int _n ){
}

void _C_method_integer_boolean_String( _class_C *this, int _nint _bchar *_name ){
}

int _C_method_integer_returns_boolean( _class_C *this, int _n ){
}

int _C_method_integer_boolean_String_r( _class_C *this, int _nint _bchar *_name ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

