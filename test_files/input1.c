#include <stdio.h>
#include <string.h> 
#include <malloc.h> 
#include <stdbool.h> 

char * leggiStringa();
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
char * leggiStringa() { 
  char *buffer = malloc(sizeof(char) * 1000);
  scanf("%s" ,buffer);
    return buffer; 
} 

int main(  ){
char* la = "la";
char* ciao = "";
char* str = "";
float pippo = 10;
ciao = concatC_C(concatC_F(concatC_C("pippo","la",5,2),pippo,50,1),"34",50,2);
str = concatC_C("ciao","pippo",4,5);
return 0;
}
