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
public class Tense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "duration_id", nullable = false)
    private Duration titleDuration;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "time_id", nullable = false)
    private TimeEntity titleTime;

    @Column(name = "formula", nullable = false, length = Integer.MAX_VALUE)
    private String formula;

    @Column(name = "verb", nullable = false, length = Integer.MAX_VALUE)
    private String verb;

}