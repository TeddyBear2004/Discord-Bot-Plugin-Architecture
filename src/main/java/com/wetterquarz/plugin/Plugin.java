package com.wetterquarz.plugin;

import com.wetterquarz.command.CommandExecutable;
import com.wetterquarz.config.FileConfig;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Plugin {

    @Nullable Logger logger;

    public @Nullable Logger getLogger(){
        if(logger == null)
            throw new NullPointerException();
        return logger;
    }

    @Nullable FileConfig config;

    public @NotNull FileConfig getConfig(){
        if(config == null)
            throw new NullPointerException();
        return config;
    }

    public void onLoad(){}

    public void onUnload(){}
}
