package cn.itcast.rpc.consumer;

import cn.itcast.rpc.lib.stub.client.RPCProxy;
import cn.itcast.rpc.provider.model.Book;
import cn.itcast.rpc.provider.service.BookService;

/**
 * 客户端入口类
 */
public class ConsumerMain {
    public static void main(String[] args) {
        //远程调用，获取热门书籍
        BookService bookService = RPCProxy.create(BookService.class);
        Book hotBook = bookService.getHotBook();
        System.out.println("远程RPC调用获取热门书籍：" + hotBook);

        //远程调用，保存书籍
        Book book = new Book();
        book.setId(1);
        book.setName("MySQL必知必会");
        book.setAuthor("MySQL");
        System.out.println("保存书籍：" + book);
        boolean isSuccess = bookService.saveBook(book);
        if (isSuccess) {
            System.out.println("书籍保存 => 成功");
        } else {
            System.out.println("书籍保存 => 失败");
        }
    }
}