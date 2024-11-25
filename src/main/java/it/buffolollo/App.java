package it.buffolollo;

import java.io.IOException;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.gson.Gson;

public class App {
    private static Client client;
    private static Gui gui;
    private static MessageDB messageDB;
    private static String username;

    public static void sendMessage(String msg) {
        String json = Utils.createJsonMessage(client.getID(), msg, username);

        if (json != null) {
            client.emit("message", json);
        } else {
            gui.aggiungiMsg("Error encoding message!");
        }
    }

    private static void handleConnectionError(IOException e) {
        System.out.println("Common Error, maybe server not available!");

        gui.makeErrorGui("Common Error, maybe server not available!");
        if (e instanceof ConnectException) {
            gui.makeErrorGui("Server is not available!");

            System.out.println("Server not available!");
        }
    }

    public static void login(String username, String password) {
        if (username.isEmpty()) {
            gui.makeErrorGui("Username is empty!");

            return;
        }

        if (password.isEmpty()) {
            gui.makeErrorGui("Password is empty!");

            return;
        }

        String hashPassword = null;

        try {
            hashPassword = Encryption.sha256(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Map<String, String> data = Map.of("username", username.toLowerCase(), "password", hashPassword);

        Gson gson = new Gson();

        String json = gson.toJson(data);

        client.emit("login", json);

        App.username = username;
    }

    public static void register(String username, String password, String cpassword) {
        if (username.isEmpty()) {
            gui.makeErrorGui("Username is empty!");

            return;
        }

        if (password.isEmpty()) {
            gui.makeErrorGui("Password is empty!");

            return;
        }

        if (cpassword.isEmpty()) {
            gui.makeErrorGui("Password doesen't match!");

            return;
        }

        if (!password.equals(cpassword)) {
            gui.makeErrorGui("Password doesen't match!");

            return;
        }

        String hashPassword = null;

        try {
            hashPassword = Encryption.sha256(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();

        Map<String, String> data = Map.of("username", username.toLowerCase(), "password", hashPassword);

        String json = gson.toJson(data);

        client.emit("register", json);
    }

    public static void connect(String address, int port) {
        try {
            client = new Client(address, port);

            client.on("login_success", (msg) -> {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gui.chatGui(client);
                    }
                });
                // add messages from db
                messageDB.getMessages().forEach((data) -> {
                    if (data != null) {
                        gui.aggiungiMsg(
                                "(" + data.getID() + ")-[" + data.getUsername() + "]" + " message: "
                                        + data.getMessage());
                    } else {
                        gui.aggiungiMsg("Error decoding message!");
                    }
                });

                gui.aggiungiMsg("You are connected with ID: " + client.getID());

                client.on("user_connected", (mssg) -> {
                    gui.aggiungiMsg("User connected: " + mssg);
                });

                client.on("user_disconnected", (id) -> {
                    gui.aggiungiMsg("User disconnected: " + id);
                });
            });

            client.on("login_error", (msg) -> {
                gui.makeErrorGui(msg);
            });

            client.on("register_error", (msg) -> {
                gui.makeErrorGui(msg);
            });

            client.on("register_success", (msg) -> {
                gui.chooseLoginRegister();
            });

            client.on("message", (msg) -> {
                Data data = Utils.getMessageFromJson(msg);

                if (data != null) {
                    gui.aggiungiMsg(
                            "(" + data.getID() + ")-[" + data.getUsername() + "]" + " message: "
                                    + data.getMessage());

                    messageDB.saveMessage(msg);
                } else {
                    gui.aggiungiMsg("Error decoding message!");
                }
            });

            client.on("connected", (msg) -> {
                gui.chooseLoginRegister();
            });

            client.on("disconnected", (msg) -> {
                gui.makeErrorGui(msg);
            });

            client.connect();
        } catch (IOException e) {
            handleConnectionError(e);
        }
    }

    public static void disconnect() {
        client.close();
    }

    public static void main(String[] args) {
        gui = new Gui("Java Chat");

        messageDB = new MessageDB("messages.db");

        gui.showConnectingToServer();

        connect(ConnectionInfo.SERVER_URL, 5005);
    }
}