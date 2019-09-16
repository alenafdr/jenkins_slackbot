package net.thumbtack.jenkins_slackbot.service;

import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import net.thumbtack.jenkins_slackbot.dao.JenkinsDao;
import net.thumbtack.jenkins_slackbot.dao.JenkinsJobRepository;
import net.thumbtack.jenkins_slackbot.model.JenkinsJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WatcherService {
    private static final Logger log = LoggerFactory.getLogger(WatcherService.class);

    private JenkinsDao jenkinsDao;
    private JenkinsJobRepository jenkinsJobRepository;
    private NotifierService notifierService;

    @Autowired
    public WatcherService(JenkinsDao jenkinsDao, NotifierService notifierService, JenkinsJobRepository jenkinsJobRepository) {
        this.jenkinsDao = jenkinsDao;
        this.notifierService = notifierService;
        this.jenkinsJobRepository = jenkinsJobRepository;
    }

    @Scheduled(fixedDelay = 30 * 1000)
    public void checkBuilds() {
        Map<String, Optional<JobWithDetails>> jobs = jenkinsDao.getJobsWithDetails();

        Map<JobWithDetails, List<BuildWithDetails>> newFinishedBuildsForJobs = new HashMap<>();
        jobs.values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(job -> {
                    int lastKnownBuild = jenkinsJobRepository.findByName(job.getName()).map(JenkinsJob::getLastKnownBuild).orElse(0);
                    List<BuildWithDetails> newBuilds = jenkinsDao.getLastFinishedBuildsByJobName(job.getName(), lastKnownBuild);
                    if (newBuilds.size() > 0) newFinishedBuildsForJobs.put(job, newBuilds);
                });

        notifierService.notifyAboutNewBuilds(newFinishedBuildsForJobs);
        updateJobsInDB(newFinishedBuildsForJobs);
    }

    private void updateJobsInDB(Map<JobWithDetails, List<BuildWithDetails>> newFinishedBuildsForJobs) {
        for (Map.Entry<JobWithDetails, List<BuildWithDetails>> newBuild : newFinishedBuildsForJobs.entrySet()) {
            JobWithDetails jobWithDetails = newBuild.getKey();
            BuildWithDetails lastBuild = Collections.max(newBuild.getValue(), Comparator.comparing(BuildWithDetails::getNumber));
            JenkinsJob newJob = jenkinsJobRepository.findByName(jobWithDetails.getName()).orElseGet(() -> jenkinsJobRepository.save(new JenkinsJob(jobWithDetails)));
            newJob.setLastKnownBuild(lastBuild.getNumber());
            jenkinsJobRepository.save(newJob);
        }
    }
}
