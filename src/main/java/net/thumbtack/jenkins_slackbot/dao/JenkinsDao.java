package net.thumbtack.jenkins_slackbot.dao;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class JenkinsDao {

    @Value("${jenkins.login}")
    private String login;

    @Value("${jenkins.password}")
    private String password;

    @Value("${jenkins.host}")
    private String host;

    public Map<String, Job> selectJobs() {
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password);
            return jenkins.getJobs();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Optional<Job> findByName(String name) {
        try {
            JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password);
            return Optional.of(jenkins.getJob(name));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
