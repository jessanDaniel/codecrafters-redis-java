import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RedisServer implements Runnable {
    private final Socket clientSocket;

    public RedisServer(Socket clienSocket) {
        this.clientSocket = clienSocket;
    }

    @Override
    public void run() {
        try {
            String response = "+PONG\r\n";

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line = reader.readLine();

            while (line != null) {
                if (line.equalsIgnoreCase("ping")) {
                    clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
                }
                line = reader.readLine();
            }
        } catch (Exception e) {

            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
}
