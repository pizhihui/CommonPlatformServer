package com.yonyou.datafin.netty.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */

@Configuration
@ComponentScan("com.yonyou.datafin")
@ImportResource("classpath:spring.xml")
public class SpringApplication {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SpringApplication.class);


    }

}
