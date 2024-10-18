package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tense")
public class Tense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "title_duration", nullable = false)
    private Duration titleDuration;

    @Column(name = "formula", nullable = false, length = Integer.MAX_VALUE)
    private String formula;

    @Column(name = "verb", nullable = false, length = Integer.MAX_VALUE)
    private String verb;

}