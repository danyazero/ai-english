package org.zero.aienglish.utils.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.CallbackEnum;

import java.util.List;

@Slf4j
@Component
public class DisabledCallback implements TelegramCallback {

    @Override
    public String getName() {
        return CallbackEnum.DISABLE.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var callbackProcessed = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .showAlert(true)
                .build();

        return List.of(callbackProcessed);
    }
}
