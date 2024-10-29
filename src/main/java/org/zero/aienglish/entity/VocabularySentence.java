package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "vocabulary_sentence")
public class VocabularySentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    public VocabularySentence(Sentence sentence, Vocabulary vocabulary, Short order, String defaultWord, Boolean isMarker) {
        this.sentence = sentence;
        this.vocabulary = vocabulary;
        this.order = order;
        this.defaultWord = defaultWord;
        this.isMarker = isMarker;
    }

    public VocabularySentence(Sentence sentence, Vocabulary vocabulary) {
        this.sentence = sentence;
        this.vocabulary = vocabulary;
    }
}