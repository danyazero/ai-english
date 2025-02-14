package org.zero.aienglish.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.zero.aienglish.mapper.SubscriptionMapper;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.TelegramCommand;
import org.zero.aienglish.model.UserStatistic;
import org.zero.aienglish.model.UserSubscription;
import org.zero.aienglish.model.UserSubscriptionDetails;
import org.zero.aienglish.service.SubscriptionService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.DayTitle;
import org.zero.aienglish.utils.TelegramInlineKeyboard;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileCommand implements TelegramCommand {
    private final DayTitle dayTitle;
    private final UserService userService;
    private final TelegramInlineKeyboard inlineKeyboard;
    private final SubscriptionService subscriptionService;

    @Override
    public String getName() {
        return "/profile";
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update, TelegramClient client) {
        var chatId = update.getMessage().getChatId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var userStatistic = userService.getUserStatistic(serverUserId);
        var userSubscriptionDetails = subscriptionService.getUserSubscriptionDetails(serverUserId);

        var messageText = getMessageText(userSubscriptionDetails.userSubscription(), userStatistic);
        var messageKeyboard = getUserSubscriptionMenu(userSubscriptionDetails);

        var message = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(messageKeyboard)
                .build();

        return List.of(message);
    }

    private InlineKeyboardMarkup getUserSubscriptionMenu(UserSubscriptionDetails userSubscriptionDetails) {
        var buttons = userSubscriptionDetails.availablePlans().stream()
                .map(SubscriptionMapper::map)
                .collect(Collectors.toList());

        return inlineKeyboard.apply(buttons);
    }

    private String getMessageText(Optional<UserSubscription> userSubscription, UserStatistic statistic) {
        if (userSubscription.isEmpty()) {
            log.info("User subscription is empty");
            return "У вас немає активної пидписки.\n";
        }

        return """
                <b>У ВАС СЕРІЯ -> %s</b>

                <b>Практикувались:</b> %s хв.
                <b>Правильних відповідей:</b> %.0f%%
                
                <b>План:</b> %s
                <b>Дійсна до:</b> %s (%s)
                <b>Вартість:</b> %.2f грн
                
            
                """.formatted(
                dayTitle.apply(statistic.daysStreak()),
                statistic.todaySpentTime(),
                statistic.todayAcceptedPercentage(),
                userSubscription.get().planName(),
                userSubscription.get().end_date(),
                dayTitle.apply(userSubscription.get().remainDays()),
                userSubscription.get().price()
        );
    }
}
