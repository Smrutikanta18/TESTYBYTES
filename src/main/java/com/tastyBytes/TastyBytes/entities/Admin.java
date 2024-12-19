package com.tastyBytes.TastyBytes.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;
    private String email;
    private String password;

    @Column(nullable = false)
    private String role;

    @PrePersist
    public void prePersist() {
        if (this.role == null) {
            this.role = "ADMIN";
        }
    }
}
