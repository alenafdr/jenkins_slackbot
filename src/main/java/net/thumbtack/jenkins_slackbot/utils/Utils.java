package net.thumbtack.jenkins_slackbot.utils;

import com.github.seratch.jslack.api.model.block.SectionBlock;
import com.github.seratch.jslack.api.model.block.composition.MarkdownTextObject;
import com.github.seratch.jslack.api.model.block.composition.PlainTextObject;
import com.github.seratch.jslack.api.model.block.element.ButtonElement;
import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    @Value("${slack.webhookpair.path}")
    private static String csvFile;

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static String resolveURLByNameProject(String name) {
        return readMapFromCSV().get(name);
    }

    public static Map<String, String> readMapFromCSV() {
        Map<String, String> answer = new HashMap<>();
        CSVReader reader = null;
        LOGGER.info(csvFile);
        try {
            reader = new CSVReader(new FileReader(csvFile));
            String[] line;
            while ((line = reader.readNext()) != null) {
                answer.put(line[0], line[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static SectionBlock buildSectionBlockWithText(String text) {
        return SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(text).build())
                .build();
    }

    public static ButtonElement buildButtonWithParams(String text, String value, String style) {
        return ButtonElement.builder()
                .text(PlainTextObject.builder().text(text).build())
                .value(value)
                .style(style)
                .build();
    }

}
