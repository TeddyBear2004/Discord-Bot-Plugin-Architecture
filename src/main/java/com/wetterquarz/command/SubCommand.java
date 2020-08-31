package com.wetterquarz.command;

import javax.annotation.Nullable;
import java.util.List;

public class SubCommand {
    @Nullable
    private final List<SubCommand> subCommands;

    SubCommand(@Nullable List<SubCommand> subCommands){
        this.subCommands = subCommands;
    }
}
