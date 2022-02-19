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
char* visualizzaMenu(char**,int*,float*);

char* visualizzaMenu(char** pippo,int* input,float* giorgio) {

printf(pippo);
printf("\n");
printf(input);
printf("\n");
printf(giorgio);
printf("\n");
return "ciao inserisci un nuomero"; 
}
int main(  ){
int input;
float giorgio = 4.4;
char* miao = "MIAOOOOO";
visualizzaMenu(miao,&input,&giorgio);
return 0;
}
