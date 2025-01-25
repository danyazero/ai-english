package org.zero.aienglish.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vocabulary")
public class Vocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "word", nullable = false, length = Integer.MAX_VALUE)
    private String word;

    @Column(name = "second", nullable = false, length = Integer.MAX_VALUE)
    private String secondForm;

    @Column(name = "third", nullable = false, length = Integer.MAX_VALUE)
    private String thirdForm;

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translate;

    @Column(name = "meaning", nullable = false, length = Integer.MAX_VALUE)
    private String meaning = "--";
}