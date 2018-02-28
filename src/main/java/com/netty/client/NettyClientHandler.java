package com.netty.client;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.CharsetUtil;

/**
 * netty客户端消息处理类
 * @author TL
 *
 */
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ByteBuf>{
	
	private static Logger log = LoggerFactory.getLogger(NettyClientHandler.class);
	
	/**
	 * 要发送的消息
	 */
	private String message;
	
	/**
	 * 服务端回执
	 */
	public String response ;
	
	private String charsetName = "GBK";
	

	public NettyClientHandler(String message) {
		this.message = message;
	}

	
	public NettyClientHandler(String message, String charsetName) {
		this.message = message;
		this.charsetName = charsetName;
	}


	/**
	 * 发送消息
	 * @throws Exception 
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(message.getBytes(charsetName)));
    }


	/**
	 * 接收服务端的回执消息
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
			throws Exception {
		byte[] res = new byte[msg.readableBytes()];
		msg.readBytes(res);
		this.response = new String(res,"GBK");
		ctx.close();
	}

	
	/**
	 * 在发生异常时，记录错误并关闭Channel 
	 */
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        log.error("客户端异常", cause);
        ctx.close();
    }
}
