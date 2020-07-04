package cn.itcast.rpc.consumer;

import cn.itcast.rpc.lib.stub.client.RPCProxy;
import cn.itcast.rpc.provider.service.BookHotService;
import cn.itcast.rpc.provider.service.BookStoreService;

/**
 * 客户端入口类
 */
public class RPCMain {
    public static void main(String[] args) {
        //远程调用热门书籍
        BookHotService bookHotService = RPCProxy.create(BookHotService.class);
        String hotBookName = bookHotService.getHotBookName();
        System.out.println("远程RPC调用获取热门书籍：" + hotBookName);

        //远程调用保存书籍
        BookStoreService bookStoreService = RPCProxy.create(BookStoreService.class);
        boolean isSuccess = bookStoreService.saveBook("MySQL必知必会");
        if (isSuccess) {
            System.out.println("书籍保存 => 成功");
        } else {
            System.out.println("书籍保存 => 失败");
        }
    }
}