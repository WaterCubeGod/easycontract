package com.easycontract.entity.vo;

/**
 * 消息请求实体类
 */
public class MessageRequest {
    private String content;
    private String mode = "chat"; // 默认为普通对话模式

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
