import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class RedisServer implements Runnable {
    private final Socket clientSocket;

    public static HashMap<String, String> keyValueMap = new HashMap<>();

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
                        case Commands.PING:
                            sendResponse(new Ping().execute("PONG"));
                            break;
                        case Commands.ECHO:
                            sendResponse(new Echo().execute(elements.get(3)));
                            break;

                        case Commands.SET:
                            keyValueMap.put(elements.get(3), elements.get(5));
                            sendResponse(new Set().execute("OK"));
                            break;
                        case Commands.GET:
                            sendResponse(new Get().execute(keyValueMap.get(elements.get(3))));
                            break;
                        default:
                            sendResponse("Invalid Commands");
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
