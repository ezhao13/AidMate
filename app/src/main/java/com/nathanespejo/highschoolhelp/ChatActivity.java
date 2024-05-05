package com.nathanespejo.highschoolhelp;

import static com.nathanespejo.highschoolhelp.ChatGPTAPIExample.chatGPT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.MessagesRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.nathanespejo.highschoolhelp.adapters.GroupsAdapter;
import com.nathanespejo.highschoolhelp.models.MessageWrapper;
import com.nathanespejo.highschoolhelp.utils.Constants;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private String groupId;
    private MessagesListAdapter<IMessage> adapter;
    public static String aiResponse;

    public static void start(Context context, String groupId) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra(Constants.GROUP_ID, groupId);
        context.startActivity(starter);
    }

    public void respond(String msg){
        TextView responseText = findViewById(R.id.response);
        responseText.setText("ChatGPT: The best place for you to find an answer is in the thread called: " + msg);    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra(Constants.GROUP_ID);
        }
        
        initViews();
        addListener();

        fetchPreviousMessages();
    }

    private void fetchPreviousMessages() {
        MessagesRequest messagesRequest = new MessagesRequest.MessagesRequestBuilder()
                .setGUID(groupId)
                .build();
        messagesRequest.fetchPrevious(new CometChat.CallbackListener<List<BaseMessage>>() {
            @Override
            public void onSuccess(List<BaseMessage> baseMessages) {
                addMessages(baseMessages);
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

    private void addMessages(List<BaseMessage> baseMessages) {
        List<IMessage> list = new ArrayList<>();
        for (BaseMessage message: baseMessages) {
            if (message instanceof TextMessage) {
                list.add(new MessageWrapper((TextMessage) message));
            }
        }
        adapter.addToEnd(list, true);
    }

    private void addListener() {
        String listenerID = "listener 1";
        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                addMessage(textMessage);
            }

            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
            }

            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
            }
        });
    }

    private void initViews() {
        MessageInput inputView = findViewById(R.id.input);
        MessagesList messagesList = findViewById(R.id.messagesList);
        inputView.setInputListener(input -> {
            sendMessage(input.toString());
            return true;
        });
        String senderId = CometChat.getLoggedInUser().getUid();
        ImageLoader imageLoader = (imageView, url, payload) -> Picasso.get().load(url).into(imageView);
        adapter = new MessagesListAdapter<>(senderId, imageLoader);
        messagesList.setAdapter(adapter);
    }

    private void sendMessage(String message) {
        getResponse(message);
        /*TextMessage textMessage = new TextMessage(groupId, message,  CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.d("BBB", "Message sent successfully: " + textMessage.toString());
                addMessage(textMessage);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("BBB", "Message sending failed with exception: " + e.getMessage());
            }
        });

        respond();*/
    }

    private void addMessage(TextMessage textMessage) {
        adapter.addToStart(new MessageWrapper(textMessage), true);
    }

    static String jsonString;

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

    public void getResponse(String userPrompt){
        fetchData();
        String prompt = "Based on the data provided, determine the group with the name most relevant to the user’s prompt. Respond ONLY with the name of the group. If there is no relevant group, then you can bypass this rule and and instead respond telling the user to create a thread to get an answer to their question. The user's prompt is: "
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
                respond(response);
            }
        });
    }
}