package net.thumbtack.jenkins_slackbot.rest;

import net.thumbtack.jenkins_slackbot.service.MessageService;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class MainController {

    private MessageService messageService;

    @Autowired
    public MainController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(
            value = "/api/slackbot",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void sendStatusToSlack(@RequestBody Map<String, String> map) throws IOException {
        String urlWebHook = "https://hooks.slack.com/services/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";

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(urlWebHook);

        StringEntity entity = new StringEntity(messageService.createMessage(map));
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        client.execute(httpPost);
        client.close();
    }
}
