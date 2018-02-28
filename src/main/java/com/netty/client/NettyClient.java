package com.netty.client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

/**
 * netty客户端测试
 * @author TL
 *
 */
public class NettyClient {

	public static void main(String[] args) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		final NettyClientHandler client = new NettyClientHandler("test1");
		try {
			Bootstrap b = new Bootstrap();
//			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
			b.group(group).
				channel(NioSocketChannel.class).
					remoteAddress("127.0.0.1", 9600).
						handler(new ChannelInitializer<SocketChannel>() {

							@Override
							protected void initChannel(SocketChannel ch)
									throws Exception {
								ch.pipeline().addLast(new IdleStateHandler(10, 5, 20, TimeUnit.SECONDS));
								ch.pipeline().addLast(client);
							}
						});
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
			System.out.println(client.response);
		} catch (Exception e) {
			group.shutdownGracefully().sync();
		}
	}
}
