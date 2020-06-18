package com.example.eventBus.core;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventBus {
    private static EventBus instance = new EventBus();
    private  Object subscriber;//要订阅的类，
    private HashMap<Object,HashMap<String,SubscribleMethod>> cacheMap ;
    private Handler handler;//用于处理需要在主线程中执行的事件
    private ExecutorService executorService;//线程池用于处理异步事件
    public static EventBus getDefault() {
        if(instance == null){
            synchronized (EventBus.class){
                if(instance == null){
                    instance = new EventBus();
                }
            }
        }
        return instance;
    }

    public EventBus(){
        this.cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }
    public void regisg(Object object){
        if(object == null){
            return;
        }
        HashMap<String,SubscribleMethod> methodMap = cacheMap.get(object);//先从缓存中查找。
        if(methodMap == null){
            methodMap = getSubscribleMethodMap(object);//获取方法map,
            cacheMap.put(object,methodMap);//将方法map放入到缓存中

        }

    }

    private HashMap<String,SubscribleMethod> getSubscribleMethodMap(Object subscriber){
        Class<?> subscriberClass = subscriber.getClass();//获取类
        HashMap<String,SubscribleMethod> methodMap = new HashMap<>();
        while (subscriberClass != null) {
            //判断分类是在那个包下，（如果是系统的就不需要）
            String name = subscriberClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }
            Method[] declaredMethods = subscriberClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                //找相应的注解
                EventBusSubscribe annotation = method.getAnnotation(EventBusSubscribe.class);
                if(annotation == null){//注解没有找到
                    continue;
                }
                ThreadMode threadMode = annotation.threadMode();
                Class<?>[] paramTypes = method.getParameterTypes();
                SubscribleMethod subscribleMethod = new SubscribleMethod(method,threadMode,paramTypes);
                Log.e("meth"," getMethodNameKey is "+subscribleMethod.getMethodNameKey());
                methodMap.put(subscribleMethod.getMethodNameKey(),subscribleMethod);
            }
            subscriberClass = subscriberClass.getSuperclass();//遍历父类
        }
        return methodMap;
    }


    public void post(final Object... event){
        postClasssMethod(null,null,event);
    }

    public void postMethod(final String medhodName,final Object... event){
        postClasssMethod(null,medhodName,event);
    }

    /**
     *
     * @param className 执行对应的类
     * @param methodName 执行对应的方法
     * @param event 参数
     */
    public void postClasssMethod(final String className,final String methodName,final Object... event){
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        Object invokeObj = null;
        SubscribleMethod subscribleMethod= null;
        String myClassName = "";
        if(className == null){
        }else{
            myClassName = className;
        }
        boolean isFindClass = false;
        String myMethodName = methodName;
        if(methodName == null ){
            myMethodName = "";
        }
        while (iterator.hasNext()) {
            //拿到注册类
            final Object nextClassObj = iterator.next();
            if(nextClassObj.getClass().getName().equals(myClassName)) {//通过类名找到对应的类
                isFindClass = true;
            }
            HashMap<String,SubscribleMethod> methodHashMap = cacheMap.get(nextClassObj);
            String tempMethodName = getMethodName(myMethodName,event);
            for (Map.Entry<String, SubscribleMethod> entry : methodHashMap.entrySet()) {
                subscribleMethod = entry.getValue();
                String paramTypesName =subscribleMethod.getParamTypesNameKey();//获取参数的拼接字符
                String methodNameKey = subscribleMethod.getMethodNameKey();
                if(methodNameKey.equals(tempMethodName)){//通过类名以及参数找到对应的方法了，执行完方法的,就退出循环了
                    invokeObj = nextClassObj;
                    invoke(subscribleMethod,invokeObj,event);
                    break;
                }else if(paramTypesName.equals(tempMethodName)){//通过参数找到对应的方法
                    if(subscribleMethod != null){
                        invokeObj = nextClassObj;
                        invoke(subscribleMethod,invokeObj,event);
                    }
                }
            }
            if(isFindClass){//如果找到对应的类，执行完对应的类中的方法就退出不再执行了
                break;
            }
        }
    }

    private String getMethodName(String methodName,Object... args){
        StringBuilder builder = new StringBuilder();
        builder.append(methodName);
        if(args != null && args.length > 0){
            //如果有参数，对方法进行拼接
            for(int i = 0;i < args.length;i++){
                builder.append(args[i].getClass().getName());
            }
           // methodName = builder.toString();
        }
        return builder.toString();
    }

    private void invoke(SubscribleMethod subscribleMethod, final Object classObj, final Object ... params) {
        final Method method = subscribleMethod.getMethod();
        ThreadMode threadMode = subscribleMethod.getThreadMode();
        switch (threadMode){
            case MAIN:
                if(Looper.myLooper() == Looper.getMainLooper()){
                    invokeMethod(method,classObj,params);
                }else{
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invokeMethod(method,classObj,params);
                        }
                    });
                }
                break;
            case POSTING:
                invokeMethod(method,classObj,params);
                break;
            case ASYNC:
                //post方法执行在主线程中
                if(Looper.myLooper() == Looper.getMainLooper()){
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            invokeMethod(method,classObj,params);
                        }
                    });
                } else {
                    //post方法执行在子线程中
                    invokeMethod(method,classObj,params);
                }
                break;
        }

    }

    private void invokeMethod(Method method,Object classObj, Object ... params){
        try {
            method.invoke(classObj, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //取消注册
    public void unregister(Object subscriber) {
        Object hashMap = cacheMap.get(subscriber);
        //如果获取到
        if (hashMap != null) {
            cacheMap.remove(subscriber);
        }
    }
}
