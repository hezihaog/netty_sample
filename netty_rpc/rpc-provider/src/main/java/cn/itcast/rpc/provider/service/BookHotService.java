package cn.itcast.rpc.provider.service;

/**
 * 热门书籍服务
 */
public interface BookHotService {
    /**
     * 获取一本热门书籍的名称
     */
    String getHotBookName();
}