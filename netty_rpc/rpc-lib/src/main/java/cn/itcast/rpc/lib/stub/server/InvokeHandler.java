package cn.itcast.rpc.lib.stub.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 收到远程调用后，反射调用本地方法
 */
public class InvokeHandler extends ChannelInboundHandlerAdapter {
    //得到某接口下某个实现类的名字
    private String getImplClassName(CallInfo callInfo) throws Exception {
        String className = callInfo.getClassName();
        int lastDot = className.lastIndexOf(".");
        //服务方接口和实现类所在的包路径
        String interfacePath = className.substring(0, lastDot);
        //获取接口名
        String interfaceName = callInfo.getClassName().substring(lastDot);
        //组合成接口的完整路径
        Class<?> superClass = Class.forName(interfacePath + interfaceName);
        Reflections reflections = new Reflections(interfacePath);
        //得到某接口下的所有实现类
        Set<Class<?>> implClassSet = reflections.getSubTypesOf((Class<Object>) superClass);
        if (implClassSet.size() == 0) {
            System.out.println("未找到实现类");
            return null;
        } else if (implClassSet.size() > 1) {
            System.out.println("找到多个实现类，未明确使用哪一个");
            return null;
        } else {
            //把集合转换为数组
            Class<?>[] classes = implClassSet.toArray(new Class[0]);
            //得到实现类的名字
            return classes[0].getName();
        }
    }

    /**
     * 读取客户端发来的数据并通过反射调用实现类的方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        CallInfo callInfo = (CallInfo) msg;
        Object clazz = Class.forName(getImplClassName(callInfo)).newInstance();
        Method method = clazz.getClass().getMethod(callInfo.getMethodName(), callInfo.getTypes());
        //通过反射调用实现类的方法
        Object result = method.invoke(clazz, callInfo.getObjects());
        ctx.writeAndFlush(result);
    }
}