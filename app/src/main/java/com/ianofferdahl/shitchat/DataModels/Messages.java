package com.ianofferdahl.shitchat.DataModels;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

/**
 * Created by Isaac on 5/8/2015.
 */
@JsonNaming(value = PropertyNamingStrategy.PascalCaseStrategy.class)
public class Messages {

    private List<String> MessageList;

    public Messages() {}

    public Messages(List<String> messageList) {
        MessageList = messageList;
    }

    public List<String> getMessageList() {
        return MessageList;
    }

    public void setMessageList(List<String> messageList) {
        MessageList = messageList;
    }
}
