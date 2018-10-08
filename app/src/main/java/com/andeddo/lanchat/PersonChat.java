package com.andeddo.lanchat;

class PersonChat {
    private int id;             //id暂无
    private String name;        //聊天昵称
    private String chatMsg;     //聊天内容
    private boolean isMeSend;   //是否自己发送

    public int getId(){
        return id;
    }

    /**
     * 设置预置头像
     * @param id 头像int值
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * 设置聊天昵称
     * @param name 聊天昵称
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getChatMsg() {
        return chatMsg;
    }

    /**
     * 设置聊天内容
     * @param chatMessage 聊天内容
     */
    public void setChatMsg(String chatMessage) {
        this.chatMsg = chatMessage;
    }

    public boolean isMeSend() {
        return isMeSend;
    }

    /**
     * 设置是否为自己发送的消息
     * @param isMeSend true false
     */
    public void setMeSend(boolean isMeSend) {
        this.isMeSend = isMeSend;
    }

    /**
     * 设置消息各属性
     * @param id 头像int值
     * @param name 聊天昵称
     * @param chatMessage 聊天内容
     * @param isMeSend 是否自己发送
     */
    public PersonChat(int id, String name, String chatMessage, boolean isMeSend) {
        super();
        this.id = id;
        this.name = name;
        this.chatMsg = chatMessage;
        this.isMeSend = isMeSend;
    }

    public PersonChat() {
        super();
    }
}
