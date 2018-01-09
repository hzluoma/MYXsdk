package com.hg6kwan.sdk.inner.utils.Dao;

/**
 * Created by Roman on 2017/5/25.
 */

public class NoticeDomain {
    //表的字段名
    private int id;
    private String messageID;
    private String content;
    private String isNeedShown = "1";

    public String isNeedShown() {
        return isNeedShown;
    }

    public void setNeedShown(String needShown) {
        isNeedShown = needShown;
    }

    public NoticeDomain() {
    }

    public NoticeDomain(String messageID, String content, String NeedShown) {
        this.messageID = messageID;
        this.content = content;
        this.isNeedShown = NeedShown;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "NoticeDomain{" +
                "id=" + id +
                ", messageID='" + messageID + '\'' +
                ", content='" + content + '\'' +
                ", isNeedShown='" + isNeedShown + '\'' +
                '}';
    }
}
