package com.wetterquarz.command;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface CommandExecutable {
    void execute(MessageCreateEvent event);
}
