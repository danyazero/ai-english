package org.zero.aienglish.callback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.zero.aienglish.mapper.UserMapper;
import org.zero.aienglish.model.CheckoutDTO;
import org.zero.aienglish.model.TelegramCallback;
import org.zero.aienglish.model.TelegramCallbackEnum;
import org.zero.aienglish.service.SubscriptionService;
import org.zero.aienglish.service.UserService;
import org.zero.aienglish.utils.DayTitle;
import org.zero.aienglish.utils.FormatDate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CheckoutCallback implements TelegramCallback {
    private final DayTitle dayTitle;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    @Override
    public String getName() {
        return TelegramCallbackEnum.PLAN_CHECKOUT.name();
    }

    @Override
    public List<BotApiMethod<?>> apply(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();

        var user = UserMapper.map(update);
        var serverUserId = userService.getUserIdIfExistOrElseCreate(user);

        var planId = Integer.parseInt(update.getCallbackQuery().getData().split(" ")[1]);

        var checkout = subscriptionService.getSubscriptionPlanCheckout(serverUserId, planId);

        var messageText = getCheckoutMessageText(checkout);
        var messageKeyboard = getCheckoutMessageKeyboard(checkout);

        var message = SendMessage.builder()
                .text(messageText)
                .parseMode("HTML")
                .replyMarkup(messageKeyboard)
                .chatId(chatId)
                .build();

        return List.of(message);
    }

    private static InlineKeyboardMarkup getCheckoutMessageKeyboard(CheckoutDTO checkout) {
        var keyboardMarkup = InlineKeyboardMarkup.builder();
        var newKeyboardRow = new InlineKeyboardRow(
                InlineKeyboardButton
                        .builder()
                        .text("Сплатити • " + checkout.subscriptionPlan().price() + "UAH")
                        .url(checkout.checkoutLink())
                        .build()
        );
        keyboardMarkup.keyboardRow(newKeyboardRow);

        return keyboardMarkup.build();
    }

    private String getCheckoutMessageText(CheckoutDTO checkout) {
        return """
                <b>План:</b> %s
                <b>Дійсна:</b> %s (%s)
                <b>Вартість:</b> %.2f грн
                
                <i>Час нової підписки додається до залишку попередньої.</i>
                """.formatted(
                checkout.subscriptionPlan().name(),
                dayTitle.apply(checkout.subscriptionPlan().durationDays()),
                FormatDate.format(checkout.validDue()),
                checkout.subscriptionPlan().price()
        );
    }
}
