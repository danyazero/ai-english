package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "speech_part")
public class SpeechPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translate;

    @Column(name = "answers_to", nullable = false, length = Integer.MAX_VALUE)
    private String answersTo;

}