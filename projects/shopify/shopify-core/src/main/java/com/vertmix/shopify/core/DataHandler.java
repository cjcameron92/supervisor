package com.vertmix.shopify.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DataHandler {
    private static final Gson gson = new Gson();

    public static void handleIncomingData(String jsonData) {
        // Parse JSON data with Gson
        JsonObject jsonObject = gson.fromJson(jsonData, JsonObject.class);

        // Extract the player name from the order notes or metadata
        String playerName = jsonObject.getAsJsonObject("customer").get("note").getAsString();

        // Example command to execute, replace with actual command as needed
        String command = "give {player} diamond 1".replace("{player}", playerName);

        // Execute command in game (assuming this method exists in your core plugin)
//        CommandExecutor.executeCommandForPlayer(playerName, command);
    }
}
