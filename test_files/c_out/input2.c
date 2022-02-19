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
float sommac(float,float,char*);

float differenzac(float,float);

float moltiplicazionec(float,float);

float divisionec(float,float);

float potenzac(float,float);

int c = 1;
float sommac(float a,float b,char* par){
float result = a+b;
printf(par);
printf("\n");
return result; 
}
float differenzac(float a,float b){
float result = a-b;
return result; 
}
float moltiplicazionec(float a,float b){
float result = a*b;
return result; 
}
float divisionec(float a,float b){
float result = a/b;
return result; 
}
float potenzac(float a,float b){
float result = pow(a,b);
return result; 
}
void stampa(char* messaggio){
int i = 1;
while(i<=4){
int incremento = 1;
printf("\n");
i = i+incremento;
}
printf(messaggio);
printf("\n");
}
int main(  ){
float a = 0, b = 0;
char* ans = "si";
char* op = "";
float risultato = 0;
char* pippo = "CIAOOOOOOOO";
while(strcmp(ans,"si")== 0){
printf("Scegli operazione 1 ADD 2 DIFF 3 MUL 4 DIV 5 POW\t");
op = (char*) malloc(sizeof(char));
scanf("%s",op);
printf("Inserisci il primo valore:\t");
scanf("%f",&a);
printf("Inserisci il secondo valore:\t");
scanf("%f",&b);
if(strcmp(op,"1")== 0){
risultato = sommac(a,b,pippo);
stampa(concatC_F(concatC_C(concatC_F(concatC_C(concatC_F("la somma di ",a,12,1),"e ",50,2),b,50,1),": ",50,2),risultato,50,1));
}
if(strcmp(op,"2")== 0){
risultato = differenzac(a,b);
stampa(concatC_F("la differenza di a e b è ",risultato,25,1));
}
if(strcmp(op,"3")== 0){
risultato = moltiplicazionec(a,b);
stampa(concatC_F("la moltiplicazione di a e b è ",risultato,30,1));
}
if(strcmp(op,"4")== 0){
risultato = divisionec(a,b);
stampa(concatC_F("la divisione di a e b è ",risultato,24,1));
}
if(strcmp(op,"5")== 0){
risultato = potenzac(a,b);
stampa(concatC_F("la potenza di a e b è ",risultato,22,1));
}
printf("Vuoi continuare ? si/no\t");
ans = (char*) malloc(sizeof(char));
scanf("%s",ans);
}
printf("\n");
printf("ciao");
return 0;
}
