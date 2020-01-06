package xuhuimin.test.hard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SendThread extends Thread {
    private Socket socket;
    private String Name;

    public SendThread(Socket socket, String Name) {
        this.socket = socket;
        this.Name = Name;
    }

    @Override
    public void run() {
        try(BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out=new PrintWriter(socket.getOutputStream())) {
            while (true){
                String line = sin.readLine();
                if (line==null||line.equals("bye"))
                    break;
                System.out.println(Name+":"+line);
                out.println(line);
                out.flush();
            }
        } catch (IOException e) {


        }

    }
}
