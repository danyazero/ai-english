package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vocabulary_sentence")
public class VocabularySentence {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vocabulary_sentence_id_gen")
    @SequenceGenerator(name = "vocabulary_sentence_id_gen", sequenceName = "vocabulary_sentence_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sentence_id", nullable = false)
    private Sentence sentence;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vocabulary_id", nullable = false)
    private Vocabulary vocabulary;

}