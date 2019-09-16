package net.thumbtack.jenkins_slackbot.model;

import com.github.seratch.jslack.app_backend.interactive_messages.payload.BlockActionPayload;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "jenkins_job_id"))
    private Set<JenkinsJob> subscriptions;

    private String privateChannelId;

    public User() {
    }

    public User(BlockActionPayload.User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.subscriptions = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<JenkinsJob> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<JenkinsJob> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getPrivateChannelId() {
        return privateChannelId;
    }

    public void setPrivateChannelId(String privateChannelId) {
        this.privateChannelId = privateChannelId;
    }
}
