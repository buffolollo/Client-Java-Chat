package it.buffolollo;

import java.util.Map;

import com.google.gson.Gson;

public class Utils {
    public static String createJsonMessage(int ID, String message, String username) {
        Gson gson = new Gson();

        Map<String, String> data = Map.of("ID", Integer.toString(ID), "message", message, "username", username);

        return gson.toJson(data);
    }

    public static Data getMessageFromJson(String json) {
        Gson gson = new Gson();

        // mi da strani warning quindi io li faccio smettere
        @SuppressWarnings("unchecked")
        Map<String, String> data = gson.fromJson(json, Map.class);

        return new Data(Integer.parseInt(data.get("ID")), data.get("message"), data.get("username"));
    }
}
