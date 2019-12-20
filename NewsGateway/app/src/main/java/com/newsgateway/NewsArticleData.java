package com.newsgateway;

import java.io.Serializable;

class NewsArticleData implements Serializable
{
    private String author;
    private String title;
    private String description;
    private String image;
    private String publish;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public NewsArticleData(String author, String title, String description, String image, String publish, String url)
    {
        this.author = author;
        this.title = title;
        this.description = description;
        this.image = image;
        this.publish = publish;
        this.url = url;
    }

    public NewsArticleData(String author, String title, String description, String image)
    {
        this.author = author;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        return image;
    }

    public void setUrlToImage(String image) {
        this.image = image;
    }

    public String getPublishedAt() {
        return publish;
    }

    public void setPublishedAt(String publish) {
        this.publish = publish;
    }
}
