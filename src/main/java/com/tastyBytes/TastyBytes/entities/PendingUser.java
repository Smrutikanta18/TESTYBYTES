package com.tastyBytes.TastyBytes.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pending_users")
public class PendingUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String number;
    private String gender;
    private String verificationToken;

    public PendingUser(String firstname, String lastname, String email, String password, String number, String gender, String verificationToken, Timestamp created_at) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.number = number;
        this.gender = gender;
        this.verificationToken = verificationToken;
        this.created_at = created_at;
    }

    private Timestamp created_at;


}

