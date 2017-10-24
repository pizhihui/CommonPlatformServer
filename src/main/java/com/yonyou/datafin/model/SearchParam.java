package com.yonyou.datafin.model;

/**
 * @author: pizhihui
 * @datae: 2017-10-24
 */
public class SearchParam {

    private String table;
    private String rowKey;
    private String family;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }
}
