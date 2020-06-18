package com.example.eventBus;;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.administrator.douyin.ActivityGloble;
import com.example.eventBus.core.EventBus;

public class ActivityB extends Activity implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        Button button = findViewById(R.id.btn);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       // activityGlobalTest();
        eventBusTest();
    }

    private void eventBusTest(){
        EventBus.getDefault().post();
      //  EventBus.getDefault().post();
        EventBus.getDefault().post("qaa","bbb");
        EventBus.getDefault().post("wwww",1);
        EventBus.getDefault().postMethod("setTextView3","wwww",1);
        EventBus.getDefault().postClasssMethod(ActivityA.class.getName(),null,"qaa","bbb");
        EventBus.getDefault().postClasssMethod(ActivityA.class.getName(),null,"wwww",1);
        EventBus.getDefault().postClasssMethod(ActivityA.class.getName(),null);
    }
    private void activityGlobalTest(){
        ActivityGloble.activityGloble.post("setTextView");
        ActivityGloble.activityGloble.post("setTextView1","qaa","bbb");
        ActivityGloble.activityGloble.post("setTextView","wwww",1);
    }
}
