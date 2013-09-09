package com.example.mycamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by marui on 13-9-4.
 */
public class ShowInfoActivity extends Activity {

    public static void startInfoActivity(Context context, String info){
        Intent i = new Intent(context, ShowInfoActivity.class);
        i.putExtra("info", info);
        context.startActivity(i);
    }

    private TextView mInfoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);
        mInfoText = (TextView) findViewById(R.id.info_text);
        String info = getIntent().getStringExtra("info");
        if(!TextUtils.isEmpty(info)){
            mInfoText.setText(info);
        }
    }

    public void setText(String text){
        mInfoText.setText(text);
        StringBuffer sb = new StringBuffer();

    }
}
