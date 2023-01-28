package com.execute233.qqbot.plugin;

import com.zhuangxv.bot.core.Contact;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.message.support.TextMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author execute233
 **/
@Component
@Data
@Slf4j
public class KFCHandler implements IHandler {
    private static final Random random = new Random();
    /**
     * 疯狂星期三.
     * **/
    @Value("${plugin.kfc.crazy3file}")
    private String crazy3file;
    /**
     * 疯狂星期四.
     * **/
    @Value("${plugin.kfc.crazy4file}")
    private String crazy4file;
    /**
     * 关键词.
     * **/
    @Value("${plugin.kfc.crazy3keywords}")
    private List<String> crazy3keywords;
    @Value("${plugin.kfc.crazy4keywords}")
    private List<String> crazy4keywords;

    private List<String> crazy3texts = null;
    private List<String> crazy4texts = null;


    @Override
    public void groupHandler(Group group, Member member, MessageChain chain, String msg, int msgID) {
        handler(group, msg);
    }

    @Override
    public void friendHandler(Friend friend, MessageChain chain, String msg, int msgId) {
        handler(friend, msg);
    }

    public void handler(Contact contact, String msg) {
        check();
        //如果是疯狂星期三
        if (hasCrazy3keyword(msg)) {
            contact.sendMessage(getRandomCrazy3message());
        }
        //如果是疯狂星期四
        if (hasCrazy4keyword(msg)) {
            contact.sendMessage(getRandomCrazy4message());
        }
    }

    /**
     * 检查文件是否完整或是初始化.
     * 解析/n为换行符
     * **/
    public void check() {
        if (crazy3texts == null) {
            try {
                crazy3texts = readToListAndFormat(crazy3file);
            } catch (IOException e) {
                log.warn(null, e);
            }
        }
        if (crazy4texts == null) {
            try {
                crazy4texts = readToListAndFormat(crazy4file);
            } catch (IOException e) {
                log.warn(null, e);
            }
        }
    }
    public List<String> readToListAndFormat(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        List<String> result = new ArrayList<>();
        String s;
        while ((s = reader.readLine()) != null) {
            result.add(s.replace("\\n", "\n"));
        }
        return result;
    }

    /**
     * 判断传入的字符串是否含有关键词.
     * @param s 要判断的字符串.
     * @return 是否包含疯狂星期三中的任意一个元素.
     * **/
    public boolean hasCrazy3keyword(String s) {
        for (String keyword: crazy3texts) {
            if (s.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断传入的字符串是否含有关键词.
     * @param s 要判断的字符串.
     * @return 是否包含疯狂星期四中的任意一个元素.
     * **/
    public boolean hasCrazy4keyword(String s) {
        for (String keyword: crazy4keywords) {
            if (s.toLowerCase().contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    public TextMessage getRandomCrazy3message() {
        return new TextMessage(crazy3texts.get(random.nextInt(crazy3texts.size())));
    }
    public TextMessage getRandomCrazy4message() {
        return new TextMessage(crazy4texts.get(random.nextInt(crazy4texts.size())));
    }

}
