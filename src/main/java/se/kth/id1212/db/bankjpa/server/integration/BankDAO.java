package se.kth.id1212.db.bankjpa.server.integration;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import se.kth.id1212.db.bankjdbc.server.integration.BankDBException;
import se.kth.id1212.db.bankjpa.server.model.Account;
import se.kth.id1212.db.bankjpa.common.AccountDTO;

/**
 * This data access object (DAO) encapsulates all database calls in the bank application. No code
 * outside this class shall have any knowledge about the database.
 */
public class BankDAO {
    private EntityManagerFactory emFactory;

    /**
     * Constructs a new DAO object connected to the specified database.
     *
     * @param dbms       Database management system vendor. Currently supported types are "derby"
     *                   and "mysql".
     * @param datasource Database name.
     */
    public BankDAO() {
        emFactory = Persistence.createEntityManagerFactory("bankPersistenceUnit");
    }

    /**
     * Searches for an account whose holder has the specified name.
     *
     * @param holderName The account holder's name
     * @return The account whose holder has the specified name, or <code>null</code> if there is no
     *         such account.
     * @throws BankDBException If failed to search for account.
     */
    public AccountDTO findAccountByName(String holderName) {
        if (holderName == null) {
            return null;
        }

        EntityManager em = null;
        try {
            em = beginTransaction();
            try {
                return (Account) em.createNamedQuery("findAccountByName").
                        setParameter("ownerName", holderName).getSingleResult();
            } catch (NoResultException noSuchAccount) {
                return null;
            }
        } finally {
            commitTransaction(em);
        }
    }

    /**
     * Retrieves all existing accounts.
     *
     * @return A list with all existing accounts. The list is empty if there are no accounts.
     * @throws BankDBException If failed to search for account.
     */
    public List<AccountDTO> findAllAccounts() {
        EntityManager em = null;
        try {
            em = beginTransaction();
            try {
                return em.createNamedQuery("findAllAccounts").getResultList();
            } catch (NoResultException noSuchAccount) {
                return null;
            }
        } finally {
            commitTransaction(em);
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to create.
     * @throws BankDBException If failed to create the specified account.
     */
    public void createAccount(AccountDTO account) {
        EntityManager em = null;
        try {
            em = beginTransaction();
            em.persist(account);
        } finally {
            commitTransaction(em);
        }
    }

    /**
     * Deletes the specified account.
     *
     * @param account The account to delete.
     * @return <code>true</code> if the specified holder had an account and it was deleted,
     *         <code>false</code> if the holder did not have an account and nothing was done.
     * @throws BankDBException If unable to delete the specified account.
     */
    public void deleteAccount(AccountDTO account) {
        EntityManager em = null;
        try {
            em = beginTransaction();
            em.remove(account);
        } finally {
            commitTransaction(em);
        }
    }

    /**
     * Starts a new transaction.
     *
     * @return The <code>EntityManager</code> used in the newly started transaction.
     */
    public EntityManager beginTransaction() {
        EntityManager em = emFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        return em;
    }

    /**
     * Commits a transaction.
     *
     * @param The <code>EntityManager</code> used in the transaction that shall be committed.
     */
    public void commitTransaction(EntityManager em) {
        em.getTransaction().commit();
    }
}
