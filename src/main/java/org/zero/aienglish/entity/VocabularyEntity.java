package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vocabulary")
public class VocabularyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "word", nullable = false, length = Integer.MAX_VALUE)
    private String word;

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translate;

    @ColumnDefault("0")
    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "speech_part_id", nullable = false)
    private SpeechPartEntity speechPart;

}