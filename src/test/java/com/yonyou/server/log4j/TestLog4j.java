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


}
