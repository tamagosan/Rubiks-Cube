package com.rcc.tamagosan.rubikscubechangecolor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

/********************  Bluetoothで接続し送受信を実行するクラス　*************************/
public class BluetoothClient {
    /**** クラス定数宣言 ********/
    public static final int MESSAGE_STATECHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int Max_Size = 32;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    private BluetoothAdapter BTadapter;
    private Handler handler;
    private int state;
    private ConnectThread connectthread;
    private ConnectedThread connectedthread;

    // コンストラクタによるクラスの初期化
    public BluetoothClient(Context context, Handler handler) {
        this.BTadapter = BluetoothAdapter.getDefaultAdapter();
        this.handler = handler;
        state = STATE_NONE;
    }


    /******** スレッド１ ********* Bluetooth接続処理スレッド  ****************************/
    private class ConnectThread extends Thread {
        private BluetoothDevice BTdevice;
        private BluetoothSocket BTsocket;

        // コンストラクタによるスレッド初期化
        public ConnectThread(BluetoothDevice device) {
            try {
                this.BTdevice = device;
                BTsocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e) {
            }
        }
        // 接続実行処理
        public void run() {
            BTadapter.cancelDiscovery();			// 検索処理終了
            try {
                BTsocket.connect();					// 接続中同期化処理へ
            }
            catch (IOException e) {
                setState(STATE_NONE);				// エラーなら初期状態とする
                try {
                    BTsocket.close();				// エラーならクローズ
                }
                catch (IOException e2) {
                }
                return;
            }
            synchronized (BluetoothClient.this) {	// 同期を取る
                connectthread = null;				// スレッド初期化
            }
            connected(BTsocket, BTdevice);			// 接続済み同期化処理へ
        }

        // Blutooth接続を切り離す処理
        public void cancel() {
            try {
                BTsocket.close();
            }
            catch (IOException e) {
            }
        }
    }
    /******************************************************************/
    /****** Bluetoothの接続実行同期化処理 ******/
    public synchronized void connect(BluetoothDevice device) {
        if (state == STATE_CONNECTING) {
            if (connectthread != null) {				// 既に接続中なら
                connectthread.cancel();					// いったんクローズする
                connectthread = null;
            }
        }
        if (connectedthread != null) {					// 既に接続済みなら
            connectedthread.cancel();					// いったんクローズ
            connectedthread = null;
        }
        connectthread = new ConnectThread(device);		// 改めて接続を開始する
        connectthread.start();
        setState(STATE_CONNECTING);
    }
    /****** Bluetooth接続完了同期化処理 ******/
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (connectthread != null) {					// 既に接続中なら
            connectthread.cancel();						// いったんクローズ
            connectthread = null;
        }
        if (connectedthread != null) {					// 既に接続済みなら
            connectedthread.cancel();					// いったんクローズ
            connectedthread = null;
        }
        connectedthread = new ConnectedThread(socket);	// 改めて接続済みとする
        connectedthread.start();
        setState(STATE_CONNECTED);
    }

    /********  スレッド2 *********** Bluetooth送受信処理実行スレッド  ******************************/
    private class ConnectedThread extends Thread {
        private BluetoothSocket BTsocket;

        //
        public ConnectedThread(BluetoothSocket bluetoothsocket) {
            this.BTsocket = bluetoothsocket;
        }
        /******** 受信を待ち、バッファからデータ取り出し処理 *********/
        public void run() {
            byte[] buf = new byte[Max_Size * 2];		// 受信バッファの用意
            byte[] Rcv = new byte[Max_Size + 1];		// 取り出しバッファの用意
            int bytes, Index, i;						// 受信バイト数他
            Index = 0;									// バイトカウンタリセット

            /***** 受信繰り返しループ *****/
            while (true) {
                try {
                    // 文字列最後まで受信繰り返し
                    while(Index < Max_Size){
                        InputStream input = BTsocket.getInputStream();
                        bytes = input.read(buf);		// 受信実行
                        for(i=0; i<bytes; i++){			// 受信バイト数だけ繰り返し
                            Rcv[Index] = buf[i];		// バッファコピー
                            if(Index < Max_Size)
                                Index++;				// バイトカウンタ更新
                        }
                    }
                    Index = 0;							// バッファインデックスリセット
                    /**** 取り出したデータを返す　****/
                    handler.obtainMessage(MESSAGE_READ, Max_Size, -1, Rcv).sendToTarget();
                }
                catch (IOException e) {
                    setState(STATE_NONE);
                    break;
                }
            }
        }
        /******** 送信処理 *******/
        public void write(byte[] buf) {
            try {
                OutputStream output = BTsocket.getOutputStream();
                output.write(buf);
            }
            catch (IOException e) {
            }
        }
        /***** クローズ処理 ******/
        public void cancel() {
            try {
                BTsocket.close();
            }
            catch (IOException e) {
            }
        }
    }
    /**********************************************************************************
     /****** 送信実行メソッド ********/
    public void write(byte[] out) {
        ConnectedThread connectedthread;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }
            connectedthread = this.connectedthread;
        }
        connectedthread.write(out);
    }

    /***** 状態の設定メソッド *******/
    private synchronized void setState(int state) {
        this.state = state;
        handler.obtainMessage(MESSAGE_STATECHANGE, state, -1).sendToTarget();
    }
    /****** 状態の取得メソッド ******/
    public synchronized int getState() {
        return state;
    }
    /***** Bluetoothの切断メソッド ******/
    public synchronized void stop() {
        if (null != connectthread) {
            connectthread.cancel();
            connectthread = null;
        }
        if (null != connectedthread) {
            connectedthread.cancel();
            connectedthread = null;
        }
        setState(STATE_NONE);
    }
}
