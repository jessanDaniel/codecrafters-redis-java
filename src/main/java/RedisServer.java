import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RedisServer implements Runnable {
    private final Socket clientSocket;

    public RedisServer(Socket clienSocket) {
        this.clientSocket = clienSocket;
    }

    @Override
    public void run() {
        try {
            // String response = "+PONG\r\n";

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // lesson multiple client request....

            // String line = reader.readLine();

            // while (line != null) {
            // if (line.equalsIgnoreCase("ping")) {
            // clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
            // }
            // line = reader.readLine();
            // }
            String input;

            while ((input = reader.readLine()) != null) {
                System.out.println("input: " + input);

                if (input.startsWith("*")) {
                    int numArrayElements = Integer.parseInt(input.substring(1, 2));

                    int numElements = numArrayElements * 2;

                    List<String> elements = new ArrayList<>(numElements);

                    for (int i = 0; i < numElements; i++) {
                        elements.add(reader.readLine());
                        System.out.println("element: " + i + " = " + elements.get(i));
                    }

                    switch (elements.get(1).toUpperCase()) {
                        case Command.PING:
                            sendResponse(new Ping().execute("PONG"));
                            break;
                        case Command.ECHO:
                            sendResponse(new Echo().execute(elements.get(3)));
                            break;
                        default:
                            sendResponse("Invalid Response");
                            break;
                    }
                }
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

    private void sendResponse(String response) {
        try {
            clientSocket.getOutputStream().write(response.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }
}
