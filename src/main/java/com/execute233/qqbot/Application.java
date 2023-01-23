package com.execute233.qqbot;

import com.zhuangxv.bot.EnableBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author execute233
 **/
@SpringBootApplication
@SpringBootConfiguration
@EnableBot
@Slf4j
public class Application {
    public static ConfigurableApplicationContext APP;
    public static void main(String[] args) {
        APP = SpringApplication.run(Application.class, args);
    }
}
