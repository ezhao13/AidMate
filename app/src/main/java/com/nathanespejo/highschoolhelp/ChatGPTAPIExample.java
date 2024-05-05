package com.nathanespejo.highschoolhelp;

import android.os.AsyncTask;
import android.util.Log;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGPTAPIExample {

    // Define an interface to handle the response
    public interface ChatGPTResponseListener {
        void onChatGPTResponse(String response);
    }

    public static void chatGPT(String prompt, ChatGPTResponseListener listener) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                return chatGPT(strings[0]);
            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                listener.onChatGPTResponse(response);
            }
        }.execute(prompt);
    }

    private static String chatGPT(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "";
        String model = "gpt-3.5-turbo";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());

        } catch (IOException e) {
            Log.e("ChatGPTAPIExample", "Error: " + e.getMessage());
            return null;
        }
    }

    private static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}

