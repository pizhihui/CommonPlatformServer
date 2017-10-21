package com.yonyou.datafin.hbase.wrapper;

import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;

/**
 * Put的封装类
 * @author jiwenlong
 * 2017-06-19
 */
public class HBasePutWrapper {
    Put put;

    // row_id的值
    public HBasePutWrapper(byte[] key, boolean writeToWAL) {
        this.put = new Put(key);
        this.put.setDurability(writeToWAL ? Durability.USE_DEFAULT
                : Durability.SKIP_WAL);
    }

    public void addColumn(byte[] colFamily, byte[] colName, byte[] colValue) {
        put.addColumn(colFamily, colName, colValue);
    }

    public Put getPut() {
        return put;
    }
}
