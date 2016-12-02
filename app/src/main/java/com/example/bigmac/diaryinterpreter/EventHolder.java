package com.example.bigmac.diaryinterpreter;

/**
 * Created by BigMac on 02/12/16.
 */
public class EventHolder {

    String eventID = null;
    int eventType = 0;
    String eventName = null;

    public EventHolder(String eventID, int eventType, String eventName){

        this.eventID = eventID;
        this.eventType = eventType;
        this.eventName = eventName;

    }

    public String getEventID(){

        return eventID;
    }

    public String getEventName(){

        return eventName;
    }

    public int getEventType(){

        return eventType;
    }

}
