import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles TCP requests on the server.
 */
public class TcpServerHandler extends Thread {

    private final CommandHandler commandHandler;
    private final ServerSocket listener;
    private Socket socket;

    public TcpServerHandler(int tcpPort, CommandHandler commandHandler) throws Exception {
        this.commandHandler = commandHandler;
        listener = new ServerSocket(tcpPort);
    }

    @Override
    public void run() {
        System.out.println("Starting TCP handler");
        try {
            System.out.println("Accepted TCP connection.");
        } catch (Exception e) {
            System.out.println("Something whent wrong.");
            e.printStackTrace();
            System.exit(-1);
        }
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
        socket = listener.accept();

        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        PrintWriter writer = new PrintWriter(output, true);

        String inputLine = reader.readLine();
        // System.out.println("TCP input " + inputLine);
        if (inputLine != null) {
            System.out.println("TCP input not null " + inputLine);
            String returnMessage = commandHandler.handle(inputLine.getBytes());
            System.out.println("returnMessage " + returnMessage);
            writer.println(returnMessage);
        }
        socket.close();
    }
}
