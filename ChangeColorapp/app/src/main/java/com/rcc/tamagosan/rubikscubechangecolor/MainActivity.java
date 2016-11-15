package com.rcc.tamagosan.rubikscubechangecolor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final int CONNECTDEVICE = 1;
    public static final int ENABLEBLUETOOTH = 2;
    private BluetoothAdapter BTadapter;
    private BluetoothClient BTclient;
    private byte[] RcvPacket = new byte[32];
    private byte[] SndPacket = new byte[32];
    private Handler phandler = new Handler();
    private MyView canvas;
    public static int[][][] color = new int[6][3][3];
    public static int select = 0;
    private int i, j, k;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        canvas = new MyView(this);
        setContentView(canvas);
        BTadapter = BluetoothAdapter.getDefaultAdapter();
        if (BTadapter == null) canvas.state = 5;

        for (i = 0; i < 6; i++) {
            for (j = 0; j < 3; j++) {
                for (k = 0; k < 3; k++) {
                    switch (i) {
                        case 0:
                            color[i][j][k] = 1;
                            break;
                        case 1:
                            color[i][j][k] = 2;
                            break;
                        case 2:
                            color[i][j][k] = 3;
                            break;
                        case 3:
                            color[i][j][k] = 4;
                            break;
                        case 4:
                            color[i][j][k] = 0;
                            break;
                        case 5:
                            color[i][j][k] = 5;
                            break;
                    }
                }
            }
        }

        phandler.post(new Runnable() {
            public void run() {
                canvas.invalidate();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int tx = (int) event.getX();
        int ty = (int) event.getY();
        int nx = 0, ny = 0;

        for (i = 0; i < 6; i++) {
            if (30 < tx && 30 + i * canvas.h / 8 < ty && 30 + canvas.h / 8 - 2 > tx && 30 + (i + 1) * canvas.h / 8 > ty)
                select = i;
        }

        if (tx > canvas.w / 5) {
            for (i = 0; i < 6; i++) {
                switch (i) {
                    case 0:
                        nx = 1;
                        ny = 2;
                        break;
                    case 1:
                        nx = 2;
                        ny = 1;
                        break;
                    case 2:
                        nx = 1;
                        ny = 1;
                        break;
                    case 3:
                        nx = 0;
                        ny = 1;
                        break;
                    case 4:
                        nx = 3;
                        ny = 1;
                        break;
                    case 5:
                        nx = 1;
                        ny = 0;
                        break;
                }
                for (j = 0; j < 3; j++) {
                    for (k = 0; k < 3; k++) {
                        if (nx * canvas.h / 3 + k * canvas.h / 9 + canvas.w / 5 < tx && ny * canvas.h / 3 + j * canvas.h / 9 < ty && nx * canvas.h / 3 + (k + 1) * canvas.h / 9 + canvas.w / 5 > tx && ny * canvas.h / 3 + (j + 1) * canvas.h / 9 > ty) {
                            color[i][j][k] = select;
                        }

                    }
                }
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (canvas.w / 5 < tx && canvas.h - 140 < ty && canvas.w / 5 + 160 > tx && canvas.h - 50 > ty) {
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
            }
            if (25 < tx && canvas.h - 90 < ty && 115 > tx && canvas.h - 40 > ty) {
                Intent Intent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(Intent, CONNECTDEVICE);
            }
        }

        phandler.post(new Runnable() {
            public void run() {
                canvas.invalidate();
            }
        });

        return super.onTouchEvent(event);
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
                BTclient = new BluetoothClient(MainActivity.this, handler);
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
            // 端末選択ダイアログからの戻り処理
            case CONNECTDEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.DEVICEADDRESS);
                    // 生成した端末に接続要求
                    BluetoothDevice device = BTadapter.getRemoteDevice(address);
                    BTclient.connect(device);
                }
                break;
            // 有効化ダイアログからの戻り処理
            case ENABLEBLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    BTclient = new BluetoothClient(this, handler);
                } else {
                    Toast.makeText(this, "Bluetoothのサポートなし", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothClient.MESSAGE_STATECHANGE:
                    switch (msg.arg1) {
                        case BluetoothClient.STATE_CONNECTED:
                            canvas.state = 2;
                            break;
                        case BluetoothClient.STATE_CONNECTING:
                            canvas.state = 1;
                            break;
                        case BluetoothClient.STATE_NONE:
                            canvas.state = 3;
                            break;
                    }
                    break;
                case BluetoothClient.MESSAGE_READ:
                    canvas.state = 4;
                    break;
            }
            phandler.post(new Runnable() {
                public void run() {
                    canvas.invalidate();
                }
            });
        }
    };
}
