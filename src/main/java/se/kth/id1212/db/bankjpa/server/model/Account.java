package se.kth.id1212.db.bankjpa.server.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import se.kth.id1212.db.bankjpa.common.AccountDTO;

@NamedQueries({
    @NamedQuery(
            name = "deleteAccountByName",
            query = "DELETE FROM Account acct WHERE acct.holder.name LIKE :holderName"
    )
    ,
        @NamedQuery(
            name = "findAccountByName",
            query = "SELECT acct FROM Account acct WHERE acct.holder.name LIKE :holderName",
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
        @NamedQuery(
            name = "findAllAccounts",
            query = "SELECT acct FROM Account acct",
            lockMode = LockModeType.OPTIMISTIC
    )
})

/**
 * An account in the bank.
 */
@Entity(name = "Account")
public class Account implements AccountDTO {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @Column(name = "balance", nullable = false)
    private int balance;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "holder", nullable = false)
    private Holder holder;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    /**
     * Creates an instance without holder and with the balance zero. A public no-arg constructor is
     * required by JPA.
     */
    public Account() {
        this(null, 0);
    }

    /**
     * Creates an instance with the specified holder and the balance zero.
     *
     * @param holder The holder of the newly created instance.
     */
    public Account(Holder holder) {
        this(holder, 0);
    }

    /**
     * Creates an instance with the specified holder and balance.
     *
     * @param holder  The holder of the newly created instance.
     * @param balance The balance of the newly created instance.
     */
    public Account(Holder holder, int balance) {
        this.holder = holder;
        this.balance = balance;
    }

    /**
     * @return The balance of this account.
     */
    @Override
    public int getBalance() {
        return balance;
    }

    /**
     * @return The name of this account's holder.
     */
    @Override
    public String getHolderName() {
        return holder.getName();
    }

    /**
     * Deposits the specified amount to this account.
     *
     * @param amount The amount to deposit.
     * @throws RejectedException If trying to deposit a negative amount.
     */
    public void deposit(int amount) throws RejectedException {
        if (amount < 0) {
            throw new RejectedException(
                    "Tried to deposit negative value, illegal value: " + amount + "." + accountInfo());
        }
        balance += amount;
    }

    /**
     * Withdraws the specified amount from this account.
     *
     * @param amount The amount to withdraw.
     * @throws RejectedException If trying to withdraw a negative amount or an amount greater than
     *                           the balance.
     */
    public void withdraw(int amount) throws RejectedException {
        if (amount < 0) {
            throw new RejectedException(
                    "Tried to withdraw negative value, illegal value: " + amount + "." + accountInfo());
        }
        if (balance - amount < 0) {
            throw new RejectedException(
                    "Tried to overdraft, illegal value: " + amount + "." + accountInfo());
        }
        balance -= amount;
    }

    private String accountInfo() {
        return " " + this;
    }

    /**
     * @return A string representation of all fields in this object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Account: [");
        stringRepresentation.append("holder: ");
        stringRepresentation.append(holder.getName());
        stringRepresentation.append(", balance: ");
        stringRepresentation.append(balance);
        stringRepresentation.append("]");
        return stringRepresentation.toString();
    }
}
