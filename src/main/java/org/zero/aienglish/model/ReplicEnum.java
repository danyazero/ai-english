package org.zero.aienglish.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReplicEnum {
    START("""
            Ну нарешті ти з'явився, а то я вже мало не заснув, чекаючи на тебе!
            
            Готовий вчити англійську?
            Тоді без зайвих церемоній — обирай що хочешь робити!
            """),
    WORD_END("""
            Ого, ти вже все вивчив?
            А тепер що, сидиш і чекаєш, поки я магічно вигадаю нові слова?
            Може, поки повториш старе, щоб не забути, розумнику?
            """),
    GOOD_WORK("✅ Так тримати!"),
    BAD_WORK("🧨 Ну що, знову повз?"),
    PRACTICE("Розпочати практику"),
    VOCABULARY("Словник до завдання"),
    TASK_NOT_FOUND("Завданя не знайдено."),
    NEXT("Далі"),
    PREV("Назад");

    private final String value;
}
