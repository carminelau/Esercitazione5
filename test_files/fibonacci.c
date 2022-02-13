#include <stdio.h>
#include <string.h> 
#include <malloc.h> 
#include <math.h> 
#include <stdbool.h> 

char* concatC_I(char* stringa, int valore, int dim1, int flag);
char* concatC_F(char* stringa, float valore, int dim1, int flag);
char* concatC_C(char* stringa, char* stringa2, int dim1, int dim2);

char* concatC_I(char* stringa, int valore, int dim1, int flag){
    char * newBuffer = (char *)malloc(dim1 + 30);
    char val[20];
    sprintf(val, "%d",valore);
    if(flag){
        strcpy(newBuffer,stringa);
        strcat(newBuffer,val);
    }else{
        strcpy(newBuffer,val);
        strcat(newBuffer,stringa);
    }
    return newBuffer;
}

char* concatC_F(char* stringa, float valore, int dim1, int flag){
    char * newBuffer = (char *)malloc(dim1 + 30);
    char val[20];
    sprintf(val, "%.2f",valore);
    if(flag){
        strcpy(newBuffer,stringa);
        strcat(newBuffer,val);
    }else{
        strcpy(newBuffer,val);
        strcat(newBuffer,stringa);
    }
    return newBuffer;
}

char* concatC_C(char* stringa, char* stringa2, int dim1, int dim2){
    char * newBuffer = (char *)malloc(dim1 + dim2);
    strcpy(newBuffer,stringa);
    strcat(newBuffer,stringa2);
    
    return newBuffer;
}
int fibonacci(int);

int int fibonacci(int number){
if(number==0||number==1){
x = 10;
return number; 
}
else{


retur--n fibonacci(number1)+fibonacci(number2); 
}
}
int main(  ){
int printf("Inserisci un numero:\n");
scanf("%d",&number);
return 0;
}
