package com.eimy.yunpicturebackend.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authcheck {
    /**必须有某个角色的权限才能访问
     *
     */
    String mustRole() default "";
}
