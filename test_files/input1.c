#include <stdio.h>
#include <string.h> 
#include <malloc.h> 
#include <stdbool.h> 

null c = 1;
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
null i = 1;
while(i<=4){
null println("");
i = i+incremento;
}
println(messaggio);
}
int main(  ){
null a = 0, b = 0;
null ans = "si";
null op = 0;
float risultato = 0;
while(strcmp(ans,"si")== 0){
printf("Scegli operazione 1 ADD 2 DIFF 3 MUL 4 DIV 5 POW"+ "\t");
scanf("%d",&op);
printf("Inserisci il primo valore:"+ "\t");
scanf("%d",&a);
printf("Inserisci il secondo valore:"+ "\t");
scanf("%d",&b);
if(op==1){
(a,b);
stampa(stampa();
}
if(op==2){
(a,b);
stampa(stampa();
}
if(op==3){
(a,b);
stampa(stampa();
}
if(op==4){
(a,b);
stampa(stampa();
}
if(op==5){
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
