package com.yonyou.server.log4j;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: pizhihui
 * @datae: 2017-10-23
 */
public class TestLog4j {

    Logger logger = LoggerFactory.getLogger(TestLog4j.class);

    @Test
    public void testLog4j() {

        logger.error("error: {}", "信息");

    }


    @Test
    public void testUncaughtException() {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error( t + " ExceptionDemo threw an exception: ", e);
                };
            });
            class adminThread implements Runnable {
                public void run() {
                    throw new RuntimeException();
                }
            }

            Thread t = new Thread(new adminThread());
            t.start();
    }



}
