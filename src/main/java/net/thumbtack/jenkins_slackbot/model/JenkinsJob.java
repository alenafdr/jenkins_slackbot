package net.thumbtack.jenkins_slackbot.model;

import com.offbytwo.jenkins.model.Job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class JenkinsJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "full_name")
    private String fullName;

    public JenkinsJob() {
    }

    public JenkinsJob(String name) {
        this.name = name;
    }

    public JenkinsJob(String name, String url, String fullName) {
        this.name = name;
        this.url = url;
        this.fullName = fullName;
    }

    public JenkinsJob(Job job) {
        this.name = job.getName();
        this.url = job.getUrl();
        this.fullName = job.getFullName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JenkinsJob that = (JenkinsJob) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
