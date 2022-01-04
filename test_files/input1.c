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
println("");
i = i+incremento;
}
println(messaggio);
}
int main(  ){
int a = 0, b = 0;
char* ans = "si";
char* op = "";
float risultato = 0;
while(strcmp(ans,"si")== 0){
printf("Scegli operazione 1 ADD 2 DIFF 3 MUL 4 DIV 5 POW"+ "\t");
op = malloc(sizeof(char));
scanf("%s",op);
printf("Inserisci il primo valore:"+ "\t");
scanf("%d",&a);
printf("Inserisci il secondo valore:"+ "\t");
scanf("%d",&b);
if(strcmp(op,"1")== 0){
(a,b);
stampa(stampa();
}
if(strcmp(op,"2")== 0){
(a,b);
stampa(stampa();
}
if(strcmp(op,"3")== 0){
(a,b);
stampa(stampa();
}
if(strcmp(op,"4")== 0){
(a,b);
stampa(stampa();
}
if(strcmp(op,"5")== 0){
(a,b);
stampa(stampa();
}
printf("Vuoi continuare ? si/no"+ "\t");
ans = malloc(sizeof(char));
scanf("%s",ans);
}
println("");
printf("ciao");
return 0;
}
