package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.app_backend.interactive_messages.response.ActionResponse;
import net.thumbtack.jenkins_slackbot.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SlashCommandsService {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandsService.class);
    public ActionResponse buildMenu() {
        return ActionResponse
                .builder()
                .blocks(buildBlocksMenu())
                .build();
    }

    private List<LayoutBlock> buildBlocksMenu() {
        SectionBlock sectionAll = SectionBlock.builder()
                .text(MarkdownTextObject.builder().text("Get a list of all jobs").build())
                .accessory(Utils.buildButtonWithParams("Get all", "buttonValueAll", null))
                .build();
        SectionBlock sectionSubscribes = SectionBlock.builder()
                .text(MarkdownTextObject.builder().text("Get a list of subscriptions").build())
                .accessory(Utils.buildButtonWithParams("Get subscription", "buttonValueSubscriptions", null))
                .build();
        SectionBlock sectionUpdateList = SectionBlock.builder()
                .text(MarkdownTextObject.builder().text("Update list of jobs").build())
                .accessory(Utils.buildButtonWithParams("Update", "buttonValueUpdate", null))
                .build();
        return Arrays.asList(sectionAll, sectionSubscribes, sectionUpdateList);
    }
}
