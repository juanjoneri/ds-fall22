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

    public TcpServerHandler(int tcpPort, CommandHandler commandHandler) throws Exception {
        this.commandHandler = commandHandler;
        listener = new ServerSocket(tcpPort);
    }

    @Override
    public void run() {
        while (true) {
            try {
                handleRequest();
            } catch (Exception e) {
                Constants.handleException(e);
            }
        }
    }

    private void handleRequest() throws Exception {
        Socket socket = listener.accept();

        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        PrintWriter writer = new PrintWriter(output, true);

        String inputLine = reader.readLine();
        if (inputLine != null) {
            String returnMessage = commandHandler.handle(inputLine.getBytes());
            writer.println(returnMessage);
        }
        socket.close();
    }
}
