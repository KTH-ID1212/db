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
                query = "DELETE FROM Account acct WHERE acct.holder.name LIKE :ownerName"
        ),
        @NamedQuery(
                name = "findAccountByName",
                query = "SELECT acct FROM Account acct WHERE acct.holder.name LIKE :ownerName",
                lockMode = LockModeType.OPTIMISTIC
        ),
        @NamedQuery(
                name = "findAllAccounts",
                query = "SELECT acct FROM Account",
                lockMode = LockModeType.OPTIMISTIC
        )
})

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

    public Account() {
        this(null, 0);
    }

    public Account(Holder holder) {
        this(holder, 0);
    }

    public Account(Holder holder, int balance) {
        this.holder = holder;
        this.balance = balance;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) throws RejectedException {
        if (amount < 0) {
            throw new RejectedException(
                    "Tried to deposit negative value, illegal value: " + amount + "." + accountInfo());
        }
        balance += amount;
    }

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

    @Override
    public String getHolderName() {
        return holder.getName();
    }
}