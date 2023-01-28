package com.execute233.qqbot.plugin;

import com.execute233.qqbot.Application;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author execute233
 **/
@Component
@Data
public class PluginAccess {
    /**
     * 启用插件的私聊用户。
     * **/
    @Value("${plugin.allow-friends}")
    private List<Long> allowFriends;
    /**
     * 启用该插件的群。
     * **/
    @Value("${plugin.allow-groups}")
    private List<Long> allowGroups;

    public static PluginAccess getInstance() {
        return Application.APP.getBean(PluginAccess.class);
    }
    public static boolean isAllowFriend(long qq) {
        return getInstance().allowFriends.contains(qq);
    }
    public static boolean isAllowGroup(long qq) {
        return getInstance().allowGroups.contains(qq);
    }
    public static boolean isAllowFriend(Friend friend) {
        return isAllowFriend(friend.getUserId());
    }
    public static boolean isAllowGroup(Group group) {
        return isAllowGroup(group.getGroupId());
    }
}
