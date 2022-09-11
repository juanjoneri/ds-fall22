import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RequestHandler extends Thread {

    private final int tcpPort;
    private final int udpPort;
    private final CommandHandler commandHandler;

    DatagramSocket dataSocket; // TODO

    private Constants.Protocol protocol = Constants.Protocol.UDP; // TODO: Use TCP

    public RequestHandler(int tcpPort, int udpPort, CommandHandler commandHandler) throws Exception {
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.commandHandler = commandHandler;

        dataSocket = new DatagramSocket(udpPort);
    }

    @Override
    public void run() {
        System.out.println(String.format("Starting request handler on %d %d", tcpPort, udpPort));
        while (true) {
            try {
                if (protocol.equals(Constants.Protocol.UDP)) {
                    handleUdp();
                } else {
                    handleTcp();
                }
            } catch (Exception e) {
                System.out.println("Something whent wrong.\n");
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    private void handleUdp() throws Exception {
        DatagramPacket dataPacket = Constants.receive(dataSocket);
        String returnMessage = commandHandler.handle(dataPacket.getData());
        Constants.send(returnMessage, dataSocket, dataPacket);
    }

    private void handleTcp() throws Exception {
        // TODO: handle receiving TCP commands
    }
}
