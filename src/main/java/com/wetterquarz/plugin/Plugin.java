package com.wetterquarz.plugin;

import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import org.apache.log4j.Logger;

import com.wetterquarz.config.FileConfig;

public class Plugin {

    Logger logger;

    public Logger getLogger(){
        if(logger == null)
            throw new NullPointerException();
        return logger;
    }

    FileConfig config;

    public FileConfig getConfig(){
        if(config == null)
            throw new NullPointerException();
        return config;
    }

    public void onLoad(){}

    public void onUnload(){}

    public IntentSet getIntents(){
        return IntentSet.none();
    }

}
