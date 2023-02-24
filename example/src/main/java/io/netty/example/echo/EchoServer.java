/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Echoes back any received data from a client.
 */

//编解码，IO模型，线程模型
public final class EchoServer {

    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) {
        System.out.println(-1/2);
        System.out.println(-2/2);
        System.out.println(-3/2);
    }

    public static void main1(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        //一个EventLoop就是一个线程，维护了一个selector，channel可以往上注册感兴趣的事件

        // Configure the server.
        // 为什么bossGroup只会用到一个线程：因为一般只bind一个端口
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // workerGroup怎么给SocketChannel选择EventLoop的？
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EchoServerHandler serverHandler = new EchoServerHandler();
        try {
            ServerBootstrap b = new ServerBootstrap();
            // [neo] 设置bossGroup的作用
            b.group(bossGroup, workerGroup)
             // AbstractBootstrap.channel
             //只是设置了channel的class，后面连接来的时候，会根据这个class创建channel
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 100)

             // [neo] 两种keepalive设置
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childOption(NioChannelOption.SO_KEEPALIVE, true)

            /* [neo]
                pooled/unpooled切换方式之一：通过编码指定类型
                默认的分配方式：io.netty.channel.DefaultChannelConfig

                堆内/外分配切换方式之一：创建分配器时指定preferDirect选项
            */
             //.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
             //.childOption(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)

             .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))


             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     p.addLast(serverHandler);
                 }
             });

            // Start the server.
            // [neo] bind触发真正的initAndRegister
            ChannelFuture f = b.bind(PORT).sync();

            // Wait until the server socket is closed.
            //f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
