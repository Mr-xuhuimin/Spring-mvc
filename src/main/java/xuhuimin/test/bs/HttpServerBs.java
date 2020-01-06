package xuhuimin.test.bs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServerBs  extends Thread{
    private Socket socket;

    public HttpServerBs(Socket socket) {
        this.socket = socket;
    }
    public static Map<String,String> map=new HashMap<>();

    static {
        map.put("/index","welcome");
        map.put("/hello","中文");

    }
    @Override
    public void run() {
        try (BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        ){
            StringBuilder stringBuilder = new StringBuilder();
            String request=null;
            String line=null;
            while ((line=in.readLine())!=null&&!line.equals("")) {
                stringBuilder.append(line).append("<br>");
                if (request == null) {
                    request = line;
                }

            }   String requesthead = stringBuilder.toString();
                System.out.println(requesthead);
                out.println("HTTP/1.1 200 ok");
                out.println("Content-Type:text/html;charset=utf-8");
                out.println();
                out.println("<html><head><title>requesthead</title></head><body>");
               if (request!=null){
                   String url = request.split(" ")[1];
                   String s = map.get(url);
                   out.println(s!=null ? s:"404");
               }
                out.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("客户端断开"+this.getName());
        }

    }
}
