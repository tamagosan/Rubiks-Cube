package com.rcc.tamagosan.rubikscubecontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;

public class SettingActivity extends AppCompatActivity {
    private RadioGroup mRadioGroup1, mRadioGroup2;
    MainActivity mact = new MainActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mRadioGroup1 = (RadioGroup) findViewById(R.id.RadioGroup1);
        if (mact.cChange) {
            mRadioGroup1.check(R.id.RadioButton12);
        } else {
            mRadioGroup1.check(R.id.RadioButton11);
        }
        mRadioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mact.cColorFlag = true;
                if (checkedId == R.id.RadioButton11) {
                    mact.cChange = false;
                } else if (checkedId == R.id.RadioButton12) {
                    mact.cChange = true;
                }
            }
        });

        mRadioGroup2 = (RadioGroup) findViewById(R.id.RadioGroup2);
        if (mact.animation) {
            mRadioGroup2.check(R.id.RadioButton21);
        } else {
            mRadioGroup2.check(R.id.RadioButton22);
        }
        mRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mact.cAnimationFlag = true;
                if (checkedId == R.id.RadioButton21) {
                    mact.animation = true;
                } else if (checkedId == R.id.RadioButton22) {
                    mact.animation = false;
                }
            }
        });

        Button rtnbutton = (Button) this.findViewById(R.id.rtn);
        rtnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
