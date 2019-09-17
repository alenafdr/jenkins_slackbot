package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.SlackApiException;
import com.github.seratch.jslack.api.methods.request.im.ImOpenRequest;
import com.github.seratch.jslack.api.methods.response.im.ImOpenResponse;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.webhook.Payload;
import net.thumbtack.jenkins_slackbot.dao.UserRepository;
import net.thumbtack.jenkins_slackbot.model.User;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private UserRepository userRepository;
    private Slack slack;

    @Value("${jenkins.bot.access.token}")
    private String botToken;

    @Autowired
    public MessageService(UserRepository userRepository) {
        this.slack = Slack.getInstance();
        this.userRepository = userRepository;
    }

    /**
     * @throws IOException method {@link Slack#send} can throw {@link java.io.IOException}
     */
    public void sendMessage(String url, List<LayoutBlock> answer) throws IOException {
        slack.send(url, Payload.builder().blocks(answer).build());
    }

    public void sendToUser(List<LayoutBlock> answer, User user) throws IOException, SlackApiException {
        String channelId = Optional.ofNullable(user.getPrivateChannelId())
                .orElseGet(() -> getAndUpdateChannelId(user));
        slack.methods(botToken).chatPostMessage(req -> req.channel(channelId).blocks(answer));
    }

    public String getChannelIdByUserId(String id) throws IOException, SlackApiException {
        ImOpenResponse imOpenResponse = slack.methods()
                .imOpen(ImOpenRequest
                        .builder().token(botToken)
                        .user(id)
                        .returnIm(true)
                        .build());
        return imOpenResponse.getChannel().getId();

    }

    private String getAndUpdateChannelId(User user) {
        try {
            String channel = getChannelIdByUserId(user.getId());
            user.setPrivateChannelId(channel);
            userRepository.save(user);
            return channel;
        } catch (IOException | SlackApiException e) {
            e.printStackTrace();
            return Strings.EMPTY;
        }

    }
}
