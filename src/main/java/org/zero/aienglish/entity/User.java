package org.zero.aienglish.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "user")
public class User {
    @Id
    private Integer id;
    private String username;
}
