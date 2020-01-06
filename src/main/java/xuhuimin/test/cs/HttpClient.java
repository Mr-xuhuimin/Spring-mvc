package xuhuimin.test.cs;

import jdk.net.Sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpClient extends Thread {
private Socket socket;

    public HttpClient(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try(BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream());
            BufferedReader sin=new BufferedReader(new InputStreamReader(System.in))
        ) {
            while (true){
                String line = sin.readLine();
                if (line==null||line.equals("bye")){
                    break;
                }
                out.println(line);
                out.flush();
                String line1 = in.readLine();
                System.out.println(line1);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
