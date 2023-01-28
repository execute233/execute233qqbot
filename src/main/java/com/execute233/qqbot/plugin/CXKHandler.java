package com.execute233.qqbot.plugin;

import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.message.Message;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.message.support.ImageMessage;
import com.zhuangxv.bot.message.support.RecordMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * @author execute233
 **/
@Data
@Component
@Slf4j
public class CXKHandler implements IHandler {
    @Value("${plugin.cxk.keywords}")
    private List<String> keyWords;
    @Value("${plugin.cxk.dir}")
    private String dir;
    /**
     * 一堆kun图对象
     * **/
    private File[] cxkImages = null;
    private File[] cxkRecords = null;

    private static final Random random = new Random();

    @Override
    public void groupHandler(Group group, Member member, MessageChain chain, String msg, int msgID) {
        if (hasKeyword(msg.replace(" ", ""))) {
            group.sendMessage(getRandomCxkImageMessage());
            group.sendMessage(getRandomCxkRecordMessage());
        }
    }

    @Override
    public void friendHandler(Friend friend, MessageChain chain, String msg, int msgId) {
        if (hasKeyword(msg.replace(" ", ""))) {
            friend.sendMessage(getRandomCxkImageMessage());
            friend.sendMessage(getRandomCxkRecordMessage());
        }
    }

    /**
     * 判断传入的字符串是否放关键词.
     * @param s 要判断的字符串.
     * @return 是否包含keywords集合中的任意一个元素.
     * **/
    public boolean hasKeyword(String s) {
        for (String keyword: keyWords) {
            if (s.toLowerCase().contains(keyword)) {
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
        if (cxkImages == null) {
            cxkImages = new File(dir).listFiles(file -> !(file.getName().endsWith(".mp3") || file.getName().endsWith(".aac")));
        }
        assert cxkImages != null;
        File cxk = cxkImages[random.nextInt(cxkImages.length)];
        result.setFile(cxk.getPath());
        result.setUrl("file:///" + cxk.getAbsolutePath().replace('\\', '/'));
        return result;
    }
    public RecordMessage getRandomCxkRecordMessage() {
        RecordMessage result = new RecordMessage();
        if (cxkRecords == null) {
            cxkRecords = new File(dir).listFiles( file -> file.getName().endsWith(".mp3") || file.getName().endsWith(".aac"));
        }
        assert cxkRecords != null;
        File cxk = cxkRecords[random.nextInt(cxkRecords.length)];
        result.setFile("file:///" + cxk.getAbsolutePath().replace('\\', '/'));
        return result;
    }
}
