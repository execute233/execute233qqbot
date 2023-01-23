package com.execute233.qqbot.cxk;

import com.zhuangxv.bot.annotation.FriendMessageHandler;
import com.zhuangxv.bot.annotation.GroupMessageHandler;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.message.support.ImageMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author execute233
 **/
@Service
@Data
@Slf4j
public class CxkHandler {
    @Value("${plugin.cxk.keywords}")
    private List<String> keyWords;
    @Value("${plugin.cxk.image-dir}")
    private String imageDir;
    /**
     * 一堆kun图对象
     * **/
    private File[] cxks = null;

    private static final Random random = new Random();
    @FriendMessageHandler
    public void friendHandler(Friend friend, String msg) {
        // 捕获关键词与允许的QQ
        if (hasKeyword(msg) && CxkAccess.getInstance().getAllowFriends().contains(friend.getUserId())) {
            friend.sendMessage(getRandomCxkImageMessage());
        }
    }
    @GroupMessageHandler
    public void GroupHandler(Group group,String msg) {
        if (keyWords.contains(msg) && CxkAccess.getInstance().getAllowGroups().contains(group.getGroupId())) {
            group.sendMessage(getRandomCxkImageMessage());
        }
    }

    /**
     * 判断传入的字符串是否放关键词.
     * @param s 要判断的字符串.
     * @return 是否包含keywords集合中的任意一个元素.
     * **/
    public boolean hasKeyword(String s) {
        for (String keyword: keyWords) {
            if (s.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    /**
     * @return  返回一个Message对象，里面有一个随机的小黑子表情包的URL路径.
     * **/
    public ImageMessage getRandomCxkImageMessage() {
        ImageMessage result = new ImageMessage();
        if (cxks == null) {
            cxks = new File(imageDir).listFiles();
        }
        assert cxks != null;
        File cxk = cxks[random.nextInt(cxks.length)];
        result.setFile(cxk.getPath());
        result.setUrl("file:///" + cxk.getAbsolutePath().replace('\\', '/'));
        return result;
    }
}
