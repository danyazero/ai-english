package org.zero.aienglish.mapper;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.zero.aienglish.model.UserDTO;

public class UserMapper {
    public static UserDTO map(Update update) {
        if (update.getMessage() == null) {
            return UserDTO.builder()
                    .username(update.getCallbackQuery().getFrom().getUserName())
                    .telegramId(update.getCallbackQuery().getFrom().getId())
                    .build();
        }

        return UserDTO.builder()
                .username(update.getMessage().getFrom().getUserName())
                .telegramId(update.getMessage().getFrom().getId())
                .build();
    }
}
