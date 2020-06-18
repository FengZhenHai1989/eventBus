package com.example.eventBus;

import android.util.Log;
import android.widget.TextView;

import com.example.eventBus.core.EventBusSubscribe;

public class Test {

    @EventBusSubscribe
    public void setTextView(){
        Log.e("test"," test setTextView");
    }
    @EventBusSubscribe
    public void setTextView(String arg1,String arg2){
        //TextView textView = findViewById(R.id.tv1);
        //textView.setText("ActivityB set ActivityA "+arg1+" "+arg2);
        Log.e("test"," test setTextView "+arg1+" arg2 is "+arg2);
    }
    @EventBusSubscribe
    public void setTextView(String arg1,Integer arg3){
        Log.e("test"," test setTextView "+arg1+" arg2 is "+arg3);
    }

    @EventBusSubscribe
    public void setTextView3(String arg1,Integer arg3){
        Log.e("test"," test setTextView3 "+arg1+" arg2 is "+arg3);
    }

    @EventBusSubscribe
    public void setTextView4(String arg1,Integer arg3){
        Log.e("test"," test setTextView4 "+arg1+" arg2 is "+arg3);
    }
}
