package se.kth.id1212.db.bankjpa.server.integration;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import se.kth.id1212.db.bankjpa.server.model.Account;
import se.kth.id1212.db.bankjpa.common.AccountDTO;

/**
 * This data access object (DAO) encapsulates all database calls in the bank application. No code
 * outside this class shall have any knowledge about the database.
 */
public class BankDAO {
    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    /**
     * Constructs a new DAO object connected to the specified database.
     */
    public BankDAO() {
        emFactory = Persistence.createEntityManagerFactory("bankPersistenceUnit");
    }

    /**
     * Searches for an account whose holder has the specified name.
     *
     * @param holderName                   The account holder's name
     * @param endTransactionAfterSearching Whether the transaction should commit after searching for
     *                                     an account with the specified holder. Set to
     *                                     <code>true</code> if no further action is taken on the
     *                                     account. Set to <code>false</code> if more operations,
     *                                     for example deposit or withdraw, are to be performed on
     *                                     the found account.
     * @return The account whose holder has the specified name, or <code>null</code> if there is no
     *         such account.
     */
    public Account findAccountByName(String holderName, boolean endTransactionAfterSearching) {
        if (holderName == null) {
            return null;
        }

        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findAccountByName", Account.class).
                        setParameter("holderName", holderName).getSingleResult();
            } catch (NoResultException noSuchAccount) {
                return null;
            }
        } finally {
            if (endTransactionAfterSearching) {
                commitTransaction();
            }
        }
    }

    /**
     * Retrieves all existing accounts.
     *
     * @return A list with all existing accounts. The list is empty if there are no accounts.
     */
    public List<Account> findAllAccounts() {
        try {
            EntityManager em = beginTransaction();
            try {
                return em.createNamedQuery("findAllAccounts", Account.class).getResultList();
            } catch (NoResultException noSuchAccount) {
                return new ArrayList<>();
            }
        } finally {
            commitTransaction();
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to create.
     */
    public void createAccount(AccountDTO account) {
        try {
            EntityManager em = beginTransaction();
            em.persist(account);
        } finally {
            commitTransaction();
        }
    }

    /**
     * Stores the state of the specified.
     *
     * @param account The account to update. All fields are stored in the database.
     */
    public void updateAccount(AccountDTO account) {
        commitTransaction();
    }

    /**
     * Deletes the specified account.
     *
     * @param account The account to delete.
     * @return <code>true</code> if the specified holder had an account and it was deleted,
     *         <code>false</code> if the holder did not have an account and nothing was done.
     */
    public void deleteAccount(AccountDTO account) {
        try {
            EntityManager em = beginTransaction();
            em.remove(em.merge(account));
        } finally {
            commitTransaction();
        }
    }

    private EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }

    private void commitTransaction() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
}
