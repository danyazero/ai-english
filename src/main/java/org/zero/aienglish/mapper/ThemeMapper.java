package org.zero.aienglish.mapper;

import org.zero.aienglish.entity.Theme;
import org.zero.aienglish.lib.grpc.ThemeOuterClass;
import org.zero.aienglish.model.ThemeDTO;

public class ThemeMapper {

    public static ThemeDTO map(Theme theme) {
        return ThemeDTO.builder()
                .title(theme.getTitle())
                .id(theme.getId())
                .isSelected(true)
                .build();
    }

    public static ThemeOuterClass.Theme map(ThemeDTO theme) {
        return ThemeOuterClass.Theme.newBuilder()
                .setIsSelected(theme.isSelected())
                .setTitle(theme.title())
                .setId(theme.id())
                .build();
    }
}
