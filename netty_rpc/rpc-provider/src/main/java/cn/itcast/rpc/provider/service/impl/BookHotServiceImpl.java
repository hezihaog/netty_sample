package cn.itcast.rpc.provider.service.impl;

import cn.itcast.rpc.provider.service.BookHotService;

/**
 * 书籍热门服务实现
 */
public class BookHotServiceImpl implements BookHotService {
    public String getHotBookName() {
        return "Java入门到放弃";
    }
}