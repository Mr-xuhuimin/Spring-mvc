package xuhuimin.test.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {
    public static void main(String[] args) throws IOException {
        //新建service8080端口
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("网络服务器开启！");
        //监听并获取client端Socket方法
        Socket accept = serverSocket.accept();
        System.out.println("客户端已连接！"+accept.getInetAddress().getHostName());
        try(BufferedReader in=new BufferedReader(new InputStreamReader(accept.getInputStream()));
            BufferedReader sin= new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out=  new PrintWriter(accept.getOutputStream())
            ){
            System.out.println("Client:"+in.readLine());;
            //向客户端发送消息
            String s = sin.readLine();
            out.println(s);
            out.flush();
        }
    }
}
