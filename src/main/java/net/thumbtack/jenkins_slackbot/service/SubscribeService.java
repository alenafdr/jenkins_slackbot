package net.thumbtack.jenkins_slackbot.service;

import com.offbytwo.jenkins.model.Job;
import net.thumbtack.jenkins_slackbot.dao.JenkinsDao;
import net.thumbtack.jenkins_slackbot.dao.JenkinsJobRepository;
import net.thumbtack.jenkins_slackbot.dao.UserRepository;
import net.thumbtack.jenkins_slackbot.model.JenkinsJob;
import net.thumbtack.jenkins_slackbot.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class SubscribeService {

    private static final Logger log = LoggerFactory.getLogger(SubscribeService.class);

    private UserRepository userRepository;
    private JenkinsJobRepository jenkinsJobRepository;
    private JenkinsDao jenkinsDao;

    @Autowired
    public SubscribeService(UserRepository userRepository, JenkinsJobRepository jenkinsJobRepository, JenkinsDao jenkinsDao) {
        this.userRepository = userRepository;
        this.jenkinsJobRepository = jenkinsJobRepository;
        this.jenkinsDao = jenkinsDao;
    }

    void addSubscribe(User user, String jobName) {
        User newUser = getOrCreate(user);
        JenkinsJob jenkinsJob = jenkinsJobRepository.findByName(jobName)
                .orElseGet(() -> jenkinsJobRepository.save(
                        new JenkinsJob(jenkinsDao.findByName(jobName)
                                .orElseThrow(() -> new RuntimeException("There is no job with name " + jobName)))));
        newUser.getSubscriptions().add(jenkinsJob);
        userRepository.save(newUser);
    }

    void updateListOfJobs() {
        Iterable<JenkinsJob> jenkinsJobs = jenkinsJobRepository.findAll();
        Set<JenkinsJob> newSet = new HashSet<>();
        jenkinsJobs.forEach(newSet::add);
        Map<String, Job> jobs = jenkinsDao.selectJobs();
        jobs.values().stream()
                .map(job -> new JenkinsJob(job.getName(), job.getUrl(), job.getFullName()))
                .forEach(newSet::add);
        jenkinsJobRepository.saveAll(newSet);
    }

    Set<JenkinsJob> getSubscriptionsByUser(User user) {
        User user1 = userRepository.findById(user.getId()).orElseGet(() -> userRepository.save(user));
        return user1.getSubscriptions();
    }

    void removeSubscribe(User user, String jobName) {
        User newUser = getOrCreate(user);
        newUser.getSubscriptions().removeIf(job -> job.getName().equals(jobName));
        userRepository.save(newUser);
    }

    private User getOrCreate(User user) {
        return userRepository.findById(user.getId()).orElseGet(() -> userRepository.save(user));
    }
}
