package xuhuimin.test.bs;

import xuhuimin.test.bs.HttpServerBs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerBs {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(80);

        while (true){
            Socket accept = serverSocket.accept();
            HttpServerBs serverAll = new HttpServerBs(accept);
            System.out.println("服务器开启！");
            serverAll.start();
        }
    }
}
