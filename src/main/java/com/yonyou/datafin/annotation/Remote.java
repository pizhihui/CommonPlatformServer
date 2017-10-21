package com.yonyou.datafin.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Remote {

    String value();

}
