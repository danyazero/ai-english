package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sentence")
public class SentenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "sentence", nullable = false, length = Integer.MAX_VALUE)
    private String sentence;

    @ColumnDefault("0")
    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme", nullable = false)
    private ThemeEntity theme;
}