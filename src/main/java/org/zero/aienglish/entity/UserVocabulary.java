package org.zero.aienglish.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_vocabulary")
public class UserVocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "word_id", nullable = false)
    private VocabularyEntity word;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "known", nullable = false)
    private Boolean known = false;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "last_seen", nullable = false)
    private Instant lastSeen = Instant.now();

}