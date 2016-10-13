package com.rcc.tamagosan.rubikscubecontroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

class MyView extends View {

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onDraw(Canvas canvas) {
        int i, j, k;
        int w = getWidth();
        int h = getHeight();
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(50, 50, 50));
        Paint spaint = new Paint();
        spaint.setAntiAlias(true);

        for (i = 0; i < 6; i++) {
            spaint.setColor(Color.rgb(0, 0, 0));
            switch (i) {
                case 0:
                    canvas.skew(0f, 0.577f);
                    canvas.drawRect(w / 4, h / 7, w / 2, h * 3 / 7, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, k, j));
                            canvas.drawRect(w / 4 + k * w / 12 + 4, h / 7 + j * h * 2 / 21 + 4, w / 4 + (k + 1) * w / 12 - 4, h / 7 + (j + 1) * h * 2 / 21 - 4, spaint);
                        }
                    }
                    canvas.skew(0f, -0.577f);
                    break;
                case 1:
                    canvas.skew(0f, -0.577f);
                    canvas.drawRect(w / 2, h * 5 / 7, w * 3 / 4, h, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, j, 2 - k));
                            canvas.drawRect(w / 2 + k * w / 12 + 4, h * 5 / 7 + j * h * 2 / 21 + 4, w / 2 + (k + 1) * w / 12 - 4, h * 5 / 7 + (j + 1) * h * 2 / 21 - 4, spaint);
                        }
                    }
                    canvas.skew(0f, 0.577f);
                    break;
                case 2:
                    canvas.skew(-1.732f, 0.577f);
                    canvas.drawRect(w * 3 / 8, -h / 14, w * 5 / 8, h / 14, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, k, j));
                            canvas.drawRect(w * 3 / 8 + k * w / 12 + 4, -h / 14 + j * h / 21 + 2, w * 3 / 8 + (k + 1) * w / 12 - 4, -h / 14 + (j + 1) * h / 21 - 2, spaint);
                        }
                    }
                    canvas.skew(1.732f, -0.577f);
                    break;
                case 3:
                    canvas.skew(0f, -0.577f);
                    canvas.drawRect(0, h / 14, w / 8, h * 3 / 14, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, 2 - j, 2 - k));
                            canvas.drawRect(k * w / 24 + 2, h / 14 + j * h / 21 + 2, (k + 1) * w / 24 - 2, h / 14 + (j + 1) * h / 21 - 2, spaint);
                        }
                    }
                    canvas.skew(0f, 0.577f);
                    break;
                case 4:
                    canvas.skew(-1.732f, 0.577f);
                    canvas.drawRect(w * 7 / 16, h * 3 / 28, w * 9 / 16, h * 5 / 28, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, 2 - k, j));
                            canvas.drawRect(w * 7 / 16 + k * w / 24 + 2, h * 3 / 28 + j * h / 42 + 1, w * 7 / 16 + (k + 1) * w / 24 - 2, h * 3 / 28 + (j + 1) * h / 42 - 1, spaint);
                        }
                    }
                    canvas.skew(1.732f, -0.577f);
                    break;
                case 5:
                    canvas.skew(0f, 0.577f);
                    canvas.drawRect(w * 3 / 16, -h * 3 / 28, w / 4, -h / 28, spaint);
                    for (j = 0; j < 3; j++) {
                        for (k = 0; k < 3; k++) {
                            spaint.setColor(sColor(i, k, 2 - j));
                            canvas.drawRect(w * 3 / 16 + k * w / 48 + 1, -h * 3 / 28 + j * h / 42 + 1, w * 3 / 16 + (k + 1) * w / 48 - 1, -h * 3 / 28 + (j + 1) * h / 42 - 1, spaint);
                        }
                    }
                    canvas.skew(0f, -0.577f);
                    break;
            }
        }
    }

    public int sColor(int mh, int sx, int sy) {
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
                return Color.rgb(255, 120, 0);
        }
        return 0;
    }
}
