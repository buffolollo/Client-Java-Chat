package it.buffolollo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The MessageDB class provides functionality for managing a database of encoded
 * messages.
 * Messages can be saved to the database and retrieved from it.
 */
public class MessageDB {
    private String dbPath;
    private ArrayList<Data> messages = new ArrayList<>();

    /**
     * Constructs a new MessageDB object with the specified database path.
     * Initializes the database by creating the file if it does not exist,
     * and loads existing messages from the file.
     *
     * @param dbPath The path to the message database file.
     */
    public MessageDB(String dbPath) {
        this.dbPath = dbPath;
        checkFile();
        loadMessages();
    }

    /**
     * Checks if the database file exists, and creates it if it does not.
     */
    private void checkFile() {
        try {
            File file = new File(dbPath);

            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves an encoded message to the database file.
     * Also decodes the message and adds it to the ArrayList of messages.
     *
     * @param encodedMsg The encoded message to be saved.
     */
    public void saveMessage(String encodedMsg) {
        try {
            FileWriter writer = new FileWriter(dbPath, true);
            PrintWriter output = new PrintWriter(writer);

            output.println(encodedMsg);

            Data data = Utils.getMessageFromJson(encodedMsg);
            messages.add(data);

            output.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the list of messages stored in the database.
     *
     * @return An ArrayList containing the decoded messages.
     */
    public ArrayList<Data> getMessages() {
        return messages;
    }

    /**
     * Loads messages from the database file.
     * Decodes each message and adds it to the ArrayList of messages.
     */
    private void loadMessages() {
        try {
            FileReader reader = new FileReader(dbPath);
            Scanner sc = new Scanner(reader);

            ArrayList<Data> tempMessages = new ArrayList<>(); // Temporary list to store all messages

            while (sc.hasNextLine()) {
                Data data = Utils.getMessageFromJson(sc.nextLine());

                if (data != null) {
                    tempMessages.add(data);
                } else {
                    System.out.println("Error decoding message!");
                }
            }

            sc.close();
            reader.close();

            // Determine the number of messages to load
            int numMessagesToLoad = Math.min(tempMessages.size(), 100);

            // Load the last 100 messages or all messages if there are less than 100
            for (int i = tempMessages.size() - numMessagesToLoad; i < tempMessages.size(); i++) {
                messages.add(tempMessages.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load messages from db
    }
}
