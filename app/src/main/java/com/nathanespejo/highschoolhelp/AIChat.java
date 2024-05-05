package com.nathanespejo.highschoolhelp;
import static com.nathanespejo.highschoolhelp.ChatGPTAPIExample.chatGPT;

import android.util.Log;

import com.nathanespejo.highschoolhelp.adapters.GroupsAdapter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AIChat {

    static String jsonString;
    public static GroupsAdapter.GroupViewHolder groupViewHolder;

    public static void fetchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://257166009b85b91f.api-us.cometchat.io/v3/conversations?conversationType=group&tags=&perPage=100&page=1")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("onBehalfOf", "you")
                        .addHeader("apikey", "06841960d8a79020b2e4afd710660f046d81770e")
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        jsonString = response.body().string();
                        // Parse JSON response
                        Log.d("AAA", jsonString);
                    } else {
                        // Handle unsuccessful response
                        Log.e("Network", "Unsuccessful response: " + response.code());
                    }
                } catch (IOException e) {
                    // Handle IO exception
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getResponse(String userPrompt){
        fetchData();
        String prompt = "You are a friendly assistant helping a highschool student. Based on the data provided, determine the group with the name most relevant to the user’s prompt. Then direct the user to the proper group (which you will call thread). If there is no relevant group, tell the user to create their own thread so others can aid them. The user's prompt is: "
                + userPrompt
                + ". The available groups are: Post Graduation, Intern Help, UofT Inquiry, HS Clubs, Chem Eng, Ask Teachers, Homework Help";
                //+ ". The JSON is: " + jsonString;
        chatGPT(prompt, new ChatGPTAPIExample.ChatGPTResponseListener() {
            @Override
            public void onChatGPTResponse(String response) {
                // Handle the response here
                Log.d("ChatGPT Response", response);
                //groupViewHolder.send(response);
                //ChatActivity.respond();
            }
        });
    }
}
