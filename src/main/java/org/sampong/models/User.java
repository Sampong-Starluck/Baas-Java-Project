package org.sampong.models;

import jakarta.persistence.*;

@Entity
@Table(name = "mas_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean status;
    private String username;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String email;
    private String address;

    public User() {
    }

    public User(Long id, Boolean status, String username, String phoneNumber, String address, String email) {
        this.address = address;
        this.email = email;
        this.id = id;
        this.status = status;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public Long id() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public Boolean status() {
        return status;
    }

    public User setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String username() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public User setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String email() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String address() {
        return address;
    }

    public User setAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", status=" + status +
                ", username='" + username + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
