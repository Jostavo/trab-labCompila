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

int _A_put( _class_A *this, int _nchar *_set ){
}

typedef struct _St_Program {
   Func *vt;
} _class_Program;

_class_Program *new_Program(void);

void _Program_run( _class_Program *this ){
}

