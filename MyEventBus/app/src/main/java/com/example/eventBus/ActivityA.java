package com.example.eventBus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.douyin.ActivityGloble;
import com.example.eventBus.core.EventBus;
import com.example.eventBus.core.EventBusSubscribe;

public class ActivityA extends Activity implements View.OnClickListener {
    private TextView textView;
    private String[] methods = {"setTextView","setTextView1"};
    Test test = new Test();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        textView = findViewById(R.id.tv);
        Button button = findViewById(R.id.btnStartB);
        button.setOnClickListener(this);
        ActivityGloble.activityGloble.register(this,methods);
        EventBus.getDefault().regisg(this);
        EventBus.getDefault().regisg(test);

      //  activityA = this;
    }

    @EventBusSubscribe
    public void setTextView(){
        textView.setText("ActivityB set ActivityA");
    }
    @EventBusSubscribe
    public void setTextView1(String arg1,String arg2){
        TextView textView = findViewById(R.id.tv1);
        textView.setText("ActivityB set ActivityA "+arg1+" "+arg2);
    }
    @EventBusSubscribe
    public void setTextView2(String arg1,Integer arg3){
        TextView textView = findViewById(R.id.tv2);
        textView.setText("ActivityB set ActivityA "+arg1+" "+" arg3 is "+3);
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, ActivityB.class);
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        ActivityGloble.activityGloble.unregister();
       // ActivityGloble.activityGloble = null;
        EventBus.getDefault().unregister(this);
    }
}
