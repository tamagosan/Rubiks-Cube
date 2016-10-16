package com.rcc.tamagosan.rubikscubecontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
    static TextView Time, Tesuu;
    private Button Soroeru, Reset;
    private Timer barasutimer, counttimer;
    static Timer timer;
    private barasuTimerTask btimerTask;
    private countTimerTask ctimerTask;
    private Handler thandler = new Handler(), bhandler = new Handler(), phandler = new Handler();
    private static MyView Cube;
    private boolean start = false, kotonaru = false, nidomehanai = false, play = false;
    public static int[][][] color = new int[6][3][3];
    public static int cc1, cc2;
    private int tesuucount = 0, i, j, k, clear;
    static long stcount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*RankingActivity rank = new RankingActivity();
        for (j = 0; j < 5; j++) {
            for (k = 0; k < 4; k++) {
                SharedPreferences pref = getSharedPreferences(String.format("rank%d_%d", k, j), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor e = pref.edit();
                e.putLong("tamagosan", 100000);
                e.commit();
            }
        }*/

        State = (TextView) this.findViewById(R.id.State);
        Time = (TextView) this.findViewById(R.id.time);
        Tesuu = (TextView) this.findViewById(R.id.tesuu);
        Soroeru = (Button) this.findViewById(R.id.soroeru);
        Reset = (Button) this.findViewById(R.id.reset);

        RankingActivity rank = new RankingActivity();
        for (i = 0; i < 5; i++) {
            for (j = 0; j < 4; j++) {
                SharedPreferences pref = getSharedPreferences(String.format("rank%d_%d", j, i), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                if (j % 2 == 0) {
                    rank.score[j][i] = pref.getLong("tamagosan", 60000);
                } else {
                    rank.score[j][i] = pref.getLong("tamagosan", 1000);
                }
            }
        }

        BTadapter = BluetoothAdapter.getDefaultAdapter();
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
                if (null != barasutimer) {
                    barasutimer.cancel();
                    barasutimer = null;
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
                barasutimer = new Timer();
                btimerTask = new barasuTimerTask();
                barasutimer.schedule(btimerTask, 0, 500);
            }
        });

    }

    class barasuTimerTask extends TimerTask {
        int count = 0, rollrand;

        @Override
        public void run() {
            bhandler.post(new Runnable() {
                public void run() {
                    SndPacket[0] = 0x53;                // S
                    SndPacket[1] = 0x43;                // C
                    SndPacket[2] = 0x31;                // 1
                    rollrand = (int) (Math.random() * 18);
                    if (rollrand < 10) {
                        SndPacket[3] = 48;
                        SndPacket[4] = (byte) (rollrand + 48);
                    } else {
                        SndPacket[3] = 49;
                        SndPacket[4] = (byte) (rollrand + 38);
                    }
                    SndPacket[5] = 0x45;                // E
                    BTclient.write(SndPacket);
                    count++;
                    if (count == 20) {
                        play = true;
                        barasutimer.cancel();
                        barasutimer = null;
                        Time.setTextColor(Color.RED);
                        Time.setText("Ready");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
        }
    }

    private final Handler shandler = new Handler() {
        // ハンドルメッセージごとの処理
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothClient.MESSAGE_STATECHANGE:
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTED:
                            State.setTextColor(Color.BLACK);
                            State.setText("接続完了");
                            timer = new Timer(true);
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    SndPacket[0] = 0x53;                // S
                                    SndPacket[1] = 0x42;                // B
                                    SndPacket[2] = 0x45;                // E
                                    BTclient.write(SndPacket);
                                }
                            }, 1000, 500);
                            SndPacket[0] = 0x53;                // S
                            SndPacket[1] = 0x43;                // C
                            SndPacket[2] = 0x33;                // 3
                            SndPacket[3] = 0x45;                // E
                            BTclient.write(SndPacket);
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
                    RcvPacket = (byte[]) msg.obj;
                    if (null == barasutimer) Process();
                    break;
            }
        }
    };

    private void Process() {
        int rb;
        boolean rankin;
        Cube = (MyView) this.findViewById(R.id.view1);
        if ((RcvPacket[0] == 0x4D) && (RcvPacket[1] == 0x42)) {
            for (i = 0; i < 6; i++) {
                for (j = 0; j < 3; j++) {
                    rb = RcvPacket[j + i * 3 + 2] & 0xFF;

                    color[i][j][0] = rb % 36 % 6;
                    color[i][j][1] = rb / 6 % 6;
                    color[i][j][2] = rb / 36;
                }
            }
            phandler.post(new Runnable() {
                public void run() {
                    Cube.invalidate();
                }
            });
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
                    clear = 0;
                    for (i = 0; i < 6; i++) {
                        if (color[i][0][0] == color[i][0][1] && color[i][0][1] == color[i][0][2] && color[i][0][2] == color[i][1][0] && color[i][1][0] == color[i][1][1] && color[i][1][1] == color[i][1][2] && color[i][1][2] == color[i][2][0] && color[i][2][0] == color[i][2][1] && color[i][2][1] == color[i][2][2]) {
                            clear++;
                        }
                    }
                    if (clear == 6) {
                        play = false;
                        nidomehanai = false;
                        start = false;
                        if (null != counttimer) {
                            counttimer.cancel();
                            counttimer = null;
                        }
                        SndPacket[0] = 0x53;                // S
                        SndPacket[1] = 0x43;                // C
                        SndPacket[2] = 0x32;                // 2
                        SndPacket[3] = 0x45;                // E
                        BTclient.write(SndPacket);

                        RankingActivity rank = new RankingActivity();

                        rankin = false;
                        for (j = 0; j < 5; j++) {
                            if (stcount <= rank.score[0][j]) {
                                for (k = 3; k >= j; k--) {
                                    rank.score[0][k + 1] = rank.score[0][k];
                                    rank.score[1][k + 1] = rank.score[1][k];
                                }
                                rank.score[0][j] = stcount;
                                rank.score[1][j] = tesuucount;
                                rankin = true;
                                cc1 = j;
                                break;
                            }
                        }
                        for (j = 0; j < 5; j++) {
                            if (tesuucount <= rank.score[3][j]) {
                                for (k = 3; k >= j; k--) {
                                    rank.score[3][k + 1] = rank.score[3][k];
                                    rank.score[2][k + 1] = rank.score[2][k];
                                }
                                rank.score[3][j] = tesuucount;
                                rank.score[2][j] = stcount;
                                rankin = true;
                                cc2 = j;
                                break;
                            }
                        }

                        if (rankin) {
                            for (j = 0; j < 5; j++) {
                                for (k = 0; k < 4; k++) {
                                    SharedPreferences pref = getSharedPreferences(String.format("rank%d_%d", k, j), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                                    SharedPreferences.Editor e = pref.edit();
                                    e.putLong("tamagosan", rank.score[k][j]);
                                    e.commit();
                                }
                            }
                            rank.clearf = true;
                            Intent intent = new Intent();
                            intent.setClassName("com.rcc.tamagosan.rubikscubecontroller", "com.rcc.tamagosan.rubikscubecontroller.RankingActivity");
                            startActivity(intent);
                        }
                    }
                    clear = 0;
                    kotonaru = false;
                }

            }
        }
    }

    class countTimerTask extends TimerTask {
        long tcount = 0;

        @Override
        public void run() {
            thandler.post(new Runnable() {
                public void run() {
                    tcount++;
                    stcount = tcount;
                    long mm = tcount * 10 / 1000 / 60;
                    long ss = tcount * 10 / 1000 % 60;
                    long ms = (tcount * 10 - ss * 1000 - mm * 1000 * 60) / 10;
                    Time.setText(String.format("%1$02d:%2$02d.%3$02d", mm, ss, ms));
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_connect) {
            Intent Intent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(Intent, CONNECTDEVICE);
        }
        if (item.getItemId() == R.id.menu_ranking) {
            Intent intent = new Intent();
            intent.setClassName("com.rcc.tamagosan.rubikscubecontroller", "com.rcc.tamagosan.rubikscubecontroller.RankingActivity");
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BTadapter.isEnabled() == false) {
            Intent BTenable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(BTenable, ENABLEBLUETOOTH);
        } else {
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
                    BTclient = new BluetoothClient(this, shandler);
                } else {
                    Toast.makeText(this, "Bluetoothのサポートなし", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
