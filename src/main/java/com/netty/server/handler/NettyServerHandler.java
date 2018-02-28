package com.netty.server.handler;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端消息处理类
 * @author TL
 *
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter{
	
	private static Logger log = LoggerFactory.getLogger(NettyServerHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
		
		ByteBuf in = (ByteBuf) msg;
		
		log.info("是否直接内存：{}",in.isDirect());
		
		byte[] req = new byte[in.readableBytes()];
		
		in.readBytes(req);
		
		String reqMs = new String(req,"GBK");
		
		log.info("服务端收到消息，开始处理：{}", reqMs);
		
		String response = "收到消息" + reqMs;
		
		ByteBuf out = Unpooled.copiedBuffer(response.getBytes("GBK")); 
		
		//将服务端处理结果，返回客户端，但不进行flush
		ctx.write(out);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx){
		//消息完成时，全部flush消息，并关闭通道
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		log.info("服务端处理消息结束");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		log.error("服务端处理消息异常", cause);
		ctx.close();
	}

}
