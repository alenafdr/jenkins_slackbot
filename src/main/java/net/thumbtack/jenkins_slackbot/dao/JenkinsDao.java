package net.thumbtack.jenkins_slackbot.dao;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JenkinsDao {

    private static final Logger log = LoggerFactory.getLogger(JenkinsDao.class);

    @Value("${jenkins.login}")
    private String login;

    @Value("${jenkins.password}")
    private String password;

    @Value("${jenkins.host}")
    private String host;

    public Map<String, Job> selectJobs() {
        try (JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password)) {
            return jenkins.getJobs();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Optional<JobWithDetails> findByName(String name) {
        try (JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password)) {
            return Optional.of(jenkins.getJob(name));
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Map<String, Optional<JobWithDetails>> getJobsWithDetails() {
        try (JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password)) {
            Map<String, Optional<JobWithDetails>> jobWithLastBuild = new HashMap<>();
            jenkins.getJobs()
                    .keySet()
                    .forEach(jobName -> jobWithLastBuild.put(jobName, getLastBuildByName(jenkins, jobName)));
            return jobWithLastBuild;
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private Optional<JobWithDetails> getLastBuildByName(JenkinsServer server, String name) {
        try {
            return Optional.ofNullable(server.getJob(name));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public List<BuildWithDetails> getLastFinishedBuildsByJobName(String jobName, int from) {
        try (JenkinsServer jenkins = new JenkinsServer(new URI(host), login, password)){
            JobWithDetails job = jenkins.getJob(jobName);
            return job.getAllBuilds()
                    .stream()
                    .map(this::getDetails)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(buildWithDetails -> buildWithDetails.getNumber() > from)
                    .filter(buildWithDetails -> {
                        BuildResult result = buildWithDetails.getResult();
                        return (result == BuildResult.ABORTED ||
                                result == BuildResult.CANCELLED ||
                                result == BuildResult.FAILURE ||
                                result == BuildResult.NOT_BUILT ||
                                result == BuildResult.SUCCESS);
                    })
                    .collect(Collectors.toList());
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Optional<BuildWithDetails> getDetails(Build build) {
        try {
            return Optional.ofNullable(build.details());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
