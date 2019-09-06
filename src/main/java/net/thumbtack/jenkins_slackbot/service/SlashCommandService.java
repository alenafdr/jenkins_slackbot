package net.thumbtack.jenkins_slackbot.service;

import com.github.seratch.jslack.api.model.block.ActionsBlock;
import com.github.seratch.jslack.api.model.block.LayoutBlock;
import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.github.seratch.jslack.app_backend.interactive_messages.response.ActionResponse;
import net.thumbtack.jenkins_slackbot.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class SlashCommandService {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandService.class);
    public ActionResponse buildMenu() {
        return ActionResponse
                .builder()
                .blocks(buildBlocksMenu())
                .build();
    }


    private List<LayoutBlock> buildBlocksMenu() {
        return buildListBlock();
    }

    private List<LayoutBlock> buildListBlock() {
        SectionBlock messageFullListBlock = Utils.buildSectionBlockWithText("Get a list of all jobs");
        SectionBlock messageListOfSubscribed = Utils.buildSectionBlockWithText("Get a list of subscribed");
        ButtonElement buttonFullListBlock = ButtonElement.builder()
                .text(PlainTextObject.builder().text("Get all").build())
                .value("buttonValueAll")
                .build();
        ButtonElement buttonListOfSubscribedBlock = ButtonElement.builder()
                .text(PlainTextObject.builder().text("Get subscribes").build())
                .value("buttonValueSubscribes")
                .build();
        ActionsBlock actionFullList = ActionsBlock
                .builder()
                .elements(Collections.singletonList(buttonFullListBlock))
                .build();
        ActionsBlock actionListOfSubscribed = ActionsBlock
                .builder()
                .elements(Collections.singletonList(buttonListOfSubscribedBlock))
                .build();
        return Arrays.asList(messageFullListBlock,
                actionFullList,
                messageListOfSubscribed,
                actionListOfSubscribed);
    }


}
