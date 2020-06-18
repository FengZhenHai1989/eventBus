package com.example.administrator.douyin;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityGloble {
    private  Object subscriber;//要订阅的类，
    public static ActivityGloble activityGloble = new ActivityGloble();
    private HashMap<String,Method> methodMap = new HashMap<>();//需要被执行方法map
    /**
     *
     * @param subscriber//要订阅的类
     * @param postMethodNames,订阅类要执行的方法列表
     */
    public void register(Object subscriber,String[] postMethodNames){
        this.subscriber = subscriber;
        if(subscriber == null){
            return;
        }
        if(postMethodNames != null){
            Class<?> subscriberClass = subscriber.getClass();//获取类
            Method[] methods = subscriberClass.getDeclaredMethods();//获取所有方法
            for(int i = 0;i < methods.length;i++){
                for(int j = 0;j < postMethodNames.length;j++){
                    if(methods[i].getName().equals(postMethodNames[j])){
                        StringBuilder paramTypeBuilde = null;//用于处理函数的重载
                        Class<?>[] paramTypes = methods[i].getParameterTypes();
                        if(paramTypes != null && paramTypes.length > 0){
                            paramTypeBuilde = new StringBuilder();
                            for(int k = 0; k < paramTypes.length;k++){
                              //  methods[i].getAnnotation()
                                Log.e("me"," method type is "+paramTypes[k].getName()+" method name is "+methods[i].getName());
                                paramTypeBuilde.append(paramTypes[k].getName());//取出函数对应的参数
                            }
                        }
                        if(paramTypeBuilde == null){
                            methodMap.put(postMethodNames[j],methods[i]);//如果这个函数没有参数
                        }else{
                            //如果有参数，参数也和方法一起，作为一个key
                            //比如setTextView(String arg1,Integer arg3)会转化为 setTextViewjava.lang.Stringjava.lang.Integer
                            methodMap.put(postMethodNames[j]+paramTypeBuilde.toString(),methods[i]);
                        }
                    }
                }
            }
        }
    }
    /**
     *
     * @param methodName
     * @param args,要传入的参数
     */
    public void post(String methodName, Object... args) {

        if(args != null && args.length > 0){
            //如果有参数，对方法进行拼接
            StringBuilder builder = new StringBuilder();
            builder.append(methodName);
            for(int i = 0;i < args.length;i++){
                Log.e("me"," simpleName is "+args[i].getClass());
                builder.append(args[i].getClass().getName());
            }
            methodName = builder.toString();
        }
        Log.e("me"," postMethos is "+methodName);
        Method method = methodMap.get(methodName);
        if(method != null){
            try {
                if(args == null || args.length == 0){
                    method.invoke(subscriber);
                }else{
                    method.invoke(subscriber,args);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void unregister(){
        subscriber = null;
        methodMap.clear();
    }

}
