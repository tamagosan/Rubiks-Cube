package com.rcc.tamagosan.rubikscubecontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private boolean start = false, kotonaru = false, nidomehanai = false, play = false, barasu = false;
    public static boolean cColorFlag = false, cAnimationFlag = false, cChange, animation;
    public static int[][][] color = new int[6][3][3];
    public static int cc1, cc2;
    private int tesuucount = 0, i, j, k, clear;
    static long stcount;
    private Shuffle shuffle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        if (disp.getHeight() > 1800) setContentView(R.layout.activity_main_tablet);
            else setContentView(R.layout.activity_main_phone);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        State = (TextView) this.findViewById(R.id.State);
        Time = (TextView) this.findViewById(R.id.time);
        Tesuu = (TextView) this.findViewById(R.id.tesuu);
        Soroeru = (Button) this.findViewById(R.id.soroeru);
        Reset = (Button) this.findViewById(R.id.reset);

        Cube = (MyView) this.findViewById(R.id.view1);

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
        SharedPreferences pref = getSharedPreferences("color", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        cChange = pref.getBoolean("tamagosan", false);
        pref = getSharedPreferences("animation", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
        animation = pref.getBoolean("tamagosan", false);

        BTadapter = BluetoothAdapter.getDefaultAdapter();
        if (BTadapter == null) {
            State.setTextColor(Color.YELLOW);
            State.setText("Bluetooth未サポート");
        }

        Soroeru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != counttimer) {
                    counttimer.cancel();
                    counttimer = null;
                }
                if (null != barasutimer) {
                    barasutimer.cancel();
                    barasutimer = null;
                }
                if (barasu) {
                    try {
                        Thread.sleep(650);
                    } catch (InterruptedException e) {
                    }
                    barasu = false;
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
                }
                play = false;
                nidomehanai = false;
                start = false;
                kotonaru = false;
                tesuucount = 0;
                Time.setTextColor(Color.BLACK);
                Time.setText("00:00.00");
                Tesuu.setText("0");
                SndPacket[0] = 0x53;                // S
                SndPacket[1] = 0x43;                // C
                SndPacket[2] = 0x33;                // 3
                if (cChange) {
                    SndPacket[3] = 0x31;
                } else {
                    SndPacket[3] = 0x30;
                }
                SndPacket[4] = 0x45;                // E
                BTclient.write(SndPacket);
            }
        });
        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!barasu) {
                    barasu = true;
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
                    if (animation) {
                        Time.setTextColor(Color.BLACK);
                        Time.setText("Now Shuffling");
                        if (null != timer) {
                            timer.cancel();
                            timer = null;
                        }
                        barasutimer = new Timer();
                        btimerTask = new barasuTimerTask();
                        barasutimer.schedule(btimerTask, 0, 600);
                    } else {
                        shuffle = new Shuffle();
                        shuffle.rolling();
                        SndPacket[0] = 0x53;                // S
                        SndPacket[1] = 0x43;                // C
                        SndPacket[2] = 0x34;                // 4
                        for (i = 0; i < 6; i++) {
                            for (j = 0; j < 3; j++) {
                                SndPacket[i * 3 + j + 3] = (byte) (color[i][j][0] + color[i][j][1] * 6 + color[i][j][2] * 36);
                            }
                        }
                        SndPacket[21] = 0x45;                // E
                        BTclient.write(SndPacket);
                        for (i = 0; i < 22; i++) {
                            SndPacket[i] = 0;
                        }
                        Time.setTextColor(Color.RED);
                        Time.setText("Ready");
                        Tesuu.setText("0");
                        play = true;
                        barasu = false;
                    }
                }
            }
        });
    }

    class barasuTimerTask extends TimerTask {
        int count = 0, rollrand, pict;

        @Override
        public void run() {
            bhandler.post(new Runnable() {
                public void run() {
                    SndPacket[0] = 0x53;                // S
                    SndPacket[1] = 0x43;                // C
                    SndPacket[2] = 0x31;                // 1
                    do {
                        rollrand = (int) (Math.random() * 18);
                    }
                    while (pict + 1 >= rollrand && pict - 1 <= rollrand);
                    pict = rollrand;
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
                    Tesuu.setText(String.format("%d", 20 - count));
                    if (count == 20) {
                        play = true;
                        barasutimer.cancel();
                        barasutimer = null;
                        Time.setTextColor(Color.RED);
                        Time.setText("Ready");
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
                        barasu = false;
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
                            if (null != counttimer) {
                                counttimer.cancel();
                                counttimer = null;
                            }
                            if (null != barasutimer) {
                                barasutimer.cancel();
                                barasutimer = null;
                            }
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
                            if (cChange) {
                                SndPacket[3] = 0x31;
                            } else {
                                SndPacket[3] = 0x30;
                            }
                            SndPacket[4] = 0x45;                // E
                            BTclient.write(SndPacket);
                            play = false;
                            nidomehanai = false;
                            start = false;
                            kotonaru = false;
                            tesuucount = 0;
                            barasu = false;
                            Time.setTextColor(Color.BLACK);
                            Time.setText("00:00.00");
                            Tesuu.setText("0");
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
                    if (!barasu) Process();
                    break;
            }
            if (cColorFlag) {
                cColorFlag = false;
                if (cChange) {
                    for (i = 0; i < 6; i++) {
                        for (j = 0; j < 3; j++) {
                            for (k = 0; k < 3; k++) {
                                if (color[i][j][k] == 0) {
                                    color[i][j][k] = 2;
                                } else if (color[i][j][k] == 2) {
                                    color[i][j][k] = 4;
                                } else if (color[i][j][k] == 4) {
                                    color[i][j][k] = 0;
                                }
                            }
                        }
                    }
                } else {
                    for (i = 0; i < 6; i++) {
                        for (j = 0; j < 3; j++) {
                            for (k = 0; k < 3; k++) {
                                if (color[i][j][k] == 0) {
                                    color[i][j][k] = 4;
                                } else if (color[i][j][k] == 2) {
                                    color[i][j][k] = 0;
                                } else if (color[i][j][k] == 4) {
                                    color[i][j][k] = 2;
                                }
                            }
                        }
                    }
                }
                SndPacket[0] = 0x53;                // S
                SndPacket[1] = 0x43;                // C
                SndPacket[2] = 0x34;                // 4
                for (i = 0; i < 6; i++) {
                    for (j = 0; j < 3; j++) {
                        SndPacket[i * 3 + j + 3] = (byte) (color[i][j][0] + color[i][j][1] * 6 + color[i][j][2] * 36);
                    }
                }
                SndPacket[21] = 0x45;                // E
                BTclient.write(SndPacket);

                SharedPreferences pref = getSharedPreferences("color", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor e = pref.edit();
                e.putBoolean("tamagosan", cChange);
                e.commit();
            }
            if (cAnimationFlag) {
                cAnimationFlag = false;
                SharedPreferences pref = getSharedPreferences("animation", MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor e = pref.edit();
                e.putBoolean("tamagosan", animation);
                e.commit();
            }
        }
    };

    private void Process() {
        int rb;
        boolean rankin;
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
                    if (play) Time.setText(String.format("%1$02d:%2$02d.%3$02d", mm, ss, ms));
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
        if (item.getItemId() == R.id.menu_setting) {
            Intent intent = new Intent();
            intent.setClassName("com.rcc.tamagosan.rubikscubecontroller", "com.rcc.tamagosan.rubikscubecontroller.SettingActivity");
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
