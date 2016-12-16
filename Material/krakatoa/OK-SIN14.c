#include <malloc.h>
#include <stdlib.h>
#include <stdio.h>

typedef int boolean;
#define true 1
#define false 0

typedef void (*Func)();

typedef struct _St_Point {
   int _x;
   int _y;
   Func *vt;
} _class_Point;

_class_Point *new_Point(void);

void _Point_set( _class_Point *this, int _xint _y ){
}

int _Point_getX( _class_Point *this ){
}

int _Point_getY( _class_Point *this ){
}

