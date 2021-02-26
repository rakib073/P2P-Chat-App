package com.three.p2p;

public class Message {
    private String msg;
    private int identifier;
     public Message(String msg,int identifier)
    {
        this.msg=msg;
        this.identifier=identifier;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
