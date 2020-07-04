package cn.itcast.rpc.provider.service;

import cn.itcast.rpc.provider.model.Book;

/**
 * 书籍服务
 */
public interface BookService {
    /**
     * 获取一本热门书籍信息
     */
    Book getHotBook();

    /**
     * 保存一本书籍
     */
    boolean saveBook(Book book);
}