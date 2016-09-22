#include <xc.h>
#define _XTAL_FREQ 32000000

#pragma config FOSC = INTOSC
#pragma config WDTE = OFF
#pragma config PWRTE = OFF
#pragma config MCLRE = OFF
#pragma config CP = OFF
#pragma config CPD = OFF
#pragma config BOREN = OFF
#pragma config CLKOUTEN = OFF
#pragma config IESO = OFF
#pragma config FCMEN = OFF

#pragma config WRT = OFF
#pragma config PLLEN = ON
#pragma config STVREN = OFF
#pragma config BORV = LO
#pragma config LVP = OFF

char color[3][3]={{0,0,0},{0,0,0},{0,0,0}};
char receiveRotation=0;

ColorSet(char,char);

main(){
    char i,j;
    
    OSCCON=0xf0;
    TRISA=0x20;
    TRISB=0x12;
    PORTA=0x00;
    PORTB=0x00;
    ANSELA=0x00;
    ANSELB=0x00;

    SSP1CON1=0b00100100;
    SSP1STAT=0b00000000;
    SDO1SEL=0;
    SS1SEL=1;

    SSP1IF=0;
    SSP1IE=1;
    PEIE=1;
    GIE=1;

    while(1){
        for(i=0;i<3;i++){
            RB3=(i==0);
            RB6=(i==1);
            RB7=(i==2);
            for(j=0;j<50;j++){
                ColorSet(0,i);
                ColorSet(0,i);
                ColorSet(1,i);
            }
        }
    }
}

void interrupt InterSPI(){
   if(SSP1IF==1){
       color[receiveRotation][0]=(char)(SSP1BUF/36);
       color[receiveRotation][1]=(char)(SSP1BUF/6)%6;
       color[receiveRotation][2]=SSP1BUF%36%6;
       receiveRotation++;
       if(receiveRotation==3) receiveRotation=0;
       SSP1IF=0;
   }
}

ColorSet(char cs,char n){
    //0:white 1:red 2:green 3:blue 4:yellow 5:orange
    char cn=color[n][0];
    RA0=(cn!=2 & cn!=3);
    RA1=((cn==5 & cs) | cn==0 | cn==2 | cn==4);
    RA2=(cn==0 | cn==3);

    cn=color[n][1];
    RA3=(cn!=2 & cn!=3);
    RA4=((cn==5 & cs) | cn==0 | cn==2 | cn==4);
    RB5=(cn==0 | cn==3);

    cn=color[n][2];
    RA6=(cn!=2 & cn!=3);
    RA7=((cn==5 & cs) | cn==0 | cn==2 | cn==4);
    RB0=(cn==0 | cn==3);
	
	__delay_us(5);
}
