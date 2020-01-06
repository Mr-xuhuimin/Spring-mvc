package xuhuimin.test.cs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer extends Thread {
  private Socket socket;

    public HttpServer(Socket socket) {
        this.socket = socket;
    }
    private static Map<String,String> map=new HashMap<>();

    static {
        map.put("index","welcome");
        map.put("hello","how are you?");
    }

    @Override
    public void run() {
        try (BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
                ){
                while (true){
                    String line=in.readLine();
                    if (line==null||line.equals("bye")){
                        break;
                    }
                    String s = map.get(line.trim());
                    out.println(s!=null ? s:"404");

                }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("客户端断开"+this.getName());
        }

    }
}
