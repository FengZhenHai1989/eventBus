package com.example.eventBus.core;

import java.lang.reflect.Method;

//对所要订阅的方法进行一些封装。
public class SubscribleMethod {
    //注册方法
    private Method method;
    //线程类型
    private ThreadMode threadMode;

    //参数类型
    private Class<?>[] paramTypes;


    public SubscribleMethod(Method method, ThreadMode threadMode, Class<?>[] paramTypes) {
        this.method = method;
        this.threadMode = threadMode;
        this.paramTypes = paramTypes;
    }

    /**
     *
     * 获取方法名字
     */
    public String getMethodNameKey(){
        String methodName = method.getName();
        if(paramTypes == null || paramTypes.length ==0){
           // return method.getName();
        }else{
            StringBuilder builder = new StringBuilder();
            builder.append(methodName);
            for(int k = 0; k < paramTypes.length;k++){
                builder.append(paramTypes[k].getName());
            }
            methodName = builder.toString();
        }
        return  methodName;
    }
    /**
     *
     * 参数拼接的字段
     */
    public String getParamTypesNameKey(){
        if(paramTypes == null || paramTypes.length ==0){
            // return method.getName();
            return "";
        }else{
            StringBuilder builder = new StringBuilder();
            for(int k = 0; k < paramTypes.length;k++){
                builder.append(paramTypes[k].getName());
            }
            return builder.toString();
        }
    }
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }
}
