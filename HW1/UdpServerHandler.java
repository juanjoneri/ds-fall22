import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Handles UDP requests on the server.
 */
public class UdpServerHandler extends Thread {

    private final CommandHandler commandHandler;
    private final DatagramSocket dataSocket;

    public UdpServerHandler(int udpPort, CommandHandler commandHandler) throws Exception {
        this.commandHandler = commandHandler;
        dataSocket = new DatagramSocket(udpPort);

    }

    @Override
    public void run() {
        System.out.println("Starting UDP request handler");
        while (true) {
            try {
                handleRequest();
            } catch (Exception e) {
                System.out.println("Something whent wrong.");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void handleRequest() throws Exception {
        DatagramPacket dataPacket = Constants.receive(dataSocket);
        String returnMessage = commandHandler.handle(dataPacket.getData());
        Constants.send(returnMessage, dataSocket, dataPacket);
    }
}
