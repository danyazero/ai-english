package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tense")
public class TenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "title_duration", nullable = false)
    private DurationEntity titleDuration;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "title_time", nullable = false)
    private TimeEntity titleTime;

    @Column(name = "formula", nullable = false, length = Integer.MAX_VALUE)
    private String formula;

    @Column(name = "verb", nullable = false, length = Integer.MAX_VALUE)
    private String verb;

}