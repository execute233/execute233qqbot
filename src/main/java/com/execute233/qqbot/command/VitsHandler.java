package com.execute233.qqbot.command;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.annotation.JSONField;
import com.execute233.qqbot.Application;
import com.execute233.qqbot.HttpUtils;
import com.zhuangxv.bot.core.Contact;
import com.zhuangxv.bot.core.Friend;
import com.zhuangxv.bot.core.Group;
import com.zhuangxv.bot.message.support.RecordMessage;
import com.zhuangxv.bot.message.support.TextMessage;
import edu.sysu.pmglab.commandParser.CommandItem;
import edu.sysu.pmglab.commandParser.CommandOptions;
import edu.sysu.pmglab.commandParser.CommandParser;
import edu.sysu.pmglab.commandParser.types.DOUBLE;
import edu.sysu.pmglab.commandParser.types.STRING;
import io.netty.handler.codec.http.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author execute233
 **/
@Slf4j
@Component
public class VitsHandler {

    private static final CommandParser parser = new CommandParser("vits");
    public VitsHandler() {
        parser.offset(2);
        CommandItem characterItem = parser.register(STRING.VALUE, "-c", "-character")
                .defaultTo("派蒙");
        CommandItem noiseItem = parser.register(DOUBLE.VALUE, "-n").defaultTo(0.667);
        CommandItem noiseWItem = parser.register(DOUBLE.VALUE, "-nw").defaultTo(0.8);
        CommandItem lengthItem = parser.register(DOUBLE.VALUE, "-l").defaultTo(1.2);
    }

    public static final Map<Long, Integer> mapping = new HashMap<>();

    private String id;

    @Value("${plugin.vits.api-url}")
    private String apiUrl;
    @Value("${plugin.vits.allow-group}")
    private List<Long> allowGroup;
    @Value("${plugin.vits.allow-friend}")
    private List<Long> allowFriend;
    @Value("${plugin.vits.temp-path}")
    private String tempPath;
    public void handler(Contact contact, int msgId, String command) {
        if (contact instanceof Friend) {
            if (!allowFriend.contains(((Friend) contact).getUserId())) {
                contact.sendMessage(new TextMessage("您没有权限"));
                return;
            }
        }
        if (contact instanceof Group) {
            if (!allowGroup.contains(((Group) contact).getGroupId())) {
                return;
            }
        }
        command = command.toLowerCase();
        String[] commands = command.split(" ");
        CommandOptions options = parser.parse(commands);
        if (options.isHelp() || commands[1].equals("-h") || commands[1].equals("-help")) {
            contact.sendMessage(new TextMessage(
                    "用法：\n/vits <文本>\n其他参数：\n-c 指定说话人\n-n 控制感情等变化程度,默认为0.667[0.4~1]\n" +
                            "-nw 控制音素发音长度变化程度。默认为0.8[0.5~1]\n-l 控制整体语速。默认为1.2[0.6~1.5]\n" +
                            "输入/vits -hc查询可选择的说话人"
            ));
            return;
        }
        if (commands[1].equals("-hc")) {
            contact.sendMessage(new TextMessage("支持的说话人:\n" + "['派蒙', '凯亚', '安柏', '丽莎', '琴', '香菱', '枫原万叶',\n" +
                    "'迪卢克', '温迪', '可莉', '早柚', '托马', '芭芭拉', '优菈'," +
                    "'云堇', '钟离', '魈', '凝光', '雷电将军', '北斗'," +
                    "'甘雨', '七七', '刻晴', '神里绫华', '戴因斯雷布', '雷泽'," +
                    "'神里绫人', '罗莎莉亚', '阿贝多', '八重神子', '宵宫'," +
                    "'荒泷一斗', '九条裟罗', '夜兰', '珊瑚宫心海', '五郎'," +
                    "'散兵', '女士', '达达利亚', '莫娜', '班尼特', '申鹤'," +
                    "'行秋', '烟绯', '久岐忍', '辛焱', '砂糖', '胡桃', '重云'," +
                    "'菲谢尔', '诺艾尔', '迪奥娜', '鹿野院平藏']"));
            return;
        }
        //是否超出长度限制
        if (commands[1].length() > 50) {
            contact.sendMessage(new TextMessage("超出最大字符长度限制"));
            return;
        }
        try {
            VitsCreateData data = new VitsCreateData();
            data.setText(commands[1]);
            data.setCharacter((String) options.get("-c"));
            data.setNoise((double) options.get("-n"));
            data.setNoiseW((double) options.get("-nw"));
            data.setLength((double) options.get("-l"));
            JSONObject jsonObject = JSONObject.parse(create(data));
            if (jsonObject.containsKey("wav_id")) {
                id = jsonObject.getString("wav_id");
                new Thread( () -> {
                    for (int i = 0;i <= 7;i++) {
                        //每10s查询语音状态
                        String status = HttpUtils.post(apiUrl + "/status", null, null,
                                "{\"wav_id\": \""+ id + "\"}", ContentType.APPLICATION_JSON);
                        if (JSONObject.parse(status).getString("text").equalsIgnoreCase("OK")) {
                            //确定生成完成，下载文件至本地
                            HttpGet get = new HttpGet(apiUrl + "/" + id + ".wav");
                            try {
                                InputStream in = HttpUtils.HTTP_CLIENT.execute(get).getEntity().getContent();
                                File outFile = new File(tempPath + id + ".wav");
                                outFile.createNewFile();
                                FileOutputStream out = new FileOutputStream(outFile);
                                IOUtils.copy(in, out);
                                IOUtils.closeQuietly(out);
                                RecordMessage recordMessage = new RecordMessage();
                                recordMessage.setFile("file:///" + tempPath + id + ".wav");
                                Thread.sleep(1000);
                                contact.sendMessage(recordMessage);
                                return;
                            } catch (Exception e) {
                                log.error(null, e);
                            }
                        }
                        try {
                            Thread.sleep(7000);
                        } catch (Exception e) {
                            log.warn("线程休眠错误", e);
                        }
                    }
                }).start();
            } else {
                contact.sendMessage(new TextMessage("有其他任务生成中"));
            }
        } catch (Exception e) {
            log.warn("参数解析错误！", e);
            contact.sendMessage(new TextMessage("参数解析错误，请输入/vits -h获取帮助"));
        }
    }
    private String create(VitsCreateData data) {
        return HttpUtils.post(apiUrl + "/create", null,
                null, JSONObject.toJSONString(data), ContentType.APPLICATION_JSON);
    }

    public static void handlerInit(Contact contact, int msgId, String command) {
        Application.APP.getBean(VitsHandler.class).handler(contact, msgId, command);
    }
}
@Data
class VitsCreateData {
    @JSONField(defaultValue = "派蒙")
    private String character;
    @JSONField(name = "text")
    private String text;
    @JSONField(name = "n", defaultValue = "0.667")
    private Double noise;
    @JSONField(name = "nw", defaultValue = "0.8")
    private Double noiseW;
    @JSONField(name = "length", defaultValue = "1.2")
    private Double length;
}
