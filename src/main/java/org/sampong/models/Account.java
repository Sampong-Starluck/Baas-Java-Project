package org.sampong.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import org.sampong.base.BaseEntity;

import java.util.Objects;

@Entity
@Table(name = "mas_account")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Account extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String accountNumber;
    private String accountName;
    private Double balance;
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Account() {
    }

    public Account(Long id, User user, String currency, Double balance, String accountName, String accountNumber) {
        this.id = id;
        this.user = user;
        this.currency = currency;
        this.balance = balance;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }

    public Long id() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public User user() {
        return user;
    }

    public Account setUser(User user) {
        this.user = user;
        return this;
    }

    public String currency() {
        return currency;
    }

    public Account setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String accountName() {
        return accountName;
    }

    public Account setAccountName(String accountName) {
        this.accountName = accountName;
        return this;
    }

    public String accountNumber() {
        return accountNumber;
    }

    public Account setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public Double balance() {
        return balance;
    }

    public Account setBalance(Double balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id)
                && Objects.equals(accountNumber, account.accountNumber)
                && Objects.equals(accountName, account.accountName)
                && Objects.equals(balance, account.balance)
                && Objects.equals(currency, account.currency)
                && Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, accountName, balance, currency, user);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", user=" + user +
                '}';
    }
}
