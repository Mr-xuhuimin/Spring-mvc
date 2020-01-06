package xuhuimin.test.cs;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket localhost = new Socket("localhost", 8088);
        System.out.println("成功连接服务器！");
        new HttpClient(localhost).start();
    }
}
