package com.weichu.youdianpu.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kogitune.activity_transition.ActivityTransition;
import com.kogitune.activity_transition.ExitActivityTransition;
import com.weichu.youdianpu.R;

public class MerchantProfileActivity extends AppCompatActivity {

    private ExitActivityTransition exitTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_profile);
        exitTransition = ActivityTransition.with(getIntent()).to(findViewById(R.id.main_backdrop)).start(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        //exitTransition.exit(this);
        super.onBackPressed();
    }
}
