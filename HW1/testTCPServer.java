import java.net.*;
import java.io.*;
import java.util.*;

public class testTCPServer {

    public static void main(String[] args)
    {
        try
        {
            ServerSocket listener = new ServerSocket(1111);
            Socket s;
            while((s=listener.accept())!=null)
            {
                tcpConnection connection = new tcpConnection(s);
                connection.start();
            }
            listener.close();
        }
        catch (IOException e)
        {
            System.err.println(e);
        }
    }
}
