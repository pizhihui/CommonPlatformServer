package com.yonyou.datafin.controller;

import com.alibaba.fastjson.JSONObject;
import com.yonyou.datafin.annotation.Remote;
import com.yonyou.datafin.hbase.HBaseAdminHelper;
import com.yonyou.datafin.hbase.HBaseTableAccess;
import com.yonyou.datafin.hbase.factory.HBaseConnectionFactory;
import com.yonyou.datafin.hbase.wrapper.HBaseRecordWrapper;
import com.yonyou.datafin.model.HbaseDataModel;
import com.yonyou.datafin.netty.param.ResponseParam;
import com.yonyou.datafin.netty.param.ResponseUtil;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Map;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */

@Controller
public class HbaseController {

    private Logger logger = LoggerFactory.getLogger(HbaseController.class);
    /**
     * 存入hbase数据
     * @param model
     */
    @Remote("put")
    public ResponseParam put(HbaseDataModel model) {

        HBaseAdminHelper helper = null;
        try {
            helper = new HBaseAdminHelper(HBaseConnectionFactory.getConnection());
            // 校验表是否存在
            if (!helper.tableExists(model.getTable())) {
                throw new TableNotFoundException("table " + model.getTable() + " was not found");
            }
            HBaseRecordWrapper record = new HBaseRecordWrapper(model.getRowKey(), model.getFamily(), model.getColumnValues());
            HBaseTableAccess access = new HBaseTableAccess(model.getTable());
            access.write(record);

        } catch (Exception e) {
            //throw new BaseRuntimeException("put hbase data error: {}", e);
            return ResponseUtil.createFailResult("put hbase data error: " + e.getMessage());
        }
        // 返回结果
        logger.error("success put hbase data: {}", model.getRowKey());
        return ResponseUtil.createSuccessResult();
    }


    @Remote("getRow")
    public ResponseParam getByRowKey(String table, String rowKey, String family) {
        Map<String, Object> values = null;
        try {
            HBaseTableAccess dataAccess = new HBaseTableAccess(table);
            values = dataAccess.queryByRowkey(rowKey, family);
        } catch (IOException e) {
            return ResponseUtil.createFailResult("get hbase data error: " + e.getMessage());
        }
        logger.info("success get hbase data: {}", rowKey);
        return ResponseUtil.createSuccessResult(JSONObject.toJSON(values));
    }

}
