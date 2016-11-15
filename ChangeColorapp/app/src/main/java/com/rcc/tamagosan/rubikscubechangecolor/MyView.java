package com.rcc.tamagosan.rubikscubechangecolor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyView extends View {

    private Paint mPaint = new Paint();
    private int i,j,k;
    public static int state=0;
    public static int w,h;


    public MyView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int nx = 0, ny = 0;
        w = getWidth();
        h = getHeight();
        MainActivity mact = new MainActivity();

        mPaint.setTextSize(40);
        canvas.drawColor(Color.rgb(100, 100, 100));

        mPaint.setColor(Color.rgb(255, 255, 0));
        canvas.drawRect(25, h - 90, 115, h - 40, mPaint);
        mPaint.setColor(Color.rgb(0, 0, 0));
        canvas.drawText("接続", 30, h - 50, mPaint);

        mPaint.setColor(Color.rgb(200, 200, 200));
        canvas.drawRect(w / 5, h - 140, w / 5 + 160, h - 50, mPaint);
        mPaint.setColor(Color.rgb(0, 0, 0));
        canvas.drawText("適応", w / 5 + 40, h - 80, mPaint);

        canvas.drawRect(30, 30, 30 + h / 8, 30 + h / 8 * 6, mPaint);

        for (i = 0; i < 6; i++) {
            mPaint.setColor(Color.rgb(0, 0, 0));
            switch (i) {
                case 0:
                    mPaint.setColor(Color.rgb(255, 255, 255));
                    break;
                case 1:
                    mPaint.setColor(Color.rgb(255, 0, 0));
                    break;
                case 2:
                    mPaint.setColor(Color.rgb(0, 255, 0));
                    break;
                case 3:
                    mPaint.setColor(Color.rgb(0, 0, 255));
                    break;
                case 4:
                    mPaint.setColor(Color.rgb(255, 255, 0));
                    break;
                case 5:
                    mPaint.setColor(Color.rgb(255, 150, 0));
                    break;
            }
            canvas.drawRect(30 + 5, 30 + i * h / 8 + 5, 30 + h / 8 - 5, 30 + (i + 1) * h / 8 - 5, mPaint);
            if (i == mact.select) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(Color.rgb(255, 0, 255));
                canvas.drawRect(30 + 2, 30 + i * h / 8 + 2, 30 + h / 8 - 2, 30 + (i + 1) * h / 8 - 2, mPaint);
                canvas.drawRect(30 + 3, 30 + i * h / 8 + 3, 30 + h / 8 - 3, 30 + (i + 1) * h / 8 - 3, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
            }
        }

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
            mPaint.setColor(Color.rgb(0, 0, 0));
            canvas.drawRect(nx * h / 3 + w / 5, ny * h / 3, (nx + 1) * h / 3 + w / 5, (ny + 1) * h / 3, mPaint);
            for (j = 0; j < 3; j++) {
                for (k = 0; k < 3; k++) {
                    mPaint.setColor(sColor(i, k, j));
                    canvas.drawRect(nx * h / 3 + k * h / 9 + w / 5 + 3, ny * h / 3 + j * h / 9 + 3, nx * h / 3 + (k + 1) * h / 9 + w / 5 - 3, ny * h / 3 + (j + 1) * h / 9 - 3, mPaint);

                }
            }
        }

        mPaint.setColor(Color.rgb(255, 120, 255));
        mPaint.setTextSize(30);
        switch (state) {
            case 0:
                mPaint.setColor(Color.rgb(180, 180, 180));
                canvas.drawText("未接続", 30, h - 4, mPaint);
                break;
            case 1:
                mPaint.setColor(Color.rgb(255, 255, 255));
                canvas.drawText("接続中", 30, h - 4, mPaint);
                break;
            case 2:
                mPaint.setColor(Color.rgb(255, 255, 255));
                canvas.drawText("接続完了", 30, h - 4, mPaint);
                break;
            case 3:
                mPaint.setColor(Color.rgb(255, 10, 10));
                canvas.drawText("接続失敗", 30, h - 4, mPaint);
                break;
            case 4:
                mPaint.setColor(Color.rgb(255, 255, 255));
                canvas.drawText("送受信処理中", 30, h - 4, mPaint);
                break;
            case 5:
                mPaint.setColor(Color.rgb(255, 255, 0));
                canvas.drawText("Bluetooth未サポート", 30, h - 4, mPaint);
                break;
        }
    }

    private int sColor(int mh, int sx, int sy) {
        MainActivity act = new MainActivity();
        switch (act.color[mh][sy][sx]) {
            case 0:
                return Color.rgb(255, 255, 255);
            case 1:
                return Color.rgb(255, 0, 0);
            case 2:
                return Color.rgb(0, 255, 0);
            case 3:
                return Color.rgb(0, 0, 255);
            case 4:
                return Color.rgb(255, 255, 0);
            case 5:
                return Color.rgb(255, 150, 0);
        }
        return 0;
    }
}
