import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Listens forever for incoming client request for connection.
 * Starts a new RequestHandler thread for each new client.
 */
public class Server extends Thread {

    private final int port;
    private final CommandHandler commandHandler;

    public Server(int port, CommandHandler commandHandler) {
        this.port = port;
        this.commandHandler = commandHandler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = readTCP();
                String message = readFromSocket(socket);
                if (message.startsWith("connect")) {
                    RequestHandler requestHandler = buildRequestHanlder(message, socket);
                    requestHandler.start();
                }
            } catch (Exception e) {
                System.out.println("Something whent wrong.\n");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private RequestHandler buildRequestHanlder(String message, Socket socket) throws Exception {
        String[] tokens = message.trim().split(" ");
        int tcpPort = Integer.parseInt(tokens[1]);
        int udpPort = Integer.parseInt(tokens[2]);
        return new RequestHandler(udpPort, commandHandler, socket);
    }

    private String readUdp() throws Exception {
        System.out.println(String.format("Waiting for clients to join on %d", port));

        DatagramSocket socket = new DatagramSocket(port);
        DatagramPacket packet = Constants.receive(socket);
        String message = new String(packet.getData());
        Constants.ack(socket, packet);
        socket.close();
        return message;
    }

    private Socket readTCP()
    {
        Socket socket = null;
        try
        {
            ServerSocket listener = new ServerSocket(port);
            if((socket=listener.accept())!=null)
            {
                System.out.println("tcpConnection Started" + socket.toString());                
            }
            listener.close();
        }
        catch(IOException ioe)
        {
            System.err.println(ioe);
        }
        return socket;
    }

    private String readFromSocket(Socket socket)
    {
        String returnString = "n/a";
        try
        {
            InputStream input = socket.getInputStream();
            BufferedReader readerBuf = new BufferedReader(new InputStreamReader(input));
            returnString = readerBuf.readLine();
            readerBuf.close();
        }
        catch(IOException ioe)
        {
            System.err.println(ioe);
        }
        return returnString;
    }
}
