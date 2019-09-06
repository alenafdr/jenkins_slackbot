package net.thumbtack.jenkins_slackbot.controller;

import com.github.seratch.jslack.app_backend.interactive_messages.payload.PayloadTypeDetector;
import net.thumbtack.jenkins_slackbot.service.InteractionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class InteractionsController {
    private static final Logger log = LoggerFactory.getLogger(InteractionsController.class);

    private InteractionsService interactionsService;


    @Autowired
    public InteractionsController(InteractionsService interactionsService) {
        this.interactionsService = interactionsService;
    }

    @PostMapping(
            value = "/interactions",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public HttpStatus handleInteractions(@RequestParam(name = "payload") String payloadString) {
        PayloadTypeDetector payloadTypeDetector = new PayloadTypeDetector();
        switch (payloadTypeDetector.detectType(payloadString)) {
            case "block_actions":
                try {
                    interactionsService.handleActionBlock(payloadString);
                    return HttpStatus.OK;
                } catch (IOException e) {
                    e.printStackTrace();
                    return HttpStatus.INTERNAL_SERVER_ERROR;
                }

            default:
                return HttpStatus.BAD_REQUEST;
        }
    }
}
