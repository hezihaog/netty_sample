package cn.itcast.rpc.provider;

import cn.itcast.rpc.lib.stub.server.RPCServer;

public class Server {
    public static void main(String[] args) {
        //创建并开启远程RPC服务
        RPCServer rpcServer = new RPCServer(9999);
        rpcServer.start();
    }
}