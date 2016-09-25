//PIC16F1827
#include <xc.h>

#define _XTAL_FREQ 32000000
#define _TOUCH_FIRST_TIMER_MAX 3000
#define _TOUCH_SECOND_TIMER_MAX 400

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

void main(){
    char i;
    char touchPort;
    char touchFirst,touchFirstFlag=0,touchSecondFlag=0;
    int touchFirstTimer=0,touchSecondTimer=_TOUCH_SECOND_TIMER_MAX;
    
    OSCCON=0xf0;
    TRISA=0xff;
    TRISB=0x32;
    PORTA=0x00;
    PORTB=0x00;
    ANSELA=0x00;
    ANSELB=0x00;

    SSP1CON1=0b00100100;
    SSP1STAT=0b00000000;
    SDO1SEL=0;
    SS1SEL=0;

    SSP1BUF=0x00;

    while(1){
        for(i=0;i<8;i++){
             touchPort=1 << i;
             if((PORTA & touchPort)==touchPort){
                 if(!touchFirstFlag && touchSecondTimer>=_TOUCH_SECOND_TIMER_MAX){
                     touchFirst=i;
                     touchFirstFlag=1;
                     touchFirstTimer=0;
                 }else if(i!=touchFirst && !touchSecondFlag && touchFirstTimer<_TOUCH_FIRST_TIMER_MAX){
                     touchSecondFlag=1;
                     switch(touchFirst*10+i){       //5 6 2
                         case 54:SSP1BUF=0x01;break;//1   7
                         case 45:SSP1BUF=0x02;break;//4 0 3
                         case 60:SSP1BUF=0x03;break;
                         case 06:SSP1BUF=0x04;break;
                         case 23:SSP1BUF=0x05;break;
                         case 32:SSP1BUF=0x06;break;
                         case 52:SSP1BUF=0x07;break;
                         case 25:SSP1BUF=0x08;break;
                         case 17:SSP1BUF=0x09;break;
                         case 71:SSP1BUF=0x0a;break;
                         case 43:SSP1BUF=0x0b;break;
                         case 34:SSP1BUF=0x0c;break;
                         default:
                             touchFirstFlag=0;
                             touchSecondFlag=0;
                             break;
                     }
                 }else{
                     touchSecondTimer=0;
                 }
             }
        }
        touchFirstTimer++;
        touchSecondTimer++;
        if(touchFirstTimer>_TOUCH_FIRST_TIMER_MAX){
            touchFirstTimer--;
            touchFirstFlag=0;
        }
        if(touchSecondTimer>_TOUCH_SECOND_TIMER_MAX){
            touchSecondTimer--;
            touchSecondFlag=0;
        }
    }
}
