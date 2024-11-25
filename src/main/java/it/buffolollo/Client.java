package it.buffolollo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Client {
    private String address;
    private int port;
    private int ID;
    static Socket clientSocket;
    private Map<String, Consumer<String>> eventHandlers;

    public Client(String address, int port) throws IOException {
        this.address = address;
        this.port = port;
        eventHandlers = new ConcurrentHashMap<>();
    }

    /**
     * Establishes a connection to the server.
     * 
     * This method creates a socket connection to the specified server address and
     * port.
     * Upon successful connection, it invokes the {@link #handleClient(Socket)}
     * method
     * to manage communication with the server.
     * 
     * @throws UnknownHostException If the IP address of the host could not be
     *                              determined.
     * @throws IOException          If an I/O error occurs while creating the socket
     *                              or
     *                              connecting to the server.
     */
    public void connect() throws UnknownHostException, IOException {
        clientSocket = new Socket(address, port);

        handleClient(clientSocket);
    }

    /**
     * Registers an event handler for a specific event.
     * 
     * This method allows registering a handler function for a specific event name.
     * When an event with the specified name is received from the server, the
     * corresponding handler function is invoked to process the event data.
     * 
     * @param eventName The name of the event to register the handler for.
     * @param handler   The handler function to be invoked when the specified event
     *                  is received. It accepts a string parameter representing the
     *                  event data.
     */
    public void on(String eventName, Consumer<String> handler) {
        eventHandlers.put(eventName, handler);
    }

    /**
     * Handles the connection event.
     * 
     * This method checks if a handler is registered for the "connected" event.
     * If a handler is registered, it invokes the handler function and passes
     * a message indicating the successful connection to the server.
     */
    private void handleConnect() {
        if (eventHandlers.containsKey("connected")) {
            eventHandlers.get("connected").accept("Connected to " + address + ":" + port);
        }
    }

    /**
     * Handles the disconnection event.
     * 
     * This method checks if a handler is registered for the "disconnected" event.
     * If a handler is registered, it invokes the handler function and passes
     * a message indicating that the server is not available.
     */
    private void handleDisconnect(String s) {
        if (eventHandlers.containsKey("disconnected")) {
            eventHandlers.get("disconnected").accept(s);
        }
    }

    /**
     * Handles the disconnection event.
     * 
     * This method checks if a handler is registered for the "disconnected" event.
     * If a handler is registered, it invokes the handler function and passes
     * a message indicating that the server is not available.
     */
    private void handleDisconnect() {
        if (eventHandlers.containsKey("disconnected")) {
            eventHandlers.get("disconnected").accept("Server is not available!");
        }
    }

    /**
     * Handles communication with the server.
     * 
     * This method manages the communication between the client and the server.
     * It performs the following tasks:
     * 1. Retrieves information from the server.
     * 2. Handles the connection event.
     * 3. Starts a new thread to receive messages/events from the server.
     * 
     * The communication with the server is performed through the provided
     * {@code Socket} object, which represents the client's connection to the
     * server.
     * 
     * This method starts a new thread to handle incoming messages/events from the
     * server.
     * Each received message/event is processed to extract the event name and data,
     * which are then used to invoke the corresponding event handler, if registered.
     * 
     * If an I/O error occurs while reading from the input stream or handling the
     * connection, appropriate error handling mechanisms are invoked.
     * 
     * @param socket The {@code Socket} object representing the client's connection
     *               to the server.
     */
    public void handleClient(Socket socket) {

        boolean status = getInfo();

        if (!status) {
            handleDisconnect("Error getting data from the server!\nConsider checking the provided port!");

            return;
        }

        handleConnect();

        Thread receiver = new Thread(new Runnable() {
            String msg;
            int startIndex = 0;
            int endIndex = 2;

            // EventNameEventData examples
            // 7messageciao a tutti
            // 5hellociao prova a tutti
            // 11userconnect545465445
            @Override
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    msg = in.readLine();

                    while (msg != null) {
                        int eventNameLength = Integer.parseInt(msg.substring(startIndex, endIndex));
                        String eventName = msg.substring(2, eventNameLength + endIndex);
                        String eventData = msg.substring(eventNameLength + endIndex);

                        if (eventHandlers.containsKey(eventName)) {
                            eventHandlers.get(eventName).accept(eventData);
                        }

                        msg = in.readLine();
                    }

                    System.out.println("Server out of service");

                    handleDisconnect("You have been disconnected from the server!");

                    clientSocket.close();
                } catch (IOException e) {
                    handleDisconnect();

                    e.printStackTrace();
                }
            }
        });

        receiver.start();
    }

    /**
     * Retrieves the client's ID.
     * 
     * This method returns the ID assigned to the client. The client's ID
     * typically represents a unique identifier associated with the client
     * within the context of the server-client communication.
     * 
     * @return The client's ID.
     */
    public int getID() {
        return ID;
    }

    /**
     * Retrieves information from the server.
     * 
     * This method reads data from the input stream of the client socket to receive
     * information from the server. The information typically includes details
     * such as the client's ID. Upon receiving the information, it attempts to parse
     * the received message as an integer and assigns it to the {@code ID} field of
     * the client object.
     * 
     * If the received message cannot be parsed as an integer, an error message is
     * printed indicating an error in the server response.
     * 
     * If an I/O error occurs while reading from the input stream, an error message
     * is printed indicating that the information is not available. Additionally,
     * the
     * stack trace of the exception is printed for debugging purposes.
     * 
     * @throws IOException If an I/O error occurs while reading from the input
     *                     stream.
     */
    private boolean getInfo() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String msg = in.readLine();

            try {
                ID = Integer.parseInt(msg);

                return true;
            } catch (NumberFormatException e) {
                System.out.println("Error in the server response!");

                return false;
            }
        } catch (IOException e) {
            System.out.println("Information not available!");

            e.printStackTrace();

            return false;
        }

    }

    /**
     * Sends an event with the specified event name and data to the connected
     * client.
     * The event data is serialized into a string format using Base64 encoding
     * before being sent.
     * 
     * @param eventName The name of the event to emit.
     * @param eventData The data associated with the event.
     * 
     *                  <p>
     *                  The event data is serialized into a string format using
     *                  Base64 encoding
     *                  before being sent to ensure compatibility and data
     *                  integrity.
     * 
     *                  EventNameEventData Examples:
     *                  - 7messageciao a tutti
     *                  - 5hellociao prova a tutti
     *                  - 11userconnect545465445
     *                  </p>
     * 
     * @throws IOException If an I/O error occurs while sending the event data.
     */
    public void emit(String eventName, String eventData) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String line = "";

            int length = eventName.length();
            if (length > 9) {
                line = length + eventName;
            } else {
                line = "0" + length + eventName;
            }

            line += eventData;

            out.println(line);

            out.flush();
        } catch (IOException e) {
            System.out.println("Error emitting data!");

            e.printStackTrace();
        }
    }

    /**
     * Closes the client socket connection.
     * 
     * This method closes the client socket connection to the server.
     * If an I/O error occurs while closing the socket, an error message is printed
     * indicating the error.
     */
    public void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}