package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "user_theme")
public class UserThemeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "theme_id")
    private ThemeEntity theme;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    public UserThemeEntity() {}

    public UserThemeEntity(ThemeEntity film, UserEntity user) {
        this.theme = film;
        this.user = user;
    }
}
