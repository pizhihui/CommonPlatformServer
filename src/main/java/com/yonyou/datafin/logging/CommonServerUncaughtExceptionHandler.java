package com.yonyou.datafin.logging;

import io.netty.util.SuppressForbidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOError;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author: pizhihui
 * @datae: 2017-10-24
 */
public class CommonServerUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

    private static Logger log = LoggerFactory.getLogger(CommonServerUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("错误: {}", e);
        System.err.println("这里是拦截到的错误信息");
        if (isFatalUncaught(e)) {
            try {
                onFatalUncaught(t.getName(), e);
            } finally {
                // we use specific error codes in case the above notification failed, at least we
                // will have some indication of the error bringing us down
                if (e instanceof InternalError) {
                    halt(128);
                } else if (e instanceof OutOfMemoryError) {
                    halt(127);
                } else if (e instanceof StackOverflowError) {
                    halt(126);
                } else if (e instanceof UnknownError) {
                    halt(125);
                } else if (e instanceof IOError) {
                    halt(124);
                } else {
                    halt(1);
                }
            }
        } else {
            onNonFatalUncaught(t.getName(), e);
        }
    }

    // visible for testing
    static boolean isFatalUncaught(Throwable e) {
        //return isFatalCause(e) || (e instanceof MergePolicy.MergeException && isFatalCause(e.getCause()));
        return isFatalCause(e) || (isFatalCause(e.getCause()));
    }
    private static boolean isFatalCause(Throwable cause) {
        return cause instanceof Error;
    }


    // visible for testing
    void onFatalUncaught(final String threadName, final Throwable t) {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonServerUncaughtExceptionHandler.class);
        //final Logger logger = Loggers.getLogger(ElasticsearchUncaughtExceptionHandler.class, loggingPrefixSupplier.get());
        logger.error("fatal error in thread [{}], exiting", threadName, t);
    }

    // visible for testing
    void onNonFatalUncaught(final String threadName, final Throwable t) {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(CommonServerUncaughtExceptionHandler.class);

        logger.warn("uncaught exception in thread [{}]", threadName, t);
    }


    // visible for testing
    void halt(int status) {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            @SuppressForbidden(reason = "halt")
            @Override
            public Void run() {
                // we halt to prevent shutdown hooks from running
                Runtime.getRuntime().halt(status);
                return null;
            }
        });
    }
}
