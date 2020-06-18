package com.example.eventBus.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBusSubscribe {
    ThreadMode threadMode() default ThreadMode.POSTING;//判断是运行在哪个线程
}
