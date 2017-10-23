package com.yonyou.datafin.controller;

import com.yonyou.datafin.annotation.Remote;
import com.yonyou.datafin.hbase.HBaseAdminHelper;
import com.yonyou.datafin.hbase.HBaseTableAccess;
import com.yonyou.datafin.hbase.factory.HBaseConnectionFactory;
import com.yonyou.datafin.hbase.wrapper.HBaseRecordWrapper;
import com.yonyou.datafin.model.HbaseDataModel;
import com.yonyou.datafin.netty.param.ResponseParam;
import com.yonyou.datafin.netty.param.ResponseUtil;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.springframework.stereotype.Controller;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */

@Controller
public class HbaseController {

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
        return ResponseUtil.createSuccessResult();
    }

}
