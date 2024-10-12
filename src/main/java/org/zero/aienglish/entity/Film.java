package org.zero.aienglish.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "film")
public class Film {
    @Id
    private Integer id;
    private String title;
    private Integer year;
}
