package xuhuimin.test.cs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAll {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8088);

        while (true){
            Socket accept = serverSocket.accept();
            HttpServer serverAll = new HttpServer(accept);
            System.out.println("服务器开启！");
            serverAll.start();
        }

    }
}
