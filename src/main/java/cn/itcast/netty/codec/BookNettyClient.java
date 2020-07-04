package cn.itcast.netty.codec;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * Netty客户端，传递Protobuf数据
 */
public class BookNettyClient {
    public static void main(String[] args) throws InterruptedException {
        //1.创建一个线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        //2.创建启动器
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                //3.设置线程组
                .group(group)
                //4.设置客户端通道的实现类
                .channel(NioSocketChannel.class)
                //5.创建一个通道初始化对象
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //添加Protobuf编码器
                        pipeline.addLast("encoder", new ProtobufEncoder());
                        //6.初始化通道，往Pipeline链中添加Handler
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        System.out.println("Client：Netty客户端已经准备好了...");
        //7.启动客户端，连接服务器端
        ChannelFuture future = bootstrap
                //连接配置，connect方法为异步
                .connect("127.0.0.1", 9999)
                //同步阻塞
                .sync();
        System.out.println("Client：Netty客户端启动完毕...");

        //8.关闭连接，异步非阻塞
        future.channel().closeFuture().sync();
    }

    /**
     * 客户端的处理消息和发送消息的处理器
     */
    private static final class NettyClientHandler extends ChannelInboundHandlerAdapter {
        /**
         * 通道就绪事件
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Client：" + ctx);
            //发送消息给服务器
            BookMessage.Book book = BookMessage.Book
                    .newBuilder()
                    .setId(1)
                    .setName("Java入门到放弃")
                    .build();
            ctx.writeAndFlush(book);
        }
    }
}