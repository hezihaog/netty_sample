package cn.itcast.netty.basic;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * Netty客户端
 */
public class NettyClient {
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
                        //6.初始化通道，往Pipeline链中添加Handler
                        socketChannel.pipeline().addLast(new NettyClientHandler());
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
            ctx.writeAndFlush(Unpooled.copiedBuffer("您好，服务器端，我是小何", CharsetUtil.UTF_8));
        }

        /**
         * 读取数据事件
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("服务器端发来的消息：" + buf.toString(CharsetUtil.UTF_8));
        }
    }
}