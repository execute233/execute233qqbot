package com.execute233.qqbot.plugin;

import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.message.MessageChain;

public interface IHandler {
    //群消息处理接口
    void groupHandler(Group group, Member member, MessageChain chain, String msg, int msgID);
    void friendHandler(Friend friend, MessageChain chain, String msg, int msgId);
}
