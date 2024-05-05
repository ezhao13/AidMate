package com.nathanespejo.highschoolhelp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.core.GroupsRequest;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.Group;
import com.nathanespejo.highschoolhelp.adapters.GroupsAdapter;

import java.util.List;

public class GroupListActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, GroupListActivity.class);
        context.startActivity(starter);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list_actiity);

        getGroupList();
    }

    private void getGroupList() {
        GroupsRequest groupsRequest = new GroupsRequest.GroupsRequestBuilder()
                .build();
        groupsRequest.fetchNext(new CometChat.CallbackListener<List<Group>>() {
            @Override
            public void onSuccess(List<Group> list) {
                Log.d("BBB", "Groups list fetched successfully: " + list.size());
                updateUI(list);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("BBB", "Groups list fetching failed with exception: " + e.getMessage());
            }
        });
    }

    private void updateUI(List<Group> list) {
        RecyclerView groupsRecyclerView = findViewById(R.id.groupsRecyclerView);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupsAdapter groupsAdapter = new GroupsAdapter(list, this);
        groupsRecyclerView.setAdapter(groupsAdapter);
    }
}