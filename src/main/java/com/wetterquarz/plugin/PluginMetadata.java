package com.wetterquarz.plugin;

import java.util.HashMap;
import java.util.Map;

public class PluginMetadata {

    private Plugin plugin;
    private String name;
    private String label;
    private String version;
    private String description;
    private final Map<String, CommandMetadata> commands = new HashMap<>();

    PluginMetadata(){}


    public Plugin getPlugin(){
        return plugin;
    }

    void setPlugin(Plugin plugin){
        this.plugin = plugin;
    }

    public String getName(){
        return name;
    }

    void setName(String name){
        this.name = name;
    }

    public String getLabel(){
        return label;
    }

    void setLabel(String label){
        this.label = label;
    }

    public String getVersion(){
        return version;
    }

    void setVersion(String version){
        this.version = version;
    }

    void addCommand(CommandMetadata commandMetadata){
        commands.put(commandMetadata.getName().toLowerCase(), commandMetadata);
    }

    public Map<String, CommandMetadata> getCommands(){
        return commands;
    }

    void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public static class CommandMetadata {
        private String name;
        private String usage;
        private String description;

        CommandMetadata(){}

        void setName(String name){
            this.name = name;
        }

        void setUsage(String usage){
            this.usage = usage;
        }

        void setDescription(String description){
            this.description = description;
        }

        public String getDescription(){
            return description;
        }

        public String getName(){
            return name;
        }

        public String getUsage(){
            return usage;
        }
    }
}
