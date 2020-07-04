package cn.itcast.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * 聊天客户端
 */
public class ChatClient {
    public static void main(String[] args) throws InterruptedException {
        //1.创建一个线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
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
                            //添加解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //添加编码器
                            pipeline.addLast("encoder", new StringEncoder());
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

            //8.读取控制台输入
            Channel channel = future.channel();
            System.out.println("------" + channel.localAddress().toString().substring(1) + "------");

            String tip = "请输入您要发送的消息：";
            System.out.println(tip);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String msg = scanner.nextLine();
                channel.writeAndFlush(msg + "\r\n");
                System.out.println("<=> 发送消息成功...\r\n");
                System.out.println(tip + "\r\n");
            }

            //9.关闭连接，异步非阻塞
            future.channel().closeFuture().sync();
        } finally {
            //10.关闭线程组
            group.shutdownGracefully();
        }
    }

    /**
     * 客户端的处理消息和发送消息的处理器
     */
    private static final class NettyClientHandler extends SimpleChannelInboundHandler<String> {
        /**
         * 收到服务端发来的消息
         */
        @Override
        public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("服务器端发来的消息：" + msg);
        }
    }
}