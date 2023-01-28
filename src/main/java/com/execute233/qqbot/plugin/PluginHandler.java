package com.execute233.qqbot.plugin;

import com.execute233.qqbot.Application;
import com.zhuangxv.bot.annotation.FriendMessageHandler;
import com.zhuangxv.bot.annotation.GroupMessageHandler;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.message.MessageChain;
import org.springframework.stereotype.Component;

/**
 * @author execute233
 **/
@Component
public class PluginHandler implements IHandler {

    @Override
    @GroupMessageHandler
    public void groupHandler(Group group, Member member, MessageChain chain, String msg, int msgID) {
        if (!PluginAccess.isAllowGroup(group)) {return;}
        Application.APP.getBean(CXKHandler.class).groupHandler(group, member, chain, msg, msgID);
        Application.APP.getBean(KFCHandler.class).groupHandler(group, member, chain, msg, msgID);
    }

    @Override
    @FriendMessageHandler
    public void friendHandler(Friend friend, MessageChain chain, String msg, int msgId) {
        if (!PluginAccess.isAllowFriend(friend)) {return;}
        Application.APP.getBean(CXKHandler.class).friendHandler(friend, chain, msg, msgId);
        Application.APP.getBean(KFCHandler.class).friendHandler(friend, chain, msg, msgId);
    }
}
