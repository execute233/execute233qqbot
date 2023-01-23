package com.execute233.qqbot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限管理类
 * @author execute233
 **/
@Service
@Data
public class AccessManager {
    /**
     * 主人QQ
     * **/
    @Value("${bot.owner}")
    private long owner;
    /**
     * 管理员QQ
     * **/
    @Value("${bot.op}")
    private List<Long> op;

    /**
     * 该QQ是否为主人账号
     * **/
    public static boolean isOwner(long qq) {
        return Application.APP.getBean(AccessManager.class).getOwner() == qq;
    }
    /**
     * 该QQ是否为op账号
     * **/
    public static boolean isOp(long qq) {
        return Application.APP.getBean(AccessManager.class).getOp().contains(qq);
    }

}
