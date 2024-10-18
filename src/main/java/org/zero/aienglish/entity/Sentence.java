package org.zero.aienglish.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "sentence")
//@Accessors(chain = true)
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "sentence", nullable = false, length = Integer.MAX_VALUE)
    private String sentence;

    @ColumnDefault("0")
    @Column(name = "views", nullable = false)
    private Integer views = 0;

    @Column(name = "translate", nullable = false, length = Integer.MAX_VALUE)
    private String translate;

    @Column(name = "\"grammar_task\"", nullable = false, length = Integer.MAX_VALUE)
    private String grammarTask;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme", nullable = false)
    private Theme theme;

}