package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import net.thumbtack.jenkins_slackbot.dao.JenkinsJobRepository;
import net.thumbtack.jenkins_slackbot.model.JenkinsJob;
import net.thumbtack.jenkins_slackbot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotifierService {
    private static final Logger log = LoggerFactory.getLogger(NotifierService.class);
    private MessageService messageService;
    private JenkinsJobRepository jenkinsJobRepository;

    public NotifierService(MessageService messageService, JenkinsJobRepository jenkinsJobRepository) {
        this.messageService = messageService;
        this.jenkinsJobRepository = jenkinsJobRepository;
    }

    public void notifyAboutNewBuilds(Map<JobWithDetails, List<BuildWithDetails>> newBuildsForJobs) {
        for (Map.Entry<JobWithDetails, List<BuildWithDetails>> newBuild : newBuildsForJobs.entrySet()) {
            String jobName = newBuild.getKey().getName();
            List<BuildWithDetails> builds = newBuild.getValue();
            List<User> subscribers = jenkinsJobRepository.findByName(jobName)
                    .orElseGet(() -> new JenkinsJob(Collections.emptyList()))
                    .getSubscribers();
            subscribers.forEach(user -> {
                messageService.sendToUser(buildMessageWithStatus(builds, jobName), user);
            });
        }
    }

    private List<LayoutBlock> buildMessageWithStatus(List<BuildWithDetails> builds, String jobName) {
        return builds.stream()
                .map(build ->
                        SectionBlock
                                .builder()
                                .text(getTextWithStatus(build, jobName))
                                .build()
                ).collect(Collectors.toList());
    }

    private PlainTextObject getTextWithStatus(BuildWithDetails build, String jobName) {
        String status;
        status = build.getResult().toString();
        return PlainTextObject.builder()
                .text(jobName + " number of build " + build.getNumber() + " is " + status)
                .emoji(true)
                .build();
    }
}
