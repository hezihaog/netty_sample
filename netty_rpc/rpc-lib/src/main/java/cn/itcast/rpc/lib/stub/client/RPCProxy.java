package cn.itcast.rpc.lib.stub.client;

import cn.itcast.rpc.lib.stub.server.CallInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * RPC远程调用的代理生成类
 */
public class RPCProxy {
    public static <T> T create(Class<T> target) {
        return (T) proxy(target, "127.0.0.1", 9999);
    }

    public static <T> T create(Class<T> target, String host, int port) {
        return (T) proxy(target, host, port);
    }

    /**
     * 根据接口创建代理对象
     *
     * @param target 要获取的服务接口
     * @param host   服务提供的ip地址
     * @param port   服务提供的端口号
     */
    private static Object proxy(Class<?> target, String host, int port) {
        return Proxy.newProxyInstance(target.getClassLoader(),
                new Class[]{target}, new RemoteMethodCall(target, host, port));
    }

    private static class RemoteMethodCall implements InvocationHandler {
        /**
         * 接口的Class
         */
        private final Class<?> targetClass;
        /**
         * 远程服务的地址
         */
        private String host;
        /**
         * 远程服务的端口号
         */
        private int port;

        public RemoteMethodCall(Class<?> targetClass, String host, int port) {
            this.targetClass = targetClass;
            this.host = host;
            this.port = port;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //封装调用信息到ClassInfo
            CallInfo callInfo = new CallInfo();
            callInfo.setClassName(targetClass.getName());
            callInfo.setMethodName(method.getName());
            callInfo.setObjects(args);
            callInfo.setTypes(method.getParameterTypes());
            //使用Netty发送数据到接口提供方
            EventLoopGroup group = new NioEventLoopGroup();
            ResultHandler resultHandler = new ResultHandler();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                //编码器
                                pipeline.addLast("encoder", new ObjectEncoder());
                                //解码器  构造方法第一个参数设置二进制数据的最大字节数  第二个参数设置具体使用哪个类解析器
                                pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                //客户端业务处理类
                                pipeline.addLast("handler", resultHandler);
                            }
                        });
                ChannelFuture future = bootstrap.connect(host, port).sync();
                future.channel().writeAndFlush(callInfo).sync();
                future.channel().closeFuture().sync();
            } finally {
                group.shutdownGracefully();
            }
            return resultHandler.getResponse();
        }
    }
}