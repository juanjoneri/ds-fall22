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
    CommandParser mParser;


    public UDPServer(Integer port, CommandParser parser)
    {
        mParser = parser;
        len = 1024;
        try
        {
            dataSocket = new DatagramSocket(port);
            buf = new byte[len];
        }
        catch (SocketException se)
        {
            System.err.println((se));
        }
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                dataPacket = new DatagramPacket(buf, buf.length);
                dataSocket.receive(dataPacket);
                //String msg = new String(dataPacket.getData());
                //System.out.print(msg);

                String returnMessage = mParser.parseBuffer(dataPacket.getData());

                returnPacket = new DatagramPacket(returnMessage.getBytes(),
                                            returnMessage.length(),
                                            dataPacket.getAddress(),
                                            dataPacket.getPort());
                dataSocket.send(returnPacket);
            }
            catch (IOException e)
            {
                System.err.println(e);
            }
        }
    }

    // todo: make a funcion that can send a message
    public boolean send(byte[] msg)
    {
        try
        {
            dataPacket = new DatagramPacket(buf, buf.length);
            dataSocket.receive(dataPacket);
            //String msg = new String(dataPacket.getData());
            //System.out.print(msg);

            mParser.parseBuffer(dataPacket.getData());

            returnPacket = new DatagramPacket(dataPacket.getData(),
                                        dataPacket.getLength(),
                                        dataPacket.getAddress(),
                                        dataPacket.getPort());
            dataSocket.send(returnPacket);
        }
        catch (IOException e)
        {
            System.err.println(e);
            return false;
        }
        return true;
    }
}

