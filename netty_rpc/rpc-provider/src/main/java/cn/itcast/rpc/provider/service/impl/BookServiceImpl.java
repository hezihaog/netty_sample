package cn.itcast.rpc.provider.service.impl;

import cn.itcast.rpc.provider.model.Book;
import cn.itcast.rpc.provider.service.BookService;

/**
 * 书籍热门服务实现
 */
public class BookServiceImpl implements BookService {
    public Book getHotBook() {
        Book book = new Book();
        book.setId(1);
        book.setName("Java入门到放弃");
        book.setAuthor("Java布道师");
        return book;
    }

    public boolean saveBook(Book book) {
        System.out.println("保存书籍成功：" + book);
        return true;
    }
}