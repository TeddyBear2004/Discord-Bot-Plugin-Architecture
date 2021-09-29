package com.wetterquarz.plugin;

import com.wetterquarz.config.FileConfig;
import org.apache.log4j.Logger;

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
}
