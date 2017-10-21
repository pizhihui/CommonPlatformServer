package com.yonyou.datafin.hbase.utils;

import org.apache.hadoop.hbase.util.Bytes;

/**
 * Hbase的列值与byte[]的转换类
 * @author jiwenlong
 * 2017-06-19
 */
public class HBaseByteUtils {
	  public static int getSizeOfFloat() {
	    return Bytes.SIZEOF_FLOAT;
	  }

	  public static int getSizeOfDouble() {
	    return Bytes.SIZEOF_DOUBLE;
	  }

	  public static int getSizeOfInt() {
	    return Bytes.SIZEOF_INT;
	  }

	  public static int getSizeOfLong() {
	    return Bytes.SIZEOF_LONG;
	  }

	  public static int getSizeOfShort() {
	    return Bytes.SIZEOF_SHORT;
	  }

	  public static int getSizeOfByte() {
	    return Bytes.SIZEOF_BYTE;
	  }
	  
	  public static byte[] toBytes( String aString ) {
	    return Bytes.toBytes( aString );
	  }

	  public static byte[] toBytes( boolean aBoolean ) {
	    return Bytes.toBytes( aBoolean );
	  }

	  public static byte[] toBytes( int anInt ) {
	    return Bytes.toBytes( anInt );
	  }

	  public static byte[] toBytes( long aLong ) {
	    return Bytes.toBytes( aLong );
	  }

	  public static byte[] toBytes( float aFloat ) {
	    return Bytes.toBytes( aFloat );
	  }

	  public static byte[] toBytes( double aDouble ) {
	    return Bytes.toBytes( aDouble );
	  }

	  public static byte[] toBytesBinary( String value ) {
	    return Bytes.toBytesBinary( value );
	  }

	  public static String toString( byte[] value ) {
	    return Bytes.toString( value );
	  }

	  public static long toLong( byte[] value ) {
	    return Bytes.toLong( value );
	  }

	  public static int toInt( byte[] value ) {
	    return Bytes.toInt( value );
	  }

	  public static float toFloat( byte[] value ) {
	    return Bytes.toFloat( value );
	  }

	  public static double toDouble( byte[] value ) {
	    return Bytes.toDouble( value );
	  }

	  public static short toShort( byte[] value ) {
	    return Bytes.toShort( value );
	  }
	  
	  public static Boolean toBoolean(byte[] value) {
	    return Bytes.toBoolean(value);
	  }
	  
	  public static void main(String[] args) {
		System.out.println(toBytes("f5355fe252d7c9f30b152529e5b4f19c"));
		System.out.println(toBytes("f5355fe252d7c9f30b152529e5b4f19c"));
	}


}
