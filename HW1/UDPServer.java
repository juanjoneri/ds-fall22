import java.io.*;
import java.net.*;
import java.util.*;

// TODO: handle request from clients
// Chapter 6 : Distributed Programming

public class UDPServer extends Thread {
    byte[] buf;
    DatagramSocket dataSocket;
    DatagramPacket dataPacket, returnPacket;
    int len;
    CommandHandler mParser;

    public UDPServer(Integer port, CommandHandler parser) {
        mParser = parser;
        len = 1024;
        try {
            dataSocket = new DatagramSocket(port);
            buf = new byte[len];
        } catch (SocketException se) {
            System.err.println(se);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                buf = new byte[len];
                dataPacket = new DatagramPacket(buf, buf.length);
                dataSocket.receive(dataPacket);

                UDPRequest request = new UDPRequest(dataPacket, this, mParser);
                request.start();

            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    public boolean send(DatagramPacket packetToSend) 
    {
        try
        {
            dataSocket.send(packetToSend);
        }
        catch (IOException e) 
        {
            System.err.println(e);
            return false;
        }
        return true;
    }
}
