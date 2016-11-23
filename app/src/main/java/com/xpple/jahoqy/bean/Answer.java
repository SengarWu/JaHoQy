package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Koreleone on 2015/9/29.
 */
public class Answer extends BmobObject {
    private User user;
    private com.xpple.jahoqy.bean.Question Question;
    private String content;
    private String Result;
    private int reword;

    public int getReword() {
        return reword;
    }

    public void setReword(int reword) {
        this.reword = reword;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public com.xpple.jahoqy.bean.Question getQuestion() {
        return Question;
    }

    public void setQuestion(com.xpple.jahoqy.bean.Question question) {
        Question = question;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
