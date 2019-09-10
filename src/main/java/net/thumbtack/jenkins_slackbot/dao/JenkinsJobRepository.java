package net.thumbtack.jenkins_slackbot.dao;

import net.thumbtack.jenkins_slackbot.model.JenkinsJob;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JenkinsJobRepository extends CrudRepository<JenkinsJob, Long> {
    public Optional<JenkinsJob> findByName(String name);
}
