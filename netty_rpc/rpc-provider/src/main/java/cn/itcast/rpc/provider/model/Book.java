package cn.itcast.rpc.provider.model;

import java.io.Serializable;

/**
 * 书籍实体
 */
public class Book implements Serializable {
    /**
     * 书籍Id
     */
    private Integer id;
    /**
     * 书籍名称
     */
    private String name;
    /**
     * 书籍作者
     */
    private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}