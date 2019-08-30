package net.thumbtack.jenkins_slackbot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageService {

    public String createMessage(Map<String, String> resource) throws JsonProcessingException {
        String text = Joiner.on("\n").withKeyValueSeparator(" - ").join(resource);
        Map<String, String> message = new HashMap<>();
        message.put("text", text);
        ObjectMapper mapperObj = new ObjectMapper();
        return mapperObj.writeValueAsString(message);
    }
}
