/**
 * Copyright 2014 Christian Kreutzfeldt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.mnxfst.stream.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Main class starting things up
 * @author mnxfst
 * @since 05.02.2014
 */
public class StreamAnalyzerServer {

	private final int port;
	
	/**
	 * Initializes the server using the provided input
	 * @param port
	 */
	public StreamAnalyzerServer(final int port) {
		this.port = port;
	}
	
	public void run() throws Exception {
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
	            .childHandler(new StreamAnalyzerChannelInitializer());

			Channel ch = b.bind(port).sync().channel();
			ch.closeFuture().sync();
		} finally {
	    	bossGroup.shutdownGracefully();
	        workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		new StreamAnalyzerServer(9090).run();
	}
}
