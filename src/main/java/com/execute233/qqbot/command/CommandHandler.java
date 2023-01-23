package com.execute233.qqbot.command;

import com.execute233.qqbot.AccessManager;
import com.zhuangxv.bot.annotation.FriendMessageHandler;
import com.zhuangxv.bot.annotation.GroupMessageHandler;
import com.zhuangxv.bot.core.Contact;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.core.Member;
import com.zhuangxv.bot.message.MessageChain;
import com.zhuangxv.bot.message.support.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author execute233
 * 指令处理类.<br/>
 * 必须满足一下所有条件可触发命令:<br/>
 * 1.消息由群聊或私聊主人发出.<br/>
 * 2.消息是先@本机器人后内容的消息链，只解析该消息链中的前两个消息(仅群聊).<br/>
 * 3.指令必须以/开头,本机终端执行指令以//开头，如@xxx //
 **/
@Component
@Slf4j
public class CommandHandler {
    /**
     * 没有权限消息
    **/
    public static final TextMessage NO_ACCESS_MSG = new TextMessage("您没有权限!");
    /**
     * 未知指令消息
     * **/
    public static final TextMessage UNKNOWN_COMMAND_MSG = new TextMessage("未知的命令");


    @GroupMessageHandler
    public void GroupCommandHandler(Group group, Member member, MessageChain chain,int msgId) {
        try {
            //这里验证消息链长度就知道是不是指令了
            if (chain.size() != 2) {
                return;
            }
            if (chain.get(1) instanceof TextMessage) {
                String msg = ((TextMessage) chain.get(1)).getText().trim();
                //匹配条件
                if (msg.startsWith("//") && AccessManager.isOwner(member.getUserId())) {
                    if (!AccessManager.isOp(member.getUserId())) {
                        sendNoAccessMsg(group, msgId);
                        return;
                    }
                    localCommandHandler(group, msg, msgId);
                } else if (msg.startsWith("/")) {
                    pluginCommandHandler(group, msg.trim(), msgId);
                }
            }
        } catch (Exception e) {
            log.warn("{}", e);
            sendUnknownCommandMsg(group, msgId);
        }
    }
    @FriendMessageHandler
    public void FriendMessageHandler(Friend friend,String msg,int msgId) {
        try {
            if (msg.startsWith("//") && AccessManager.isOwner(friend.getUserId())) {
                localCommandHandler(friend, msg, msgId);
            } else if (msg.startsWith("/") && AccessManager.isOp(friend.getUserId())) {
                pluginCommandHandler(friend, msg, msgId);
            }
        } catch (Exception e) {
            log.warn("{}", e);
            sendUnknownCommandMsg(friend, msgId);
        }

    }
    /**
     * 查询指令处理
     * **/
    public void pluginCommandHandler(Contact contact, String command, int msgId) {

    }
    /**
     * 本地exec指令处理,使用commons-exec异步处理，执行powershell指令
    **/
    public void localCommandHandler(Contact contact, String command, int msgId) {
        command = command.substring(2);
        ByteArrayOutputStream susStream = new ByteArrayOutputStream();//正常结果流
        ByteArrayOutputStream errStream = new ByteArrayOutputStream();//异常结果流
        CommandLine commandLine = CommandLine.parse("powershell -Command \"" + command + "\"");
        DefaultExecutor exec = new DefaultExecutor();
        PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
        exec.setStreamHandler(streamHandler);
        ExecuteResultHandler erh = new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                try {
                    String result = IOUtils.toString(susStream.toInputStream(), "GBK").trim();
                    contact.sendMessage(new TextMessage(result));
                } catch (IOException e) {
                    log.error("{}", e);
                    contact.sendMessage(new TextMessage("获取流错误，详情请看后台日志"));
                }
            }
            @Override
            public void onProcessFailed(ExecuteException e) {
                try {
                    String result = IOUtils.toString(errStream.toInputStream(), "GBK").trim();
                    contact.sendMessage(new TextMessage(result));
                } catch (IOException ex) {
                    log.error("{}", ex);
                    contact.sendMessage(new TextMessage("获取错误流错误，详情请看后台日志"));
                }
            }
        };
        try {
            exec.execute(commandLine, erh);
        } catch (IOException e) {
            log.error("{}", e);
            contact.sendMessage(new TextMessage("虚拟机与系统操作异常，详情请查看后台日志"));
        }
    }

    public static void sendUnknownCommandMsg(Contact contact, int msgId) {
        MessageChain errorMsg = new MessageChain();
        errorMsg.add(UNKNOWN_COMMAND_MSG);
        errorMsg.reply(msgId);
        contact.sendMessage(errorMsg);
    }
    public static void sendNoAccessMsg(Contact contact, int msgId) {
        MessageChain noAccessMsg = new MessageChain();
        noAccessMsg.add(NO_ACCESS_MSG);
        noAccessMsg.reply(msgId);
        contact.sendMessage(noAccessMsg);
    }

}
