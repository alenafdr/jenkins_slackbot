package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.webhook.Payload;
import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;
import com.github.seratch.jslack.common.json.GsonFactory;
import com.google.gson.Gson;
import net.thumbtack.jenkins_slackbot.Utils;
import net.thumbtack.jenkins_slackbot.dao.JenkinsDao;
import net.thumbtack.jenkins_slackbot.model.JenkinsJob;
import net.thumbtack.jenkins_slackbot.model.User;
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
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class InteractionsService {

    private static final Logger log = LoggerFactory.getLogger(InteractionsService.class);
    private JenkinsDao jenkinsDao;
    private SubscribeService subscribeService;
    private Slack slack;

    private final Map<String, BiFunction<User, String, List<LayoutBlock>>> mapFunctions;

    @Autowired
    public InteractionsService(JenkinsDao jenkinsDao, SubscribeService subscribeService) {
        this.jenkinsDao = jenkinsDao;
        this.subscribeService = subscribeService;
        this.slack = Slack.getInstance();

        mapFunctions = new HashMap<>();

        mapFunctions.put("buttonValueAll", (user, source) -> getFullList(user));
        mapFunctions.put("buttonValueSubscriptions", (user, source) -> getSubscribes(user));
        mapFunctions.put("buttonValueUpdate", (user, source) -> update(user));
        mapFunctions.put("(.+):Subscribe:(.+)", this::subscribe);
        mapFunctions.put("(.+):Unsubscribe:(.+)", this::unsubscribe);
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
        List<LayoutBlock> answer = apply(firstAction.getValue(), new User(payload.getUser()));
        slack.send(payload.getResponseUrl(), Payload.builder().blocks(answer).build());
    }

    private List<LayoutBlock> apply(String value, User user) {
        for (Map.Entry<String, BiFunction<User, String, List<LayoutBlock>>> functionMap : mapFunctions.entrySet()) {
            if (Pattern.compile(functionMap.getKey()).matcher(value).matches()) {
                return functionMap.getValue().apply(user, value);
            }
        }

        throw new IllegalArgumentException("There isn't handler for " + value);
    }

    private List<LayoutBlock> getFullList(User user) {
        try {
            return buildResponseWithJobListForUser(jenkinsDao.selectJobs().keySet(), user, ":FullList");
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<LayoutBlock> getSubscribes(User user) {
        try {
            return buildResponseWithJobListForUser(
                    subscribeService.getSubscriptionsByUser(user)
                            .stream()
                            .map(JenkinsJob::getName)
                            .collect(Collectors.toSet()),
                    user,":Subscriptions");
        } catch (Exception e) {
            e.printStackTrace();
            return getFullList(user);
        }
    }

    private List<LayoutBlock> update(User user) {
        subscribeService.updateListOfJobs();
        return getFullList(user);
    }

    private List<LayoutBlock> subscribe(User user, String value) {
        String jobName = getJobName(value);
        log.info("job name {}", jobName);
        subscribeService.addSubscribe(user, jobName);
        return lastKnownList(value, user);
    }

    private List<LayoutBlock> unsubscribe(User user, String value) {
        String jobName = getJobName(value);
        log.info("job name {}", jobName);
        subscribeService.removeSubscribe(user, jobName);
        return lastKnownList(value, user);
    }

    private List<LayoutBlock> buildResponseWithJobListForUser(Set<String> jobs, User user, String lastKnownList) {
        Set<String> subscriptions = subscribeService.getSubscriptionsByUser(user)
                .stream()
                .map(JenkinsJob::getName)
                .collect(Collectors.toSet());
        return jobs.stream().map(jobName ->
                SectionBlock.builder()
                        .text(MarkdownTextObject.builder().text(jobName).build())
                        .accessory(subscriptions.contains(jobName) ?
                                Utils.buildButtonWithParams("Unsubscribe", jobName.concat(":Unsubscribe").concat(lastKnownList), "danger") :
                                Utils.buildButtonWithParams("Subscribe", jobName.concat(":Subscribe").concat(lastKnownList), "primary"))
                        .build())
                .collect(Collectors.toList());
    }

    private String getJobName(String source) {
        Pattern pattern = Pattern.compile("(.+):(.+):(.+)");
        Matcher matcher = pattern.matcher(source);
        return (matcher.find()) ? matcher.group(1) : null;
    }

    private List<LayoutBlock> lastKnownList(String source, User user) {
        Pattern pattern = Pattern.compile("(.+):(.+):(.+)");
        Matcher matcher = pattern.matcher(source);
        String value = (matcher.find()) ? matcher.group(3) : null;
        if ("Subscriptions".equals(value)) {
            return getSubscribes(user);
        } else {
            return getFullList(user);
        }
    }
}
