package cn.itcast.rpc.provider.service;

/**
 * 图书存储服务
 */
public interface BookStoreService {
    /**
     * 保存一本书籍
     */
    boolean saveBook(String book);
}