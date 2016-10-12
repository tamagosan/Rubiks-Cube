package com.rcc.tamagosan.rubikscubecontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final int CONNECTDEVICE = 1;
    public static final int ENABLEBLUETOOTH = 2;
    private BluetoothAdapter BTadapter;
    private BluetoothClient BTclient;
    private byte[] RcvPacket = new byte[32];
    private byte[] SndPacket = new byte[32];
    private byte[] nowjyoutai = new byte[32];
    private TextView State;
    static TextView Time,Tesuu;
    private Button Soroeru,Reset;
    private Timer barasutimer,counttimer;
    static Timer timer;
    private barasuTimerTask btimerTask;
    private countTimerTask ctimerTask;
    private Handler thandler = new Handler(),bhandler = new Handler();
    private boolean start=false,kotonaru=false;
    private boolean nidomehanai=false,play=false;
    private int tesuucount=0,i,j,clear;
    private int[][][] color=new int[6][3][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        State=(TextView)this.findViewById(R.id.State);
        Time=(TextView)this.findViewById(R.id.time);
        Tesuu=(TextView)this.findViewById(R.id.tesuu);
        Soroeru=(Button)this.findViewById(R.id.soroeru);
        Reset=(Button)this.findViewById(R.id.reset);
        BTadapter = BluetoothAdapter.getDefaultAdapter();
        if (BTadapter == null) {
            State.setTextColor(Color.YELLOW);
            State.setText("Bluetooth未サポート");
        }
        Soroeru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SndPacket[0] = 0x53;                // S
                SndPacket[1] = 0x43;                // C
                SndPacket[2] = 0x33;                // 3
                SndPacket[3] = 0x45;                // E
                BTclient.write(SndPacket);
                play = false;
                nidomehanai = false;
                start = false;
                kotonaru = false;
                tesuucount = 0;
                if (null != counttimer) {
                    counttimer.cancel();
                    counttimer = null;
                }
                Time.setTextColor(Color.BLACK);
                Time.setText("00:00.00");
                Tesuu.setText("0");
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != barasutimer) {
                    barasutimer.cancel();
                    barasutimer = null;
                }
                play=false;
                nidomehanai=false;
                start=false;
                kotonaru=false;
                tesuucount=0;
                if(null != counttimer){
                    counttimer.cancel();
                    counttimer = null;
                }
                Time.setTextColor(Color.BLACK);
                Time.setText("00:00.00");
                Tesuu.setText("0");
                barasutimer=new Timer();
                btimerTask = new barasuTimerTask();
                barasutimer.schedule(btimerTask, 0, 500);
            }
        });

    }
    class barasuTimerTask extends TimerTask {
        int count=0,rollrand;
        @Override
        public void run() {
            bhandler.post(new Runnable() {
                public void run() {
                    SndPacket[0]=0x53;                // S
                    SndPacket[1]=0x43;                // C
                    SndPacket[2]=0x31;                // 1
                    rollrand=(int)(Math.random()*18);
                    if(rollrand<10){
                        SndPacket[3]=48;
                        SndPacket[4]=(byte)(rollrand+48);
                    }else{
                        SndPacket[3]=49;
                        SndPacket[4]=(byte)(rollrand+38);
                    }
                    SndPacket[5]=0x45;                // E
                    BTclient.write(SndPacket);
                    count++;
                    if(count==20) {
                        play=true;
                        barasutimer.cancel();
                        barasutimer = null;
                        Time.setTextColor(Color.RED);
                        Time.setText("Ready");
                    }
                }
            });
        }
    }

    private final Handler shandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothClient.MESSAGE_STATECHANGE:
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTED:
                            State.setTextColor(Color.BLACK);
                            State.setText("接続完了");
                            timer = new Timer(true);
                            timer.schedule(new TimerTask(){
                                @Override
                                public void run(){
                                    SndPacket[0] = 0x53;				// S
                                    SndPacket[1] = 0x42;				// B
                                    SndPacket[2] = 0x45;				// E
                                    BTclient.write(SndPacket);
                                }
                            }, 1000, 500);
                            break;
                        case BluetoothClient.STATE_CONNECTING:
                            State.setTextColor(Color.BLACK);
                            State.setText("接続中");
                            break;
                        case BluetoothClient.STATE_NONE:
                            State.setTextColor(Color.RED);
                            State.setText("接続失敗");
                            break;
                    }
                    break;
                case BluetoothClient.MESSAGE_READ:
                    State.setText("送受信処理中");
                    RcvPacket = (byte[])msg.obj;
                    Process();
                    break;
            }
        }
    };

    private void Process(){
        if((RcvPacket[0] == 0x4D) && (RcvPacket[1] == 0x42)) {
            for(i=0;i<6;i++){
                for(j=0;j<3;j++){
                    color[i][j][0]=RcvPacket[i+j*6+2]%36;
                    color[i][j][0]=color[i][j][0]%6;
                    color[i][j][1]=RcvPacket[i+j*6+2]/6;
                    color[i][j][1]=color[i][j][1]%6;
                    color[i][j][2]=RcvPacket[i+j*6+2]/36;

                }
            }
            if (play) {
                for (i = 0; i < 18; i++) {
                    if (!nidomehanai) nowjyoutai[i] = RcvPacket[i + 2];
                    if (RcvPacket[i + 2] != nowjyoutai[i]) {
                        nowjyoutai[i] = RcvPacket[i + 2];
                        kotonaru = true;
                    }
                }
                nidomehanai = true;
                if (kotonaru) {

                    if (!start) {
                        Time.setTextColor(Color.BLACK);
                        start = true;
                        if (null != counttimer) {
                            counttimer.cancel();
                            counttimer = null;
                        }
                        counttimer = new Timer();
                        ctimerTask = new countTimerTask();
                        counttimer.schedule(ctimerTask, 0, 10);
                    }
                    tesuucount++;
                    Tesuu.setText(String.format("%d", tesuucount));
                    clear=0;
                    for(i=0;i<6;i++){
                        if(color[i][0][0]==color[i][0][1] && color[i][0][1]==color[i][0][2] && color[i][0][2]==color[i][1][0] && color[i][1][0]==color[i][1][1] && color[i][1][1]==color[i][1][2] && color[i][1][2]==color[i][2][0] && color[i][2][0]==color[i][2][1] && color[i][2][1]==color[i][2][2]){clear++;}
                    }
                    if(clear>=3){
                        play = false;
                        nidomehanai = false;
                        start = false;
                        kotonaru = false;
                        if (null != counttimer) {
                            counttimer.cancel();
                            counttimer = null;
                        }
                        SndPacket[0] = 0x53;				// S
                        SndPacket[1] = 0x43;				// C
                        SndPacket[2] = 0x32;				// 2
                        SndPacket[3] = 0x45;				// E
                        BTclient.write(SndPacket);
                    }

                    kotonaru = false;
                }

            }
        }
    }

    class countTimerTask extends TimerTask {
        int tcount=0;
        @Override
        public void run() {
            thandler.post(new Runnable() {
                public void run() {
                    tcount++;
                    long mm = tcount * 10 / 1000 / 60;
                    long ss = tcount * 10 / 1000 % 60;
                    long ms = (tcount * 10 - ss * 1000 - mm * 1000 * 60) / 10;
                    Time.setText(String.format("%1$02d:%2$02d.%3$02d", mm, ss, ms));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.menu_connect){
            Intent Intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(Intent, CONNECTDEVICE);
        }
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (BTadapter.isEnabled() == false) {
            Intent BTenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BTenable, ENABLEBLUETOOTH);
        }
        else {
            if (BTclient == null) {
                BTclient = new BluetoothClient(MainActivity.this, shandler);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BTclient != null) BTclient.stop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONNECTDEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.DEVICEADDRESS);
                    BluetoothDevice device = BTadapter.getRemoteDevice(address);
                    BTclient.connect(device);
                }
                break;
            case ENABLEBLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    BTclient = new BluetoothClient(this, handler);
                }
                else {
                    Toast.makeText(this, "Bluetoothのサポートなし", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private final Handler handler = new Handler() {
        // ハンドルメッセージごとの処理
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothClient.MESSAGE_STATECHANGE:
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTED:
                            State.setTextColor(Color.BLACK);
                            State.setText("接続完了");
                            break;
                        case BluetoothClient.STATE_CONNECTING:
                            State.setTextColor(Color.BLACK);
                            State.setText("接続中");
                            break;
                        case BluetoothClient.STATE_NONE:
                            State.setTextColor(Color.RED);
                            State.setText("接続失敗");
                            break;
                    }
                    break;
                case BluetoothClient.MESSAGE_READ:
                    State.setText("送受信処理中");
                    RcvPacket = (byte[])msg.obj;
                    break;
            }
        }
    };
}
