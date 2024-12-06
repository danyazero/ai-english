package org.zero.aienglish.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "title", nullable = false, length = Integer.MAX_VALUE)
    private String title = "Unknown";

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translate = "Unknown";

    @Column(name = "answers_to", nullable = false, length = Integer.MAX_VALUE)
    private String answersTo = "--";

}