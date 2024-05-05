package com.nathanespejo.highschoolhelp;

import static com.nathanespejo.highschoolhelp.ChatGPTAPIExample.chatGPT;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.nathanespejo.highschoolhelp.utils.Constants;

public class MainActivity extends AppCompatActivity {

    public static String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCometChat();

        //init views
        EditText usernameEdittext = findViewById(R.id.userEditText);
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
        CometChat.login(usernameEdittext.getText().toString(), Constants.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("AAA", "Login Successful : " + user.toString());
                redirectToGroupListScreen();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("AAA", "Login failed with exception: " + e.getMessage());
                redirectToGroupListScreen();

            }
        });
        });
    }

    private void redirectToGroupListScreen() {
        GroupListActivity.start(this);
    }

    String region = "us"; // Replace with your App Region ("eu" or "us")

    AppSettings appSettings=new AppSettings.AppSettingsBuilder()
            .subscribePresenceForAllUsers()
            .setRegion(region)
            .autoEstablishSocketConnection(true)
            .build();


    private void initCometChat(){
        CometChat.init(this, Constants.APP_ID, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
            }

            @Override
            public void onError(CometChatException e) {

            }
        });
    }

}