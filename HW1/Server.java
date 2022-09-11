import java.net.DatagramPacket;
import java.net.DatagramSocket;

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
                String message = readUdp();
                if (message.startsWith("connect")) {
                    RequestHandler requestHandler = buildRequestHanlder(message);
                    requestHandler.start();
                }
            } catch (Exception e) {
                System.out.println("Something whent wrong.\n");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private RequestHandler buildRequestHanlder(String message) throws Exception {
        String[] tokens = message.trim().split(" ");
        int tcpPort = Integer.parseInt(tokens[1]);
        int udpPort = Integer.parseInt(tokens[2]);
        return new RequestHandler(tcpPort, udpPort, commandHandler);
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
}
