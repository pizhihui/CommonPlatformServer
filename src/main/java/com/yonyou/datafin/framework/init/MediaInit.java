package com.yonyou.datafin.framework.init;

import java.lang.reflect.Method;
import java.util.Map;

import com.yonyou.datafin.annotation.Remote;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;


@Component
public class MediaInit implements ApplicationListener<ContextRefreshedEvent>,Ordered{

	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//根据Spring容器，找到包含有Controller注解的所有bean
		Map<String,Object> beans = event.getApplicationContext().getBeansWithAnnotation(Controller.class);
		Map<String,BeanMethod> commandBeans = Media.commandBeans;
		for(String key : beans.keySet()){
			Object bean = beans.get(key);
			Method[] ms = bean.getClass().getDeclaredMethods();
			for(Method m : ms){
				if(m.isAnnotationPresent(Remote.class)){
					Remote remote = m.getAnnotation(Remote.class);
					// remote注解的名字
					String command = remote.value();
					BeanMethod  beanMethod = new BeanMethod();
					beanMethod.setBean(bean);
					beanMethod.setM(m);
					commandBeans.put(command, beanMethod);
				}
			}
			
		}
		
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
