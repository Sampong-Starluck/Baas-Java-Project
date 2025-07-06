package org.sampong.models;

import jakarta.persistence.*;
import org.sampong.base.BaseEntity;
import org.sampong.models.enumerate.TransactionType;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "mas_transaction_record")
public class TransactionRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "from_user_id")
    private Long fromUserId;
    @Column(name = "trx_id")
    private String transactionId;
    @Enumerated(EnumType.STRING)
    @Column(name = "trx_type")
    private TransactionType transactionType;
    private Double amount;
    @Column(name = "from_account_id")
    private Long fromAccountId;
    @Column(name = "from_account_number")
    private String fromAccountNumber;
    @Column(name = "to_account_number")
    private String toAccountNumber;
    @Column(name = "to_user_id")
    private Long toUserId;
    @Column(name = "to_account_id")
    private Long toAccountId;
    @Column(name = "trx_time_date")
    private LocalDateTime transactionTimeDate;

    public TransactionRecord() {
    }

    public TransactionRecord(Long id, Long toAccountId, Long toUserId, String toAccountNumber, String fromAccountNumber, Long fromAccountId, Double amount, String transactionId, Long fromUserId, LocalDateTime transactionTimeDate, TransactionType transactionType) {
        this.id = id;
        this.toAccountId = toAccountId;
        this.toUserId = toUserId;
        this.toAccountNumber = toAccountNumber;
        this.fromAccountNumber = fromAccountNumber;
        this.fromAccountId = fromAccountId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.fromUserId = fromUserId;
        this.transactionTimeDate = transactionTimeDate;
        this.transactionType = transactionType;
    }

    public Long id() {
        return id;
    }

    public TransactionRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public Long fromUserId() {
        return fromUserId;
    }

    public TransactionRecord setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
        return this;
    }

    public String transactionId() {
        return transactionId;
    }

    public TransactionRecord setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public Double amount() {
        return amount;
    }

    public TransactionRecord setAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    public Long fromAccountId() {
        return fromAccountId;
    }

    public TransactionRecord setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
        return this;
    }

    public String fromAccountNumber() {
        return fromAccountNumber;
    }

    public TransactionRecord setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
        return this;
    }

    public String toAccountNumber() {
        return toAccountNumber;
    }

    public TransactionRecord setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
        return this;
    }

    public Long toUserId() {
        return toUserId;
    }

    public TransactionRecord setToUserId(Long toUserId) {
        this.toUserId = toUserId;
        return this;
    }

    public Long toAccountId() {
        return toAccountId;
    }

    public TransactionRecord setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
        return this;
    }

    public LocalDateTime transactionTimeDate() {
        return transactionTimeDate;
    }

    public TransactionRecord setTransactionTimeDate(LocalDateTime transactionTimeDate) {
        this.transactionTimeDate = transactionTimeDate;
        return this;
    }

    public TransactionType transactionType() {
        return transactionType;
    }

    public TransactionRecord setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransactionRecord that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(fromUserId, that.fromUserId) && Objects.equals(transactionId, that.transactionId) && transactionType == that.transactionType && Objects.equals(amount, that.amount) && Objects.equals(fromAccountId, that.fromAccountId) && Objects.equals(fromAccountNumber, that.fromAccountNumber) && Objects.equals(toAccountNumber, that.toAccountNumber) && Objects.equals(toUserId, that.toUserId) && Objects.equals(toAccountId, that.toAccountId) && Objects.equals(transactionTimeDate, that.transactionTimeDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromUserId, transactionId, transactionType, amount, fromAccountId, fromAccountNumber, toAccountNumber, toUserId, toAccountId, transactionTimeDate);
    }

    @Override
    public String toString() {
        return "TransactionRecord{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", transactionId='" + transactionId + '\'' +
                ", transactionType=" + transactionType +
                ", amount=" + amount +
                ", fromAccountId=" + fromAccountId +
                ", fromAccountNumber='" + fromAccountNumber + '\'' +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                ", toUserId=" + toUserId +
                ", toAccountId='" + toAccountId + '\'' +
                ", transactionTimeDate=" + transactionTimeDate +
                '}';
    }
}
