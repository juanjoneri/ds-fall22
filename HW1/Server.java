import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Listens forever for incoming client request for connection.
 * Starts a new RequestHandlelr thread for each new client.
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
        System.out.println("Starting receiver thread.");
        while (true) {
            try {
                String message = readUdp();
                if (message.startsWith("connect")) {
                    System.out.println("Starting request handler.");
                    String[] tokens = message.trim().split(" ");
                    int tcpPort = Integer.parseInt(tokens[1]);
                    int udpPort = Integer.parseInt(tokens[2]);
                    RequestHandler requestHandler = new RequestHandler(tcpPort, udpPort, commandHandler);
                    requestHandler.start();
                }
            } catch (Exception e) {
                System.out.println("Something whent wrong.\n");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private String readUdp() throws Exception {
        System.out.println(String.format("Waiting for clients to join on %d", port));

        byte[] inBuffer = new byte[Constants.BUFFER_LENGTH];
        DatagramPacket dataPacket = new DatagramPacket(inBuffer, Constants.BUFFER_LENGTH);
        DatagramSocket dataSocket = new DatagramSocket(port);
        dataSocket.receive(dataPacket);

        String response = new String(dataPacket.getData());

        String returnMessage = "OK";
        DatagramPacket returnPacket = new DatagramPacket(returnMessage.getBytes(),
                returnMessage.length(),
                dataPacket.getAddress(),
                dataPacket.getPort());
        dataSocket.send(returnPacket);

        dataSocket.close();

        System.out.println("Read udp " + response);

        return response;
    }

}
