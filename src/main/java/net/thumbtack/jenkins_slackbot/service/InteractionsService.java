package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import net.thumbtack.jenkins_slackbot.dao.JenkinsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class InteractionsService {


    private static final Logger log = LoggerFactory.getLogger(InteractionsService.class);
    private JenkinsDao jenkinsDao;
    private Slack slack;


    private final Map<String, Supplier<List<LayoutBlock>>> mapFunctions;

    @Autowired
    public InteractionsService(JenkinsDao jenkinsDao) {
        this.jenkinsDao = jenkinsDao;
        this.slack = Slack.getInstance();

        mapFunctions = new HashMap<>();

        mapFunctions.put("buttonValueAll", this::getFullList);
        mapFunctions.put("buttonValueSubscribes", this::getSubscribes);
    }

    /**
     * @param json json source as string
     * @throws IOException method {@link Slack#send} can throw {@link java.io.IOException}
     */
    public void handleActionBlock(String json) throws IOException {
        Gson gson = GsonFactory.createSnakeCase();
        BlockActionPayload payload = gson.fromJson(json, BlockActionPayload.class);

        BlockActionPayload.Action firstAction = payload
                .getActions()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Action list is empty"));
        List<LayoutBlock> answer = mapFunctions.get(firstAction).get();

        slack.send(payload.getResponseUrl(), Payload.builder().blocks(answer).build());
    }

    private List<LayoutBlock> getFullList() {
        try {
            return buildResponseWithJobList(jenkinsDao.selectJobs().keySet());
        } catch (Exception e) {
            log.info("Exception");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<LayoutBlock> getSubscribes() {
        return null;
    }

    private List<LayoutBlock> buildResponseWithJobList(Set<String> jobs) {
        return jobs.stream().map(jobName ->
                SectionBlock.builder()
                        .text(MarkdownTextObject.builder().text(jobName).build())
                        .accessory(buildButtonWithParams("Subscribe", jobName.concat(":Subscribe"), "primary"))
                        .build())
                .collect(Collectors.toList());
    }

    private ButtonElement buildButtonWithParams(String text, String value, String style) {
        return ButtonElement.builder()
                .text(PlainTextObject.builder().text(text).build())
                .value(value)
                .style(style)
                .build();
    }
}
