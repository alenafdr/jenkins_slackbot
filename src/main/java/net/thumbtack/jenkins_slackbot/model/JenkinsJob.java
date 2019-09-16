package net.thumbtack.jenkins_slackbot.model;

import com.offbytwo.jenkins.model.Job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "subscriptions",
            joinColumns = @JoinColumn(name = "jenkins_job_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> subscribers;

    private int lastKnownBuild;

    public JenkinsJob(List<User> subscribers) {
        this.subscribers = subscribers;
    }

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

    public List<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<User> subscribers) {
        this.subscribers = subscribers;
    }

    public int getLastKnownBuild() {
        return lastKnownBuild;
    }

    public void setLastKnownBuild(int lastKnownBuild) {
        this.lastKnownBuild = lastKnownBuild;
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
