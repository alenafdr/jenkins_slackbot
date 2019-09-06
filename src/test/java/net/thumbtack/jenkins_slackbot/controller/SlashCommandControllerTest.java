package net.thumbtack.jenkins_slackbot.controller;

import net.thumbtack.jenkins_slackbot.service.SlashCommandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = SlashCommandsController.class, secure = false)
@ContextConfiguration(classes = {SlashCommandsController.class, SlashCommandService.class})
public class SlashCommandControllerTest {
    Logger LOGGER = LoggerFactory.getLogger(SlashCommandControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void sendSlashCommandTest() throws Exception {

        RequestBuilder requestBuilder = post("/api/v1/jbcal")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .content(buildSlashCommandPayload());

        ResultActions resultActions = mockMvc.perform(requestBuilder);
        LOGGER.info("resultActions: {}", resultActions.andReturn().getResponse().getContentAsString());
        resultActions.andExpect(status().isOk());
    }

    private String buildSlashCommandPayload() {
        return "token=U8BW9FZuVvnOCR6hiMhiH5YO&" +
                "teamId=null&teamDomain=null&" +
                "enterpriseId=null&" +
                "enterpriseName=null&" +
                "channelId=null&" +
                "channelName=null&" +
                "userId=null&" +
                "userName=null&" +
                "command=/jbcal&" +
                "text=&" +
                "responseUrl=null&" +
                "triggerId=null";
    }
}
