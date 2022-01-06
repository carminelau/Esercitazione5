#include <stdio.h>
#include <string.h> 
#include <malloc.h> 
#include <stdbool.h> 

int c = 1;
float sommac(float a,float b){
float result = a+b;
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
while(strcmp(ans,"si")== 0){
printf("Scegli operazione 1 ADD 2 DIFF 3 MUL 4 DIV 5 POW\t");
op = malloc(sizeof(char));
scanf("%s",op);
printf("Inserisci il primo valore:\t");
scanf("%f",&a);
printf("Inserisci il secondo valore:\t");
scanf("%f",&b);
if(strcmp(op,"1")== 0){
risultato = sommac(a,b);
char buffer[29];
snprintf(buffer, sizeof(buffer),"la somma di a e b è %f", risultato);
stampa(buffer);
}
if(strcmp(op,"2")== 0){
risultato = differenzac(a,b);
char buffer[34];
snprintf(buffer, sizeof(buffer),"la differenza di a e b è %f", risultato);
stampa(buffer);
}
if(strcmp(op,"3")== 0){
risultato = moltiplicazionec(a,b);
char buffer[39];
snprintf(buffer, sizeof(buffer),"la moltiplicazione di a e b è %f", risultato);
stampa(buffer);
}
if(strcmp(op,"4")== 0){
risultato = divisionec(a,b);
char buffer[33];
snprintf(buffer, sizeof(buffer),"la divisione di a e b è %f", risultato);
stampa(buffer);
}
if(strcmp(op,"5")== 0){
risultato = potenzac(a,b);
char buffer[31];
snprintf(buffer, sizeof(buffer),"la potenza di a e b è %f", risultato);
stampa(buffer);
}
printf("Vuoi continuare ? si/no\t");
ans = malloc(sizeof(char));
scanf("%s",ans);
}
printf("\n");
printf("ciao");
return 0;
}
