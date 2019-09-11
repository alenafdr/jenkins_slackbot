package net.thumbtack.jenkins_slackbot.controller;

import com.github.seratch.jslack.app_backend.interactive_messages.response.ActionResponse;
import net.thumbtack.jenkins_slackbot.service.InteractionsService;
import net.thumbtack.jenkins_slackbot.service.SlashCommandsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SlashCommandsController {

    private static final Logger log = LoggerFactory.getLogger(SlashCommandsController.class);
    private SlashCommandsService slashCommandsService;
    private InteractionsService interactionsService;

    @Autowired
    public SlashCommandsController(SlashCommandsService slashCommandsService, InteractionsService interactionsService) {
        this.slashCommandsService = slashCommandsService;
        this.interactionsService = interactionsService;
    }

    @RequestMapping(
            value = "/jbcal",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public ActionResponse mainMenu(String slashCommandPayload) {
        log.info("slackSlashCommand: {}", slashCommandPayload);
        return slashCommandsService.buildMenu();
    }
}
