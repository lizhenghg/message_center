package com.cracker.api.mc.mq;

import java.io.Serializable;

/**
 * MQ消息传输类
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-12-08
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class MqMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Serializable content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Serializable getContent() {
        return content;
    }

    public void setContent(Serializable content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("MqMessage [title: %s, content: %s]", this.getTitle(), this.getContent());
    }
}