package net.thumbtack.jenkins_slackbot.dao;

import net.thumbtack.jenkins_slackbot.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, String> {

    public Optional<User> findByName(String name);
}
