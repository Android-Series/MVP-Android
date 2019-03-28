package com.jxq.mvp.common.databus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标记观察者的方法
 *
 */
@Target(ElementType.METHOD) //代表修饰的是一个方法
@Retention(RetentionPolicy.RUNTIME) //运行时起作用
@Documented
public @interface RegisterBus {
    //这个接口是我们写的一个注解类
    //注解使用非常简单
}
