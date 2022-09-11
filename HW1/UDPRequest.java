import java.net.*;

public class UDPRequest extends Thread {
    DatagramPacket mReceivedPacket;
    UDPServer mUdpServer;
    CommandHandler mCommandHandler;

    public UDPRequest(DatagramPacket receivedPacket, UDPServer udpServer, CommandHandler commandHandler)
    {
        mReceivedPacket = receivedPacket;
        mUdpServer = udpServer;
        mCommandHandler = commandHandler;
    }

    @Override
    public void run()
    {
        String returnMessage = mCommandHandler.handle(mReceivedPacket.getData());
        DatagramPacket returnPacket = new DatagramPacket(returnMessage.getBytes(),
        returnMessage.length(),
        mReceivedPacket.getAddress(),
        mReceivedPacket.getPort());
        mUdpServer.send(returnPacket);
    }
}
