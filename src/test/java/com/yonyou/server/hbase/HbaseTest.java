package com.yonyou.server.hbase;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.datafin.exception.BaseException;
import com.yonyou.datafin.hbase.HBaseAdminHelper;
import com.yonyou.datafin.hbase.HBaseTableAccess;
import com.yonyou.datafin.hbase.factory.HBaseConnectionFactory;
import com.yonyou.datafin.hbase.wrapper.HBaseRecordWrapper;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring.xml"})
public class HbaseTest {


    @Test
    public void testHbasePut() throws BaseException, IOException {
        String table = "hbase_api_test";
        String rowkey = "bbbbb";
        String family = "info";
        String data = "{\"spider_name\": \"bankofbbg_com\", \"infotype\": \"002\",}";


        Map<String, Object> columnValues = (Map<String, Object>) JSONObject.parse(data);
        // 校验表是否存在
        HBaseAdminHelper helper = new HBaseAdminHelper(HBaseConnectionFactory.getConnection());
        if (!helper.tableExists(table)) {
            throw new TableNotFoundException("table " + table + " was not found");
        }
        HBaseRecordWrapper record = new HBaseRecordWrapper(rowkey, family, columnValues);
        HBaseTableAccess access = new HBaseTableAccess(table);
        access.write(record);
    }

}
