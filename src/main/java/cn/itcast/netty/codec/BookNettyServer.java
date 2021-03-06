package cn.itcast.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

/**
 * Netty服务端，传递Protobuf数据
 */
public class BookNettyServer {
    public static void main(String[] args) throws InterruptedException {
        //1.创建Boss线程组，处理客户端连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //2.创建Worker线程组，处理网络操作
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
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
                        //添加Protobuf的解码器
                        pipeline.addLast("decoder", new ProtobufDecoder(BookMessage.Book.getDefaultInstance()));
                        //9.向pipeline的尾部添加一个自定义的处理器
                        pipeline.addLast(new NettyServerHandler());
                    }
                });
        System.out.println("Server：Netty服务端已经准备好了...");
        //10.绑定端口，启动服务端，bind方法是异步的，sync同步阻塞的
        ChannelFuture future = bootstrap.bind(9999).sync();
        System.out.println("Server：Netty服务端启动完毕...");

        //11.关闭通道，关闭线程组
        future.channel().closeFuture().sync();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    /**
     * 客户端的处理消息和发送消息的处理器
     */
    private static final class NettyServerHandler extends ChannelInboundHandlerAdapter {
        /**
         * 读取数据事件
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Server：" + ctx);
            BookMessage.Book book = (BookMessage.Book) msg;
            System.out.println("Server：收到客户端发来的消息 => " + book.getName());
        }

        /**
         * 异常发生事件
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //发生异常，关闭通道
            ctx.close();
        }
    }
}