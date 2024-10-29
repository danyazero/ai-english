package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity(name = "theme")
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer year;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "last_update")
    private Instant lastUpdate = Instant.now();
}
