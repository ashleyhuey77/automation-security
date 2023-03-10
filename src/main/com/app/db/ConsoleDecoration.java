package com.app.db;

import com.utils.CDeco;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleDecoration {

    public static void printSection(String msg) {
        StringBuilder starsBuilder = new StringBuilder();
        for (int i = 0; i < msg.length() + 4; i++) {
            starsBuilder.append("*");
        }
        String stars = starsBuilder.toString();
        log.info("{}{}{}{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, "\n", stars);
        log.info("{}{}* {} *", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, msg);
        log.info("{}{}{}{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, stars, CDeco.RESET.value);
    }

}
