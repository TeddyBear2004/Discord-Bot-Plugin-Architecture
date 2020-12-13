package com.wetterquarz.permissions;

import discord4j.core.event.domain.*;
import discord4j.core.event.domain.channel.*;
import discord4j.core.event.domain.guild.*;
import discord4j.core.event.domain.lifecycle.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.event.domain.role.RoleEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class DiscordEventPermissions {
    private final String name;
    private final double id;

    DiscordEventPermissions(String name, int id){
        this.name = name;
        this.id = id;
    }

    public static void main(String[] args){
        Map<Double, Class<? extends Event>> map = new HashMap<>();
        Map<Double, Class<? extends Event>> map1 = new HashMap<>();


        map1.put(1d, Event.class);
        map1.put(2d, Event.class);
        map1.put(4d, Event.class);
        map1.put(8d, Event.class);


        map.put(0d, Event.class);
        map.put(1d, InviteCreateEvent.class);
        map.put(2d, InviteDeleteEvent.class);
        map.put(3d, PresenceUpdateEvent.class);
        map.put(4d, UserUpdateEvent.class);
        map.put(5d, VoiceServerUpdateEvent.class);
        map.put(6d, VoiceStateUpdateEvent.class);
        map.put(7d, WebhooksUpdateEvent.class);

        map.put(8d, ChannelEvent.class);
        map.put(9d, CategoryCreateEvent.class);
        map.put(10d, CategoryDeleteEvent.class);
        map.put(11d, NewsChannelCreateEvent.class);
        map.put(12d, NewsChannelDeleteEvent.class);
        map.put(13d, NewsChannelUpdateEvent.class);
        map.put(14d, PinsUpdateEvent.class);
        map.put(15d, PrivateChannelCreateEvent.class);
        map.put(16d, PrivateChannelDeleteEvent.class);
        map.put(17d, StoreChannelCreateEvent.class);
        map.put(18d, StoreChannelDeleteEvent.class);
        map.put(19d, StoreChannelUpdateEvent.class);
        map.put(20d, TextChannelCreateEvent.class);
        map.put(21d, TextChannelDeleteEvent.class);
        map.put(22d, TextChannelUpdateEvent.class);
        map.put(23d, TypingStartEvent.class);
        map.put(24d, VoiceChannelCreateEvent.class);
        map.put(25d, VoiceChannelDeleteEvent.class);
        map.put(26d, VoiceChannelUpdateEvent.class);

        map.put(27d, GuildEvent.class);
        map.put(28d, BanEvent.class);
        map.put(29d, EmojisUpdateEvent.class);
        map.put(30d, GuildCreateEvent.class);
        map.put(31d, GuildDeleteEvent.class);
        map.put(32d, GuildUpdateEvent.class);
        map.put(33d, IntegrationsUpdateEvent.class);
        map.put(34d, MemberChunkEvent.class);
        map.put(35d, MemberJoinEvent.class);
        map.put(36d, MemberLeaveEvent.class);
        map.put(37d, MemberUpdateEvent.class);
        map.put(38d, UnbanEvent.class);

        map.put(39d, GatewayLifecycleEvent.class);
        map.put(40d, ConnectEvent.class);
        map.put(41d, DisconnectEvent.class);
        map.put(42d, ReadyEvent.class);
        map.put(43d, ReconnectEvent.class);
        map.put(44d, ReconnectFailEvent.class);
        map.put(45d, ReconnectStartEvent.class);
        map.put(46d, ResumeEvent.class);

        map.put(47d, MessageEvent.class);
        map.put(48d, RoleEvent.class);
        map.put(49d, MessageBulkDeleteEvent.class);
        map.put(50d, MessageCreateEvent.class);
        map.put(51d, MessageDeleteEvent.class);
        map.put(52d, MessageUpdateEvent.class);
        map.put(53d, ReactionAddEvent.class);
        map.put(54d, ReactionRemoveAllEvent.class);
        map.put(55d, ReactionRemoveEmojiEvent.class);
        map.put(56d, ReactionRemoveEvent.class);

        BigDecimal d = new BigDecimal(0);

        for(int i = 0; i < 57; i++){
            d = d.add(BigDecimal.valueOf(Math.pow(2, i)));
            System.out.println(Math.pow(2, i));
        }
        System.out.println(d);

        PermissionManager<Class<? extends Event>> permissionManager = new PermissionManager<>(map, d.doubleValue());
        System.out.println(permissionManager.isTrue(0));
    }

    @Override
    public String toString(){
        return  "map.put(" + id + ", \"" + name + "\");";
    }
}
