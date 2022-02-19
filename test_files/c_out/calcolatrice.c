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
char* visualizzaMenu();

int somma(int,int);

int sottrazione(int,int);

int DIV = 5, ADD = 2, SHOWMENU = 1, TIMES = 4, STOP = 0, MINUS = 3;
char* visualizzaMenu() {
return concatC_C(concatC_C(concatC_C(concatC_C(concatC_C(concatC_C("Digita uno dei seguenti numeri per svolgere un'operazione","0) Termina il programma",57,23),"1) Mostra il menù",260,17),"2) Effettua una somma fra due numeri",260,36),"3) Effettua una differenza fra due numeri",260,41),"4) Effettua una moltiplicazione fra due numeri",260,46),"5) Effettua una divisione fra due numeri",260,40); 
}
int somma(int x,int y) {

return x+y; 
}
void moltiplicazione(int x,int y,int* result) {

*result = x*y;
}
int sottrazione(int x,int y) {

int differenza = 0;
differenza = x-y;
return differenza; 
}
void divisione(int x,int y,float* result) {

*result = x/y;
}
int main(  ){
int result = 0;
float resultDiv = 0.0;
int input;
char* ERROR = "Valore di input non riconosciuto.";
printf(visualizzaMenu());
scanf("%d",&input);
while(input!=0){
if(input==SHOWMENU){
printf(visualizzaMenu());
scanf("%d",&input);
}
else{
if(input==ADD){
int x,y;
printf("Inserisci i due numeri:	");
scanf("%d",&x);
printf("\n");
scanf("%d",&y);
printf("\n");
printf(concatC_I(concatC_C(concatC_I(concatC_I(" + ",x,3,0),y,296,1)," = ",296,3),somma(x,y),296,1));
printf("\n");
printf("Scegli un'altra operazione: ");
scanf("%d",&input);
}
else{
if(input==MINUS){
int x,y;
printf("Inserisci i due numeri:	");
scanf("%d",&x);
printf("\n");
scanf("%d",&y);
printf("\n");
printf(concatC_I(concatC_C(concatC_I(concatC_I(" - ",x,3,0),y,332,1)," = ",332,3),sottrazione(x,y),332,1));
printf("\n");
printf("Scegli un'altra operazione: ");
scanf("%d",&input);
}
else{
if(input==TIMES){
int x,y;
printf("Inserisci i due numeri:	");
scanf("%d",&x);
printf("\n");
scanf("%d",&y);
printf("\n");
moltiplicazione(x,y,&result);
printf(concatC_I(concatC_C(concatC_I(concatC_I(" * ",x,3,0),y,368,1)," = ",368,3),result,368,1));
printf("\n");
printf("Scegli un'altra operazione: ");
scanf("%d",&input);
}
else{
if(input==DIV){
int x,y;
printf("Inserisci i due numeri:	");
scanf("%d",&x);
printf("\n");
scanf("%d",&y);
printf("\n");
divisione(x,y,&resultDiv);
printf(concatC_F(concatC_C(concatC_I(concatC_I(" div ",x,5,0),y,406,1)," = ",406,3),resultDiv,406,1));
printf("\n");
printf("Scegli un'altra operazione: ");
scanf("%d",&input);
}
else{
printf(ERROR);
printf("\n");
printf("Scegli un'operazione riconosciuta: ");
scanf("%d",&input);
}
}
}
}
}
}
return 0;
}
