package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vocabulary_sentence")
public class VocabularySentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

    @Column(name = "\"order\"")
    private Short order = 0;

    @Column(name = "default_word")
    private String defaultWord = "";

    @Column(name = "is_marker")
    private Boolean isMarker = Boolean.FALSE;

    public VocabularySentence(
            Sentence sentence,
            Vocabulary vocabulary
    ) {
        this.sentence = sentence;
        this.vocabulary = vocabulary;
    }
}