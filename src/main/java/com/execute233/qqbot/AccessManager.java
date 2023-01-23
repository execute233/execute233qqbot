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
     * 获取该QQ的权限类型
     * **/
    public static AccessType getAccess(long qq) {
        AccessManager manager = Application.APP.getBean(AccessManager.class);
        if (manager.getOwner() == qq) {
            return AccessType.ONLY_OWNER;
        }
        if (manager.getOp().contains(qq)) {
            return AccessType.ONLY_OP;
        }
        return AccessType.EVERYONE;
    }

}
