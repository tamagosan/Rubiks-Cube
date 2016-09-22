#include <xc.h>

#define _XTAL_FREQ 32000000
#define Fosc 32

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

unsigned char RcvFlag;
unsigned char RcvBuf[32], SndBuf[32], Buffer[32];
int Index;
#define Max_Size 32

char SpiSend(char,char);
void Beep(char);
unsigned char getcUSART (void);
void Send(unsigned char txchar);
void SendStr(unsigned char * str);
void SendCmd(const unsigned char *cmd);
void RcvDisp(void);
void Process(void);
void delay_us(unsigned int usec);
void delay_ms(unsigned int msec);
void TRStart(void);
int  GetADC(int ch);
void Rolling(char);
void ColorPaint(void);

char color[6][3][3]={
    {{1,1,1},{1,1,1},{1,1,1}},
    {{4,4,4},{4,4,4},{4,4,4}},
    {{3,3,3},{3,3,3},{3,3,3}},
    {{2,2,2},{2,2,2},{2,2,2}},
    {{0,0,0},{0,0,0},{0,0,0}},
    {{5,5,5},{5,5,5},{5,5,5}},
    };
char rFlag=0;

//unsigned char msg1[] = "$$$";
//unsigned char msg2[] = "SF,1\r";
//unsigned char msg3[] = "SN,Rubiks_Cube\r";
//unsigned char msg4[] = "SA,4\r";
//unsigned char msg5[] = "R,1\r";

void main(void){
    char i,j,k,spiReceive;
    char BTConnected=0;
    int BTConnectCount=0;

    char RollingArray[6][12]={
        { 1, 0, 3, 2, 5, 4,17,16,15,14,13,12},
        {16,17,14,15,12,13,11,10, 9, 8, 7, 6},
        { 1, 0, 3, 2, 5, 4,11,10, 9, 8, 7, 6},
        {13,12,15,14,17,16,11,10, 9, 8, 7, 6},
        { 4, 5, 2, 3, 0, 1,11,10, 9, 8, 7, 6},
        { 1, 0, 3, 2, 5, 4,12,13,14,15,16,17}
    };
    if(eeprom_read(54)==0xcc){
        for(i=0;i<6;i++){
            for(j=0;j<3;j++){
                for(k=0;k<3;k++){
                    color[i][j][k]=eeprom_read(i*9+j*3+k);
                }
            }
        }
    }

    OSCCON=0xf0;
    TRISA=0x00;
    TRISB=0x00;
    TRISC=0b10010100;
    PORTA=0b00111111;
    PORTB=0b00111111;
    PORTC=0x00;
    ANSELA=0x00;
    ANSELB=0x00;

    SSPCON1=0b00100010;
    SSPSTAT=0b00000000;

    RC0=0;
    __delay_ms(100);
    RC0=1;
    __delay_ms(500);

    TXSTA=0x24;
    RCSTA=0x90;
    BAUDCON=0x08;
    SPBRG=68;

    __delay_ms(2000);
        //SendCmd(msg1);
        //SendCmd(msg2);
        //SendCmd(msg3);
        //SendCmd(msg4);
        //SendCmd(msg5);
        //__delay_ms(2000);

    TXSTA = 0x24;
    RCSTA = 0x90;

    ADCON0=0;
    ADCON1=0xE0;

    Index=0;
    RcvFlag=0;

    PIR1bits.RCIF=0;
    PIE1bits.RCIE=1;
    INTCONbits.PEIE=1;
    INTCONbits.GIE=1;

    Beep(0);

    ColorPaint();

    while(1){
        for(i=0;i<6;i++){
            spiReceive=SpiSend(i*2+1,0);
            if(spiReceive!=0){
                Rolling(RollingArray[i][spiReceive-1]);
            }
        }

        if(RcvFlag){
            RcvFlag = 0;

            if((Buffer[0]=='S')){
                SndBuf[0]='M';
                SndBuf[1]=Buffer[1];
                Process();
                Index=0;
            }
        }

        if(!RC2) BTConnectCount+=(!BTConnected);
        else{
            BTConnectCount=0;
            BTConnected=0;
        }
        if(BTConnectCount>5000){
            Beep(0);
            BTConnectCount=0;
            BTConnected=1;
        }
    }
}



void ColorPaint(void){
    unsigned char i,j,sendData;
    for(i=0;i<6;i++){
        for(j=0;j<3;j++){
            sendData=color[i][j][0];
            sendData+=color[i][j][1]*6;
            sendData+=color[i][j][2]*36;
            SpiSend(i*2,sendData);
            __delay_ms(1);
        }
    }
}

void SendCmd(const unsigned char *cmd){
    while(*cmd!=0) Send(*cmd++);
    __delay_ms(1000);
}

void Send(unsigned char txchar){
    while(!TXSTAbits.TRMT);
    TXREG=txchar;
}

void Process(void){
    char i,j,k,reset;

    switch(Buffer[1]){
        case 'B':
            for(i=0;i<6;i++){
                for(j=0;j<3;j++){
                    SndBuf[i+j*6+2]=color[i][j][0];
                    SndBuf[i+j*6+2]+=color[i][j][1]*6;
                    SndBuf[i+j*6+2]+=color[i][j][2]*36;
                }
            }
            SendStr(SndBuf);
            break;

        case 'C':
            switch(Buffer[2]){
                case '0':
                    color[Buffer[3]-48][Buffer[4]-48][Buffer[5]-48]=Buffer[6]-48;
                    ColorPaint();
                    break;
                case '1':
                    Rolling((Buffer[3]-48)*10+Buffer[4]-48);
                    rFlag=0;
                    break;
                case '2':
                    Beep(0);
                    break;
                case '3':
                    for(i=0;i<6;i++){
                        switch(i){
                            case 0:reset=1;break;
                            case 1:reset=4;break;
                            case 2:reset=3;break;
                            case 3:reset=2;break;
                            case 4:reset=0;break;
                            case 5:reset=5;break;
                        }
                        for(j=0;j<3;j++){
                            for(k=0;k<3;k++){
                                color[i][j][k]=reset;
                            }
                        }
                    }
                    ColorPaint();
                    break;
                default: break;
            }
            break;
        default: break;
    }
}

void interrupt isr(void){
    unsigned char data;
    int i;

    if(PIR1bits.RCIF){
        PIR1bits.RCIF=0;
        if((RCSTAbits.OERR) || (RCSTAbits.FERR)){
            data=RCREG;
            RCSTA=0;
            RCSTA=0x90;
        }else{
            if(Index < Max_Size){
                data=RCREG;
                if(data=='S')Index=0;
                RcvBuf[Index]=data;
                if(RcvBuf[Index]=='E'){
                    i=0;
                    while(i<=Index){
                        Buffer[i]=RcvBuf[i];
                        i++;
                    }
                    RcvFlag = 1;
                }
                Index++;
            }else{
                data=RCREG;
                Index=0;
            }
        }
    }
}


void SendStr(unsigned char * str){
    int i;

    for(i= 0; i<Max_Size; i++)
        Send(*str++);
}

void Beep(unsigned char tone){
    char i;
    for(i=0;i<200;i++){
        RC1=1;
        __delay_us(500);
        RC1=0;
        __delay_us(500);
    }
}

char SpiSend(char ssSelect,unsigned char sendData){
    switch(ssSelect){
        case 0:RB5=0;break;
        case 1:RB4=0;break;
        case 2:RB3=0;break;
        case 3:RB2=0;break;
        case 4:RB1=0;break;
        case 5:RB0=0;break;
        case 6:RA5=0;break;
        case 7:RA4=0;break;
        case 8:RA3=0;break;
        case 9:RA2=0;break;
        case 10:RA1=0;break;
        case 11:RA0=0;break;
    }
    SSPBUF=sendData;
    while(SSPSTATbits.BF==0);
    switch(ssSelect){
        case 0:RB5=1;break;
        case 1:RB4=1;break;
        case 2:RB3=1;break;
        case 3:RB2=1;break;
        case 4:RB1=1;break;
        case 5:RB0=1;break;
        case 6:RA5=1;break;
        case 7:RA4=1;break;
        case 8:RA3=1;break;
        case 9:RA2=1;break;
        case 10:RA1=1;break;
        case 11:RA0=1;break;
    }
    return SSPBUF;
}


void Rolling(char rollingNum){
    char mem,i,j,k;
    if(rFlag){
        rFlag=0;
        return;
    }
    rFlag=1;
    switch (rollingNum){
        case 0:
            for(i=0;i<3;i++){
                mem=color[5][0][0];
                color[5][0][0]=color[5][1][0];
                color[5][1][0]=color[5][2][0];
                color[5][2][0]=color[2][0][0];
                color[2][0][0]=color[2][1][0];
                color[2][1][0]=color[2][2][0];
                color[2][2][0]=color[0][0][0];
                color[0][0][0]=color[0][1][0];
                color[0][1][0]=color[0][2][0];
                color[0][2][0]=color[4][2][2];
                color[4][2][2]=color[4][1][2];
                color[4][1][2]=color[4][0][2];
                color[4][0][2]=mem;
                if(i!=1){
                    mem=color[3][0][0];
                    color[3][0][0]=color[3][0][1];
                    color[3][0][1]=color[3][0][2];
                    color[3][0][2]=color[3][1][2];
                    color[3][1][2]=color[3][2][2];
                    color[3][2][2]=color[3][2][1];
                    color[3][2][1]=color[3][2][0];
                    color[3][2][0]=color[3][1][0];
                    color[3][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 1:
            for(i=0;i<3;i++){
                mem=color[0][2][0];
                color[0][2][0]=color[0][1][0];
                color[0][1][0]=color[0][0][0];
                color[0][0][0]=color[2][2][0];
                color[2][2][0]=color[2][1][0];
                color[2][1][0]=color[2][0][0];
                color[2][0][0]=color[5][2][0];
                color[5][2][0]=color[5][1][0];
                color[5][1][0]=color[5][0][0];
                color[5][0][0]=color[4][0][2];
                color[4][0][2]=color[4][1][2];
                color[4][1][2]=color[4][2][2];
                color[4][2][2]=mem;
                if(i!=1){
                    mem=color[3][0][0];
                    color[3][0][0]=color[3][1][0];
                    color[3][1][0]=color[3][2][0];
                    color[3][2][0]=color[3][2][1];
                    color[3][2][1]=color[3][2][2];
                    color[3][2][2]=color[3][1][2];
                    color[3][1][2]=color[3][0][2];
                    color[3][0][2]=color[3][0][1];
                    color[3][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 2:
            for(i=0;i<3;i++){
                mem=color[5][0][1];
                color[5][0][1]=color[5][1][1];
                color[5][1][1]=color[5][2][1];
                color[5][2][1]=color[2][0][1];
                color[2][0][1]=color[2][1][1];
                color[2][1][1]=color[2][2][1];
                color[2][2][1]=color[0][0][1];
                color[0][0][1]=color[0][1][1];
                color[0][1][1]=color[0][2][1];
                color[0][2][1]=color[4][2][1];
                color[4][2][1]=color[4][1][1];
                color[4][1][1]=color[4][0][1];
                color[4][0][1]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 3:
            for(i=0;i<3;i++){
                mem=color[0][2][1];
                color[0][2][1]=color[0][1][1];
                color[0][1][1]=color[0][0][1];
                color[0][0][1]=color[2][2][1];
                color[2][2][1]=color[2][1][1];
                color[2][1][1]=color[2][0][1];
                color[2][0][1]=color[5][2][1];
                color[5][2][1]=color[5][1][1];
                color[5][1][1]=color[5][0][1];
                color[5][0][1]=color[4][0][1];
                color[4][0][1]=color[4][1][1];
                color[4][1][1]=color[4][2][1];
                color[4][2][1]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 4:
            for(i=0;i<3;i++){
                mem=color[5][0][2];
                color[5][0][2]=color[5][1][2];
                color[5][1][2]=color[5][2][2];
                color[5][2][2]=color[2][0][2];
                color[2][0][2]=color[2][1][2];
                color[2][1][2]=color[2][2][2];
                color[2][2][2]=color[0][0][2];
                color[0][0][2]=color[0][1][2];
                color[0][1][2]=color[0][2][2];
                color[0][2][2]=color[4][2][0];
                color[4][2][0]=color[4][1][0];
                color[4][1][0]=color[4][0][0];
                color[4][0][0]=mem;
                if(i!=1){
                    mem=color[1][0][0];
                    color[1][0][0]=color[1][1][0];
                    color[1][1][0]=color[1][2][0];
                    color[1][2][0]=color[1][2][1];
                    color[1][2][1]=color[1][2][2];
                    color[1][2][2]=color[1][1][2];
                    color[1][1][2]=color[1][0][2];
                    color[1][0][2]=color[1][0][1];
                    color[1][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 5:
            for(i=0;i<3;i++){
                mem=color[0][2][2];
                color[0][2][2]=color[0][1][2];
                color[0][1][2]=color[0][0][2];
                color[0][0][2]=color[2][2][2];
                color[2][2][2]=color[2][1][2];
                color[2][1][2]=color[2][0][2];
                color[2][0][2]=color[5][2][2];
                color[5][2][2]=color[5][1][2];
                color[5][1][2]=color[5][0][2];
                color[5][0][2]=color[4][0][0];
                color[4][0][0]=color[4][1][0];
                color[4][1][0]=color[4][2][0];
                color[4][2][0]=mem;
                if(i!=1){
                    mem=color[1][0][0];
                    color[1][0][0]=color[1][0][1];
                    color[1][0][1]=color[1][0][2];
                    color[1][0][2]=color[1][1][2];
                    color[1][1][2]=color[1][2][2];
                    color[1][2][2]=color[1][2][1];
                    color[1][2][1]=color[1][2][0];
                    color[1][2][0]=color[1][1][0];
                    color[1][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 6:
            for(i=0;i<3;i++){
                mem=color[3][2][0];
                color[3][2][0]=color[3][2][1];
                color[3][2][1]=color[3][2][2];
                color[3][2][2]=color[2][2][0];
                color[2][2][0]=color[2][2][1];
                color[2][2][1]=color[2][2][2];
                color[2][2][2]=color[1][2][0];
                color[1][2][0]=color[1][2][1];
                color[1][2][1]=color[1][2][2];
                color[1][2][2]=color[4][2][0];
                color[4][2][0]=color[4][2][1];
                color[4][2][1]=color[4][2][2];
                color[4][2][2]=mem;
                if(i!=1){
                    mem=color[0][0][0];
                    color[0][0][0]=color[0][0][1];
                    color[0][0][1]=color[0][0][2];
                    color[0][0][2]=color[0][1][2];
                    color[0][1][2]=color[0][2][2];
                    color[0][2][2]=color[0][2][1];
                    color[0][2][1]=color[0][2][0];
                    color[0][2][0]=color[0][1][0];
                    color[0][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 7:
            for(i=0;i<3;i++){
                mem=color[4][2][2];
                color[4][2][2]=color[4][2][1];
                color[4][2][1]=color[4][2][0];
                color[4][2][0]=color[1][2][2];
                color[1][2][2]=color[1][2][1];
                color[1][2][1]=color[1][2][0];
                color[1][2][0]=color[2][2][2];
                color[2][2][2]=color[2][2][1];
                color[2][2][1]=color[2][2][0];
                color[2][2][0]=color[3][2][2];
                color[3][2][2]=color[3][2][1];
                color[3][2][1]=color[3][2][0];
                color[3][2][0]=mem;
                if(i!=1){
                    mem=color[0][0][0];
                    color[0][0][0]=color[0][1][0];
                    color[0][1][0]=color[0][2][0];
                    color[0][2][0]=color[0][2][1];
                    color[0][2][1]=color[0][2][2];
                    color[0][2][2]=color[0][1][2];
                    color[0][1][2]=color[0][0][2];
                    color[0][0][2]=color[0][0][1];
                    color[0][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 8:
            for(i=0;i<3;i++){
                mem=color[3][1][0];
                color[3][1][0]=color[3][1][1];
                color[3][1][1]=color[3][1][2];
                color[3][1][2]=color[2][1][0];
                color[2][1][0]=color[2][1][1];
                color[2][1][1]=color[2][1][2];
                color[2][1][2]=color[1][1][0];
                color[1][1][0]=color[1][1][1];
                color[1][1][1]=color[1][1][2];
                color[1][1][2]=color[4][1][0];
                color[4][1][0]=color[4][1][1];
                color[4][1][1]=color[4][1][2];
                color[4][1][2]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 9:
            for(i=0;i<3;i++){
                mem=color[4][1][2];
                color[4][1][2]=color[4][1][1];
                color[4][1][1]=color[4][1][0];
                color[4][1][0]=color[1][1][2];
                color[1][1][2]=color[1][1][1];
                color[1][1][1]=color[1][1][0];
                color[1][1][0]=color[2][1][2];
                color[2][1][2]=color[2][1][1];
                color[2][1][1]=color[2][1][0];
                color[2][1][0]=color[3][1][2];
                color[3][1][2]=color[3][1][1];
                color[3][1][1]=color[3][1][0];
                color[3][1][0]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 10:
            for(i=0;i<3;i++){
                mem=color[3][0][0];
                color[3][0][0]=color[3][0][1];
                color[3][0][1]=color[3][0][2];
                color[3][0][2]=color[2][0][0];
                color[2][0][0]=color[2][0][1];
                color[2][0][1]=color[2][0][2];
                color[2][0][2]=color[1][0][0];
                color[1][0][0]=color[1][0][1];
                color[1][0][1]=color[1][0][2];
                color[1][0][2]=color[4][0][0];
                color[4][0][0]=color[4][0][1];
                color[4][0][1]=color[4][0][2];
                color[4][0][2]=mem;
                if(i!=1){
                    mem=color[5][0][0];
                    color[5][0][0]=color[5][1][0];
                    color[5][1][0]=color[5][2][0];
                    color[5][2][0]=color[5][2][1];
                    color[5][2][1]=color[5][2][2];
                    color[5][2][2]=color[5][1][2];
                    color[5][1][2]=color[5][0][2];
                    color[5][0][2]=color[5][0][1];
                    color[5][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 11:
            for(i=0;i<3;i++){
                mem=color[4][0][2];
                color[4][0][2]=color[4][0][1];
                color[4][0][1]=color[4][0][0];
                color[4][0][0]=color[1][0][2];
                color[1][0][2]=color[1][0][1];
                color[1][0][1]=color[1][0][0];
                color[1][0][0]=color[2][0][2];
                color[2][0][2]=color[2][0][1];
                color[2][0][1]=color[2][0][0];
                color[2][0][0]=color[3][0][2];
                color[3][0][2]=color[3][0][1];
                color[3][0][1]=color[3][0][0];
                color[3][0][0]=mem;
                if(i!=1){
                    mem=color[5][0][0];
                    color[5][0][0]=color[5][0][1];
                    color[5][0][1]=color[5][0][2];
                    color[5][0][2]=color[5][1][2];
                    color[5][1][2]=color[5][2][2];
                    color[5][2][2]=color[5][2][1];
                    color[5][2][1]=color[5][2][0];
                    color[5][2][0]=color[5][1][0];
                    color[5][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 12:
            for(i=0;i<3;i++){
                mem=color[0][2][0];
                color[0][2][0]=color[0][2][1];
                color[0][2][1]=color[0][2][2];
                color[0][2][2]=color[1][2][2];
                color[1][2][2]=color[1][1][2];
                color[1][1][2]=color[1][0][2];
                color[1][0][2]=color[5][0][2];
                color[5][0][2]=color[5][0][1];
                color[5][0][1]=color[5][0][0];
                color[5][0][0]=color[3][0][0];
                color[3][0][0]=color[3][1][0];
                color[3][1][0]=color[3][2][0];
                color[3][2][0]=mem;
                if(i!=1){
                    mem=color[4][0][0];
                    color[4][0][0]=color[4][0][1];
                    color[4][0][1]=color[4][0][2];
                    color[4][0][2]=color[4][1][2];
                    color[4][1][2]=color[4][2][2];
                    color[4][2][2]=color[4][2][1];
                    color[4][2][1]=color[4][2][0];
                    color[4][2][0]=color[4][1][0];
                    color[4][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 13:
            for(i=0;i<3;i++){
                mem=color[0][2][2];
                color[0][2][2]=color[0][2][1];
                color[0][2][1]=color[0][2][0];
                color[0][2][0]=color[3][2][0];
                color[3][2][0]=color[3][1][0];
                color[3][1][0]=color[3][0][0];
                color[3][0][0]=color[5][0][0];
                color[5][0][0]=color[5][0][1];
                color[5][0][1]=color[5][0][2];
                color[5][0][2]=color[1][0][2];
                color[1][0][2]=color[1][1][2];
                color[1][1][2]=color[1][2][2];
                color[1][2][2]=mem;
                if(i!=1){
                    mem=color[4][0][0];
                    color[4][0][0]=color[4][1][0];
                    color[4][1][0]=color[4][2][0];
                    color[4][2][0]=color[4][2][1];
                    color[4][2][1]=color[4][2][2];
                    color[4][2][2]=color[4][1][2];
                    color[4][1][2]=color[4][0][2];
                    color[4][0][2]=color[4][0][1];
                    color[4][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 14:
            for(i=0;i<3;i++){
                mem=color[0][1][0];
                color[0][1][0]=color[0][1][1];
                color[0][1][1]=color[0][1][2];
                color[0][1][2]=color[1][2][1];
                color[1][2][1]=color[1][1][1];
                color[1][1][1]=color[1][0][1];
                color[1][0][1]=color[5][1][2];
                color[5][1][2]=color[5][1][1];
                color[5][1][1]=color[5][1][0];
                color[5][1][0]=color[3][0][1];
                color[3][0][1]=color[3][1][1];
                color[3][1][1]=color[3][2][1];
                color[3][2][1]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 15:
            for(i=0;i<3;i++){
                mem=color[0][1][2];
                color[0][1][2]=color[0][1][1];
                color[0][1][1]=color[0][1][0];
                color[0][1][0]=color[3][2][1];
                color[3][2][1]=color[3][1][1];
                color[3][1][1]=color[3][0][1];
                color[3][0][1]=color[5][1][0];
                color[5][1][0]=color[5][1][1];
                color[5][1][1]=color[5][1][2];
                color[5][1][2]=color[1][0][1];
                color[1][0][1]=color[1][1][1];
                color[1][1][1]=color[1][2][1];
                color[1][2][1]=mem;
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 16:
            for(i=0;i<3;i++){
                mem=color[0][0][0];
                color[0][0][0]=color[0][0][1];
                color[0][0][1]=color[0][0][2];
                color[0][0][2]=color[1][2][0];
                color[1][2][0]=color[1][1][0];
                color[1][1][0]=color[1][0][0];
                color[1][0][0]=color[5][2][2];
                color[5][2][2]=color[5][2][1];
                color[5][2][1]=color[5][2][0];
                color[5][2][0]=color[3][0][2];
                color[3][0][2]=color[3][1][2];
                color[3][1][2]=color[3][2][2];
                color[3][2][2]=mem;
                if(i!=1){
                    mem=color[2][0][0];
                    color[2][0][0]=color[2][1][0];
                    color[2][1][0]=color[2][2][0];
                    color[2][2][0]=color[2][2][1];
                    color[2][2][1]=color[2][2][2];
                    color[2][2][2]=color[2][1][2];
                    color[2][1][2]=color[2][0][2];
                    color[2][0][2]=color[2][0][1];
                    color[2][0][1]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;
        case 17:
            for(i=0;i<3;i++){
                mem=color[0][0][2];
                color[0][0][2]=color[0][0][1];
                color[0][0][1]=color[0][0][0];
                color[0][0][0]=color[3][2][2];
                color[3][2][2]=color[3][1][2];
                color[3][1][2]=color[3][0][2];
                color[3][0][2]=color[5][2][0];
                color[5][2][0]=color[5][2][1];
                color[5][2][1]=color[5][2][2];
                color[5][2][2]=color[1][0][0];
                color[1][0][0]=color[1][1][0];
                color[1][1][0]=color[1][2][0];
                color[1][2][0]=mem;
                if(i!=1){
                    mem=color[2][0][0];
                    color[2][0][0]=color[2][0][1];
                    color[2][0][1]=color[2][0][2];
                    color[2][0][2]=color[2][1][2];
                    color[2][1][2]=color[2][2][2];
                    color[2][2][2]=color[2][2][1];
                    color[2][2][1]=color[2][2][0];
                    color[2][2][0]=color[2][1][0];
                    color[2][1][0]=mem;
                }
                ColorPaint();
                __delay_ms(70);
            }
            break;

    }
    for(i=0;i<6;i++){
        for(j=0;j<3;j++){
            for(k=0;k<3;k++){
                eeprom_write(i*9+j*3+k,color[i][j][k]);
            }
        }
    }
    eeprom_write(54,0xcc);
}
