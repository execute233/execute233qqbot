package com.execute233.qqbot.cxk;

import com.execute233.qqbot.Application;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author execute233
 **/
@Component
@Data
public class CxkAccess {
    /**
     * 启用该模块的私聊用户
     * **/
    @Value("${plugin.cxk.allow-friends}")
    private List<Long> allowFriends;
    /**
     * 启用该模块的群
     * **/
    @Value("${plugin.cxk.allow-groups}")
    private List<Long> allowGroups;

    public static CxkAccess getInstance() {
        return Application.APP.getBean(CxkAccess.class);
    }
}
