package com.rcc.tamagosan.rubikscubecontroller;

import android.support.v7.app.AppCompatActivity;
import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DeviceListActivity extends Activity implements AdapterView.OnItemClickListener{
    /*** 定数定義 ******/
    private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
    private final static int MP = LinearLayout.LayoutParams.MATCH_PARENT;
    public static String DEVICEADDRESS = "deviceaddress";
    private ArrayAdapter<String> DeviceList;
    private BluetoothAdapter BTadapter;

    /**** アクティビティ起動時 最初に実行するメソッド *********/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ダイアログタイトルなし
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 戻り値の初期化
        setResult(Activity.RESULT_CANCELED);
        // ダイアログレイアウトの作成
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);

        // デバイス配列
        DeviceList = new ArrayAdapter<String>(this,R.layout.rowdata);

        // リストビュー生成
        ListView listView = new ListView(this);
        listView.setLayoutParams(new LinearLayout.LayoutParams(MP, WC));
        listView.setAdapter(DeviceList);
        layout.addView(listView);
        listView.setOnItemClickListener(this);

        // ブロードキャストレシーバーの生成
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        // Bluetooth端末のリストアップ
        BTadapter = BluetoothAdapter.getDefaultAdapter();			// 自分のBT選択
        // 既にペアリングしたことがある全デバイスリスト作成
        Set<BluetoothDevice> boundeddevices = BTadapter.getBondedDevices();
        if (boundeddevices.size() > 0) {
            for (BluetoothDevice device : boundeddevices) {
                DeviceList.add(device.getName() + "\n" + device.getAddress());
            }
        }
        // 検索継続中ならいったん検索終了
        if (BTadapter.isDiscovering() == true) {
            BTadapter.cancelDiscovery();
        }
        // 改めて一定時間だけ検索開始
        BTadapter.startDiscovery();
    }

    /************ 検索で発見された場合のブロードキャストレシーバー *************/
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // Bluetooth端末新発見の場合リストに追加
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BluetoothDevice.BOND_BONDED != device.getBondState()) {
                    DeviceList.add(device.getName() + "\n"+ device.getAddress());
                }
            }
            // Bluetooth端末検索終了
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                android.util.Log.e("", "Bluetooth端末検索完了");
            }
        }
    };

    /**** アクティビティ破棄時 ****/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (BTadapter != null) {
            BTadapter.cancelDiscovery();
        }
        unregisterReceiver(receiver);
    }
    /***** デバイス選択クリック時 *****/
    public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        // Bluetooth端末の検索のキャンセル
        BTadapter.cancelDiscovery();		// 検索終了

        // 端末のアドレスを戻り値として設定
        String info = ((TextView) v).getText().toString();
        String address = info.substring(info.length() - 17);
        Intent intent = new Intent();
        intent.putExtra(DEVICEADDRESS, address);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
