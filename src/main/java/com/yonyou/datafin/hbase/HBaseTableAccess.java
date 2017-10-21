package com.yonyou.datafin.hbase;

import com.alibaba.fastjson.JSON;
import com.yonyou.datafin.hbase.factory.HBaseConnectionFactory;
import com.yonyou.datafin.hbase.utils.HBaseByteUtils;
import com.yonyou.datafin.hbase.wrapper.HBaseRecordWrapper;
import com.yonyou.datafin.hbase.wrapper.HBaseTableWrapper;
import com.yonyou.datafin.hbase.wrapper.ValueWrapper;
import com.yonyou.datafin.utils.Toolkit;
import com.yonyou.datafin.exception.BaseException;
import com.yonyou.datafin.hbase.wrapper.HBasePutWrapper;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 操作Hbase表数据
 * @author jiwenlong
 * 2017-06-19
 */
public class HBaseTableAccess {
	private String tableName;
	private Logger logger = LogManager.getLogger(HBaseTableAccess.class);
	
	public HBaseTableAccess(String tableName){
		this.tableName = tableName;
	}
	
	
	public void createTableIfNotExist(String... families) throws IOException{
		try (HBaseAdminHelper helper = new HBaseAdminHelper(HBaseConnectionFactory.getConnection());) {
			if (!helper.tableExists(tableName)) {
				if (families == null) {
					throw new RuntimeException("createSql is null and " + tableName + "not Exist");
				}
				helper.createTable(tableName, families);
			}
		}
	}
	
	/**
	 * 插入多个family(rowkey同)
	 * @param data<HBaseRecordWrapper>
	 */
	public void writeBatch(List<HBaseRecordWrapper> data) throws BaseException {
		 String rowkey = null;
		 if (data != null && !data.isEmpty()){
			 rowkey = data.get(0).getRowkey();
		 }
		 HBasePutWrapper putWrapper = new HBasePutWrapper(HBaseByteUtils.toBytes(rowkey), false);
		 for (HBaseRecordWrapper fimilyData : data){
			 for (Map.Entry<String, Object> entry: fimilyData.getData().entrySet()){
				 Object obj  = entry.getValue();
				 String str = null;
				 if(obj instanceof String){
					 str = obj.toString();
				 }else{
					 str = JSON.toJSONString(obj);
				 }
				 putWrapper.addColumn(HBaseByteUtils.toBytes(fimilyData.getFamilyName()), HBaseByteUtils.toBytes(entry.getKey()), HBaseByteUtils.toBytes(str));
			 }
		 }
		 try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			 tableWrapper.put(putWrapper);
		 }catch (IOException e) {
			throw new BaseException("hbase writeBatch occur error!",e);
		}
	}
	
	/**
	 * 插入一个family
	 * @param recordWrapper
	 */
	public void write(HBaseRecordWrapper recordWrapper) throws BaseException{
		 String rowkey = recordWrapper.getRowkey();
		 HBasePutWrapper putWrapper = new HBasePutWrapper(HBaseByteUtils.toBytes(rowkey), false);
		 for (Map.Entry<String, Object> entry: recordWrapper.getData().entrySet()){
			 Object obj  = entry.getValue();
			 String str = null;
			 if(obj instanceof String){
				 str = obj.toString();
			 }else{
				 str = JSON.toJSONString(obj);
			 }
			 putWrapper.addColumn(HBaseByteUtils.toBytes(recordWrapper.getFamilyName()), HBaseByteUtils.toBytes(entry.getKey()), HBaseByteUtils.toBytes(str));
		 }
		 try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			 tableWrapper.put(putWrapper);
		 }catch(IOException e){
			 logger.error(e);
			 throw new BaseException("hbase write occur error!",e);
		 }
	}
	
	
	/**
	 * 插入一个key value
	 * @param rowkey
	 * @param family
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void writeToTable(String rowkey, String family, String key, String value) throws BaseException{
		 HBasePutWrapper putWrapper = new HBasePutWrapper(HBaseByteUtils.toBytes(rowkey), false);
		 putWrapper.addColumn(HBaseByteUtils.toBytes(family), HBaseByteUtils.toBytes(key), HBaseByteUtils.toBytes(value));
		 try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			 tableWrapper.put(putWrapper);
		 }catch(IOException e){
			 logger.error(e);
			 throw new BaseException("hbase write occur error!",e);
		 }
	}
	
	public void writeToTable(HBaseTableWrapper tableWrapper, String rowkey, String family, String key, String value) throws BaseException{
		 HBasePutWrapper putWrapper = new HBasePutWrapper(HBaseByteUtils.toBytes(rowkey), false);
		 putWrapper.addColumn(HBaseByteUtils.toBytes(family), HBaseByteUtils.toBytes(key), HBaseByteUtils.toBytes(value));
		 try{
			 tableWrapper.put(putWrapper);
		 }catch(IOException e){
			 logger.error(e);
			 throw new BaseException("hbase write occur error!",e);
		 }
	}
	
	
	/**
	 * 根据rowkey获取一个列值
	 * @param rowkey
	 * @param queryFamilyName
	 * @param queryColumn
	 * @return
	 * @throws IOException
	 */
	public ValueWrapper queryByRowkeyWithTS(String rowkey, String queryFamilyName, String queryColumn) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			Result result = tableWrapper.get(toGet);
			byte[] value = getRowColumnLatestValue(result, queryFamilyName, queryColumn);
			if (value != null){
				Cell cell = result.getColumnLatestCell(HBaseByteUtils.toBytes(queryFamilyName), HBaseByteUtils.toBytes(queryColumn));
				return new ValueWrapper(HBaseByteUtils.toString(value), cell.getTimestamp());
			}
			return null;
		}
	}
	
	/**
	 * @param rowkey
	 * @param queryColumns 要查询的列的集合，列族和列用逗号分隔
	 * @return
	 * @throws IOException
	 */
	public List<ValueWrapper> queryMultiColsByRowkeyWithTS(String rowkey, List<String> queryColumns) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			Result result = tableWrapper.get(toGet);
			List<ValueWrapper> valueLst = new ArrayList<ValueWrapper>();
			for (String str : queryColumns){
				String[] familyColumn = str.split(",",-1);
				byte[] value = getRowColumnLatestValue(result, familyColumn[0], familyColumn[1]);
				if (value != null){
					Cell cell = result.getColumnLatestCell(HBaseByteUtils.toBytes(familyColumn[0]), HBaseByteUtils.toBytes(familyColumn[1]));
					ValueWrapper valueWrapper = new ValueWrapper( HBaseByteUtils.toString(value), cell.getTimestamp());
					valueLst.add(valueWrapper);
				}
			}
			return valueLst;
		}
	}
	
	/**
	 * 根据rowkey获取一个列值
	 * @param rowkey
	 * @param queryFamilyName
	 * @param queryColumn
	 * @return
	 * @throws IOException
	 */
	public String queryByRowkey(String rowkey, String queryFamilyName, String queryColumn) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			Result result = tableWrapper.get(toGet);
			byte[] value = getRowColumnLatestValue(result, queryFamilyName, queryColumn);
			if (value != null){
				return HBaseByteUtils.toString(value);
			}
			return null;
		}
	}
	
	/**
	 * 查询多个列族下的所有值
	 * @param rowkey
	 * @param queryFamilyNames
	 * @return
	 * @throws IOException
	 */
	public Map<String,Map<String, String>> queryFamiliesByRowkey(String rowkey,String... queryFamilyNames) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			//Scan scan = new Scan(HBaseByteUtils.toBytes(rowkey));
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			
			for (String family : queryFamilyNames){
				toGet.addFamily(Bytes.toBytes(family));
				//scan.addFamily(Bytes.toBytes(family));
			}
			//ResultScanner resScan = tableWrapper.getResultScanner(scan);
			//Result result = resScan.next();
			Result result = tableWrapper.get(toGet);
			Map<String,Map<String, String>> wrapper = new HashMap<String,Map<String, String>>();
			if(null != result){
				List<Cell> cells = result.listCells();
				if (cells != null && cells.size() > 0){
					for (Cell cell : cells) {
						String faimily = new String(CellUtil.cloneFamily(cell));
						if (wrapper.get(faimily) == null){
							Map<String,String> oneFamilyValues = new HashMap<String,String>();
							wrapper.put(faimily, oneFamilyValues);
						}
						Map<String,String> oneFamilyValues = wrapper.get(faimily);
						oneFamilyValues.put(new String(CellUtil.cloneQualifier(cell)),
								new String(CellUtil.cloneValue(cell)));
					}
				}
			}
		    return wrapper;
		}
	}
	
	/**
	 * 查询某一列族下的所有值
	 * @param rowkey
	 * @param queryFamilyName
	 * @return
	 * @throws IOException
	 */
	public Map<String,Object> queryByRowkey(String rowkey,String queryFamilyName) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			toGet.addFamily(Bytes.toBytes(queryFamilyName));
			Result result = tableWrapper.get(toGet);
			
			//Scan scan = new Scan(HBaseByteUtils.toBytes(rowkey));
		    //scan.addFamily(Bytes.toBytes(queryFamilyName));
			//ResultScanner resScan = tableWrapper.getResultScanner(scan);
			//Result result = resScan.next();
			Map<String,Object> wrapper = new HashMap<String,Object>();
			if(null != result){
				List<Cell> cells = result.listCells();
				if (cells != null && cells.size() > 0){
					for (Cell cell : cells) {
						wrapper.put(new String(CellUtil.cloneQualifier(cell)),
								new String(CellUtil.cloneValue(cell)));
					}
				}
			}
		    return wrapper;
		}
	}
	
	/**
	 * @param rowkey
	 * @param queryColumns 要查询的列的集合，列族和列用逗号分隔
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> queryMultiColsByRowkey(String rowkey, List<String> queryColumns) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Get toGet = new Get(HBaseByteUtils.toBytes(rowkey));
			Result result = tableWrapper.get(toGet);
			Map<String, String> resMap = new HashMap<String, String>();
			for (String str : queryColumns){
				String[] familyColumn = str.split(",",-1);
				byte[] value = getRowColumnLatestValue(result, familyColumn[0], familyColumn[1]);
				if (value != null){
					resMap.put(familyColumn[0] + "." + familyColumn[1], HBaseByteUtils.toString(value));
				}
			}
			return resMap;
		}
	}  
	/**
	 * @param rowkeyLower rowkey起始
	 * @param rowkeyUpper rowkey终止
	 * @param queryFamilyName 列族
	 * @param queryColumn 列
	 * @return
	 * @throws IOException
	 */
	public List<ValueWrapper> queryByRowkeyRange(String rowkeyLower, String rowkeyUpper, String queryFamilyName, String queryColumn) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			Scan scan = new Scan(HBaseByteUtils.toBytes(rowkeyLower), HBaseByteUtils.toBytes(rowkeyUpper));
			scan.addColumn(HBaseByteUtils.toBytes(queryFamilyName), HBaseByteUtils.toBytes(queryColumn));
	        // 查询产生Scanner->ResultScanner-->result
			ResultScanner resScan = tableWrapper.getResultScanner(scan);
			List<ValueWrapper> valueLst = new ArrayList<ValueWrapper>();
			Result result = resScan.next();
			while(result != null){
				byte[] value = getRowColumnLatestValue(result, queryFamilyName, queryColumn);
				if (value != null){
					Cell cell = result.getColumnLatestCell(HBaseByteUtils.toBytes(queryFamilyName), HBaseByteUtils.toBytes(queryColumn));
					ValueWrapper valueWrapper = new ValueWrapper( HBaseByteUtils.toString(value), cell.getTimestamp());
					valueLst.add(valueWrapper);
				}
				result = resScan.next();
			}
	        return valueLst;
		}
	}
	
	/**
	 * 
	 * @param rowkeyLower
	 * @param rowkeyUpper
	 * @param queryFamilyName
	 * @return
	 * @throws IOException
	 */
	 public List<Map<String,Object>> queryByRowkeyRange(String rowkeyLower, String rowkeyUpper, String queryFamilyName) throws IOException{
	    try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
	    	Scan scan = new Scan(HBaseByteUtils.toBytes(rowkeyLower), HBaseByteUtils.toBytes(rowkeyUpper));
		    scan.addFamily(Bytes.toBytes(queryFamilyName));
			ResultScanner resScan = tableWrapper.getResultScanner(scan);
			List<Map<String,Object>> valueLst = new ArrayList<Map<String,Object>>();
			for (Iterator<Result> it = resScan.iterator(); it.hasNext();) {
				Result result = it.next();
				if (result != null){
					List<Cell> cells = result.listCells();
					if (cells != null && cells.size() > 0){
						Map<String,Object> map = new HashMap<String,Object>();
						for (Cell cell : cells) {
							map.put(new String(CellUtil.cloneQualifier(cell)),
									new String(CellUtil.cloneValue(cell)));
						}
						valueLst.add(map);
					}
				}
			}
		    return valueLst;
		}
	}
  
    /**
     * 根据过滤条件查询对应的列值
     * @param filters 逗号分开,如：  列族,列名,列值
     * @param queryColumns 要查询的列的集合，列族和列用逗号分隔
     * @return 返回结果对应的列值也是用逗号分隔
     * @throws IOException
     */
    public List<String> queryMultiColsByFilter(List<String> filters,List<String> queryColumns) throws IOException{
		try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
			 // 构造scan
	    	Scan scan = new Scan();//scan = new Scan(keyLowerBound, keyUpperBound);
	    	for (String str : queryColumns){
	    		String[] familyColumn = str.split(",",-1);
	        	scan.addColumn(HBaseByteUtils.toBytes(familyColumn[0]), HBaseByteUtils.toBytes(familyColumn[1]));
	    	}
	        
	        // 添加filter
	        FilterList filterList = new FilterList();  
	        if (filters != null && !filters.isEmpty()){
		        for(String str : filters){ // 各个条件之间是“与”的关系  
		            String [] s= str.split(",");  
		            filterList.addFilter(new SingleColumnValueFilter(HBaseByteUtils.toBytes(s[0]),  HBaseByteUtils.toBytes(s[1]),  CompareOp.EQUAL,HBaseByteUtils.toBytes(s[2])));  
		        }
		        scan.setFilter(filterList);
	        }
	        
	        // 查询产生Scanner->ResultScanner-->result
	        List<String> values = new ArrayList<String>();
			ResultScanner resScan = tableWrapper.getResultScanner(scan);
			Result result = resScan.next();
			while(result != null){
				List<String> row = new ArrayList<String>();
				for (String str : queryColumns){
					String[] familyColumn = str.split(",",-1);
					row.add(HBaseByteUtils.toString(getRowColumnLatestValue(result, familyColumn[0], familyColumn[1])));
				}
				values.add(Toolkit.join(row, ","));
				result = resScan.next();
			}
	        return values;
		}
       
    }

    private byte[] getRowColumnLatestValue(Result aRow, String colFamilyName, String colName){
        byte[] result = aRow.getValue(HBaseByteUtils.toBytes(colFamilyName), HBaseByteUtils.toBytes(colName));
        return result;
    }
    
    /**
     * 删除rowKey前缀的信息
     * @param wrapper
     * @throws IOException
     */
    public void delByRowkey(HBaseRecordWrapper wrapper)throws IOException{
        try(HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(), tableName);){
	    	Scan scan = new Scan();
		    scan.addFamily(Bytes.toBytes(wrapper.getFamilyName()));
		    Filter filter = new RowFilter(CompareOp.EQUAL,
	                new RegexStringComparator(wrapper.getRowkey()+".*"));
		    scan.setFilter(filter);
			ResultScanner resScan = tableWrapper.getResultScanner(scan);
			for(Result result : resScan){
				Delete delete = new Delete(result.getRow());
				tableWrapper.delete(delete);
			}
		}
    }
    
    
	/**
	 * 根据前缀查询某个列族的值
	 * @param familyName
	 * @param rowPrefix
	 * @return
	 * @throws IOException
	 */
	public List<Map<String,Object>> scanByPrefixFilter(String familyName, String rowPrefix) throws IOException {
		try (HBaseTableWrapper tableWrapper = new HBaseTableWrapper(HBaseConnectionFactory.getConnection(),tableName);) {
			Scan scan = new Scan();
			scan.addFamily(HBaseByteUtils.toBytes(familyName));
			
			FilterList filters = new FilterList(Operator.MUST_PASS_ALL);
			PrefixFilter filter = new PrefixFilter(Bytes.toBytes(rowPrefix));
			filters.addFilter(filter);
			scan.setFilter(filters);
			//scan.setFilter(new PrefixFilter(HBaseByteUtils.toBytes(rowPrefix)));
			ResultScanner resScan = tableWrapper.getResultScanner(scan);
			List<Map<String, Object>> valueLst = new ArrayList<Map<String, Object>>();
			for (Iterator<Result> it = resScan.iterator(); it.hasNext();) {
				Result result = it.next();
				if (result != null) {
					List<Cell> cells = result.listCells();
					if (cells != null && cells.size() > 0) {
						Map<String, Object> map = new HashMap<String, Object>();
						for (Cell cell : cells) {
							map.put(new String(CellUtil.cloneQualifier(cell)), new String(CellUtil.cloneValue(cell)));
						}
						valueLst.add(map);
					}
				}
			}
			resScan.close();
			return valueLst;
		}
	}
}
