package cn.itcast.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天服务端
 */
public class ChatServer {
    public static void main(String[] args) throws InterruptedException {
        //1.创建Boss线程组，处理客户端连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //2.创建Worker线程组，处理网络操作
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //3.创建服务端启动器
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    //4.设置2个线程组
                    .group(bossGroup, workerGroup)
                    //5.使用NioServerSocketChannel作为服务器端通讯的实现
                    .channel(NioServerSocketChannel.class)
                    //6.设置线程队列中等待连接数的个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //7.保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //8.创建通道初始化器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //添加解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //添加编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            //9.向pipeline的尾部添加一个自定义的处理器
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("Server：Netty服务端已经准备好了...");
            //10.绑定端口，启动服务端，bind方法是异步的，sync同步阻塞的
            ChannelFuture future = bootstrap.bind(9999).sync();
            System.out.println("Server：Netty服务端启动完毕...");

            //11.关闭通道
            future.channel().closeFuture().sync();
        } finally {
            //12.关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 客户端的处理消息和发送消息的处理器
     */
    private static final class NettyServerHandler extends SimpleChannelInboundHandler<String> {
        /**
         * 连接的通道（所有的聊天用户）
         */
        private final List<Channel> channels = new ArrayList<>();

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel inChannel = ctx.channel();
            channels.add(inChannel);
            System.out.println("[Server]:" + inChannel.remoteAddress().toString().substring(1) + "上线");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Channel inChannel = ctx.channel();
            channels.remove(inChannel);
            System.out.println("[Server]:" + inChannel.remoteAddress().toString().substring(1) + "离线");
        }

        /**
         * 读取数据事件
         */
        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            Channel inChannel = ctx.channel();
            //收到用户发来的消息，转发给其他用户
            System.out.println("Server：" + ctx);
            System.out.println("Server：收到客户端发来的消息 => " + msg);
            for (Channel channel : channels) {
                if (channel != inChannel) {
                    channel.writeAndFlush("[" + inChannel.remoteAddress().toString().substring(1) + "]" + "说：" + msg + "\n");
                }
            }
        }

        /**
         * 异常发生事件
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            channels.remove(ctx.channel());
            //发生异常，关闭通道
            ctx.close();
        }
    }
}