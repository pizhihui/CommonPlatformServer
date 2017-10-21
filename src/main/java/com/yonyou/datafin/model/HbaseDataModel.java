package com.yonyou.datafin.model;

import java.util.Map;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */
public class HbaseDataModel {

    private String table;
    private String family;
    private String rowKey;
    private Map<String, Object> columnValues;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public Map<String, Object> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(Map<String, Object> columnValues) {
        this.columnValues = columnValues;
    }
}
