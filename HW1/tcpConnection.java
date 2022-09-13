import java.net.*;
import java.io.*;
import java.util.*;


public class tcpConnection extends Thread
{
    private final Socket mSocket;

    public tcpConnection(Socket socket)
    {
        this.mSocket = socket;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("tcpConnection Started" + this.mSocket.toString());
            InputStream input = mSocket.getInputStream();
            BufferedReader readerBuf = new BufferedReader(new InputStreamReader(input));
            String line = readerBuf.readLine();
            System.out.println(line);
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
    }
}