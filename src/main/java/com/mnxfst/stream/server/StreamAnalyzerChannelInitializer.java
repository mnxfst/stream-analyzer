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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * TODO
 * @author mnxfst
 * @since 05.02.2014
 */
public class StreamAnalyzerChannelInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
	 */
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline chPipeline = channel.pipeline();
		chPipeline.addLast("codec", new HttpServerCodec());
		chPipeline.addLast("handler", new StreamAnalyzerServerHandler());
	}

}
