package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "theme")
public class ThemeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer year;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
    @Column(name = "last_update")
    private Instant lastUpdate = Instant.now();
}
