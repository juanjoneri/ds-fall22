import java.net.*;
import java.io.*;
import java.util.*;

public class testTCPClient {

    public static void main(String[] args)
    {
        try
        {
            Socket socket = new Socket("127.0.0.1", 1111);
            OutputStream output = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(output);
            dataOutputStream.writeUTF("HELLO WORLD");
            dataOutputStream.flush();
            dataOutputStream.close();
            socket.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }
}
