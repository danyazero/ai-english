package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "user_theme")
public class UserTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserTheme() {}

    public UserTheme(Theme film, User user) {
        this.theme = film;
        this.user = user;
    }
}
