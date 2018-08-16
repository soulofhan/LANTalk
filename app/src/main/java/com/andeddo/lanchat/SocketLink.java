package com.andeddo.lanchat;

import java.net.Socket;

public class SocketLink {
    private String IPAddress;
    private int PORT;

    /**
     *
     * @param IPAddress 传入连接的IP地址
     * @param PORT 传入连接的端口号
     */
    public SocketLink(String IPAddress,int PORT){
        this.IPAddress = IPAddress;
        this.PORT = PORT;
    }

    private void connect(){
        Socket mSocket = new Socket();
    }

}
