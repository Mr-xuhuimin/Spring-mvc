package xuhuimin.test.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        //连接服务器
        Socket socket = new Socket("localhost",8080);
        System.out.println("成功连接服务器！");
        try(BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out=new  PrintWriter(socket.getOutputStream());
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))
                ) {
            String s = sin.readLine();
            out.println(s);
            out.flush();
            //接受来自服务器的消息
            System.out.println("server："+in.readLine());
        }
    }
}
