package com.newsgateway;

import java.io.Serializable;


class ServiceGetSet implements Serializable
{
        String id;
        String name;
        String url;
        String category;



    public ServiceGetSet(String id, String name, String url, String category) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.category = category;
    }


    public ServiceGetSet(String category)
    {

        this.category = category;
    }
    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
