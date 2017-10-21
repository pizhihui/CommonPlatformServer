package com.yonyou.datafin.framework;

import com.yonyou.datafin.utils.Toolkit;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取properties配置
 * @author caozpa
 * 2017-06-19
 */

public class SpringPropertiesUtil extends PropertyPlaceholderConfigurer {


    private static Map<String, String> propertiesMap;
	 // Default as in PropertyPlaceholderConfigurer
	 private int springSystemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;
	 
	 public static Properties properties;
	 
	 
	 @Override
	 public void setSystemPropertiesMode(int systemPropertiesMode) {
	    super.setSystemPropertiesMode(systemPropertiesMode);
	    springSystemPropertiesMode = systemPropertiesMode;
	 }

	 @Override
	 protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props) throws BeansException {
	     super.processProperties(beanFactory, props);

	     propertiesMap = new HashMap<String, String>();
	     properties = new Properties();
	     for (Object key : props.keySet()) {
	         String keyStr = key.toString();
	         String valueStr = resolvePlaceholder(keyStr, props, springSystemPropertiesMode);
	         properties.put(keyStr, valueStr);
	         propertiesMap.put(keyStr, valueStr);
	     }
	 }
	 public static String getProperty(String name) {
	     return propertiesMap.get(name);
	 }
	 
	 public static String getProperty(String name, String defaultValue) {
	      String val = propertiesMap.get(name);
	      if (Toolkit.isEmpty(val)){
	    	  return defaultValue;
	      }
	      return val;
	 }
	 
}
