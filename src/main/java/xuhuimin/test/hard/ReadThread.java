package xuhuimin.test.hard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends  Thread {
    private Socket socket;
    private String fromName;

    public ReadThread(Socket socket, String fromName) {
        this.socket = socket;
        this.fromName = fromName;
    }

    @Override
    public void run() {
        try (BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()))){

            while (true){
                String line = in.readLine();
                if (line==null||line.equals("bye")){
                    break;
                }
                System.out.println(fromName+":"+line);
            }
        } catch (IOException e) {

        }

    }

    //读

}
