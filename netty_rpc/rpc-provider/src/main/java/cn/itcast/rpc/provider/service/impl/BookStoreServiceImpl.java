package cn.itcast.rpc.provider.service.impl;

import cn.itcast.rpc.provider.service.BookStoreService;

/**
 * 书籍存储实现
 */
public class BookStoreServiceImpl implements BookStoreService {
    public boolean saveBook(String book) {
        System.out.println("保存书籍成功：" + book);
        return true;
    }
}