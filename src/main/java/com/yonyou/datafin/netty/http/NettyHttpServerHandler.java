package com.yonyou.datafin.netty.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.datafin.framework.init.BeanMethod;
import com.yonyou.datafin.framework.init.Media;
import com.yonyou.datafin.netty.param.RequestParam;
import com.yonyou.datafin.netty.param.ResponseParam;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author: pizhihui
 * @datae: 2017-10-21
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger logger = LoggerFactory.getLogger(NettyHttpServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        Object result = new Object();
        try {
            String content = msg.content().toString(Charset.defaultCharset());

            RequestParam requestParam = JSONObject.parseObject(content, RequestParam.class);
            String command = requestParam.getCommand();
            BeanMethod beanMethod = Media.commandBeans.get(command);
            if(beanMethod !=null){
                Object bean = beanMethod.getBean();
                Method m = beanMethod.getM();
                Class<?> paramType = m.getParameterTypes()[0];
                Object param=null;
                if(paramType.isAssignableFrom(List.class)){
                    param = JSONArray.parseArray(JSONArray.toJSONString(requestParam.getContent()), paramType);
                }else if(paramType.getName().equals(String.class.getName())){
                    param=requestParam.getContent();
                }else{
                    param = JSON.parseObject(JSONObject.toJSONString(requestParam.getContent()), paramType);
                }
                // 调用实际的方法
                result = m.invoke(bean, param);
            }
        } catch (Exception e) {
            logger.error("netty serve handle data error: ", e);
            ResponseParam responseParam = new ResponseParam();
            responseParam.setCode("500");
            responseParam.setMsg(e.getMessage());
            result = responseParam;
        }

        DefaultFullHttpResponse response =new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSONObject.toJSONString(result).getBytes(Charset.defaultCharset())));
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,response.content().readableBytes());
        response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
        ctx.channel().writeAndFlush(response);
    }
}
