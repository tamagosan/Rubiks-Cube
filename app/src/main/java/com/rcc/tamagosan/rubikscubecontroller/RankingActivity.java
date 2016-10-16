package com.rcc.tamagosan.rubikscubecontroller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RankingActivity extends AppCompatActivity {
    public static long score[][]=new long[4][5];
    public static TextView[][] rank=new TextView[4][5];
    public static boolean clearf=false;
    private int i,j;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        
        rank[0][0]=(TextView)this.findViewById(R.id.time_time_1);
        rank[0][1]=(TextView)this.findViewById(R.id.time_time_2);
        rank[0][2]=(TextView)this.findViewById(R.id.time_time_3);
        rank[0][3]=(TextView)this.findViewById(R.id.time_time_4);
        rank[0][4]=(TextView)this.findViewById(R.id.time_time_5);

        rank[1][0]=(TextView)this.findViewById(R.id.time_tesuu_1);
        rank[1][1]=(TextView)this.findViewById(R.id.time_tesuu_2);
        rank[1][2]=(TextView)this.findViewById(R.id.time_tesuu_3);
        rank[1][3]=(TextView)this.findViewById(R.id.time_tesuu_4);
        rank[1][4]=(TextView)this.findViewById(R.id.time_tesuu_5);

        rank[2][0]=(TextView)this.findViewById(R.id.tesuu_time_1);
        rank[2][1]=(TextView)this.findViewById(R.id.tesuu_time_2);
        rank[2][2]=(TextView)this.findViewById(R.id.tesuu_time_3);
        rank[2][3]=(TextView)this.findViewById(R.id.tesuu_time_4);
        rank[2][4]=(TextView)this.findViewById(R.id.tesuu_time_5);

        rank[3][0]=(TextView)this.findViewById(R.id.tesuu_tesuu_1);
        rank[3][1]=(TextView)this.findViewById(R.id.tesuu_tesuu_2);
        rank[3][2]=(TextView)this.findViewById(R.id.tesuu_tesuu_3);
        rank[3][3]=(TextView)this.findViewById(R.id.tesuu_tesuu_4);
        rank[3][4]=(TextView)this.findViewById(R.id.tesuu_tesuu_5);

        MainActivity mact = new MainActivity();
        for(i=0;i<5;i++) {
            for (j = 0; j < 4; j++) {
                SharedPreferences pref = getSharedPreferences(String.format("rank%d_%d", j, i), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
                if (j % 2 == 0) {
                    long tcount = pref.getLong("tamagosan", 600000);
                    long mm = tcount * 10 / 1000 / 60;
                    long ss = tcount * 10 / 1000 % 60;
                    long ms = (tcount * 10 - ss * 1000 - mm * 1000 * 60) / 10;
                    rank[j][i].setText(String.format("%1$02d:%2$02d.%3$02d", mm, ss, ms));
                    score[j][i] = tcount;
                } else {
                    long tesuu = pref.getLong("tamagosan", 1000);
                    rank[j][i].setText(String.format("%d手", tesuu));
                    score[j][i] = tesuu;
                }
                rank[j][i].setTextColor(Color.BLACK);
            }
            if (clearf) {
                if (mact.cc1 == i) {
                    rank[0][i].setTextColor(Color.RED);
                    rank[1][i].setTextColor(Color.RED);
                }
                if (mact.cc2 == i){
                    rank[2][i].setTextColor(Color.RED);
                    rank[3][i].setTextColor(Color.RED);
                }
            }
        }
        
        clearf=false;
        Button rtnbutton=(Button)this.findViewById(R.id.rtn);
        rtnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
