package it.buffolollo;

public class Data {
    private int ID;
    private String message;
    private String username;

    public Data(int ID, String message, String username) {
        this.ID = ID;
        this.message = message;
        if (username.isEmpty()) {
            this.username = "Anonymous";
        } else {
            this.username = username;
        }
    }

    public Data(int ID, String message) {
        this.ID = ID;
        this.message = message;
        this.username = "Anonymous";
    }

    public int getID() {
        return ID;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }
}
