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
public class SentenceTense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tense_id", nullable = false)
    private Tense tense;

    public SentenceTense(
            Tense tense,
            Sentence sentence
    ) {
        this.tense = tense;
        this.sentence = sentence;
    }
}