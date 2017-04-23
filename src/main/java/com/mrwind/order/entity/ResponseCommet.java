package com.mrwind.order.entity;

import java.util.Set;

/**
 * Created by ycmac on 2017/4/23.
 */
public class ResponseCommet {

    private Set<String> title;
    private String content;
    private Integer Star;

    public ResponseCommet() {
    }

    public ResponseCommet(Set<String> title, String content, Integer star) {
        this.title = title;
        this.content = content;
        Star = star;
    }

    public Set<String> getTitle() {
        return title;
    }

    public void setTitle(Set<String> title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStar() {
        return Star;
    }

    public void setStar(Integer star) {
        Star = star;
    }
}
