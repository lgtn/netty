package com.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netty.server.handler.NettyServerHandler;

/**
 * netty服务端
 * @author TL
 */
public class NettyServer {
	
	private static Logger log = LoggerFactory.getLogger(NettyServer.class);
	
	private static final int PORT = 9600;
	
	public static void main(String[] args) throws Exception {
		
		//事件监控，用于channel注册，可看做是selector，主线程
		EventLoopGroup group = null;
		
		try {
			//nio
			group = new NioEventLoopGroup();
			
			final NettyServerHandler handler = new NettyServerHandler();
			
			//服务
			ServerBootstrap server = new ServerBootstrap();
			
			server.group(group).//使用NioEventLoopGroup来接收和处理连接
				channel(NioServerSocketChannel.class).//将通道指定为NIO类型
					localAddress(PORT). //监听的端口
						childHandler(new ChannelInitializer<Channel>() {
							@Override
							protected void initChannel(Channel ch) throws Exception {
								ch.pipeline().addLast(handler);  //指定消息处理器
							}
			});
			
			//异步地绑定服务器；调用sync()方法阻塞等待直到绑定完成
			ChannelFuture f = server.bind().sync(); 
			
			log.info("服务端创建成功！");
			
			// 获取Channel的CloseFuture，并且阻塞当前线程直到它完成
			f.channel().closeFuture().sync();
			
		} catch (Exception e) {
			log.error("初始化异常", e);
		} finally{
			//EventLoopGroup，释放所有的资源
			group.shutdownGracefully().sync(); 
		}
		
	}

}
