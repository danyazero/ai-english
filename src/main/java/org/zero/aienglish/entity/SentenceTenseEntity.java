package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sentence_tense")
public class SentenceTenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sentence_id", nullable = false)
    private SentenceEntity sentence;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tense_id", nullable = false)
    private TenseEntity tense;

    public SentenceTenseEntity(
            TenseEntity tense,
            SentenceEntity sentence
    ) {
        this.tense = tense;
        this.sentence = sentence;
    }
}