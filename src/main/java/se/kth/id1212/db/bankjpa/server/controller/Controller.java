package se.kth.id1212.db.bankjpa.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import se.kth.id1212.db.bankjpa.common.AccountDTO;
import se.kth.id1212.db.bankjpa.server.model.AccountException;
import se.kth.id1212.db.bankjpa.common.Bank;
import se.kth.id1212.db.bankjpa.server.integration.BankDAO;
import se.kth.id1212.db.bankjpa.server.model.Account;
import se.kth.id1212.db.bankjpa.server.model.Holder;
import se.kth.id1212.db.bankjpa.server.model.RejectedException;

/**
 * Implementations of the bank's remote methods, this is the only server class that can be called
 * remotely
 */
public class Controller extends UnicastRemoteObject implements Bank {
    private final BankDAO bankDb;

    public Controller() throws RemoteException {
        super();
        bankDb = new BankDAO();
    }

    @Override
    public List<? extends AccountDTO> listAccounts() throws AccountException {
        try {
            return bankDb.findAllAccounts();
        } catch (Exception e) {
            throw new AccountException("Unable to list accounts.", e);
        }
    }

    @Override
    public void createAccount(String holderName) throws AccountException {
        try {
            if (bankDb.findAccountByName(holderName, true) != null) {
                throw new AccountException("Account for: " + holderName + " already exists");
            }
            bankDb.createAccount(new Account(new Holder(holderName)));
        } catch (Exception e) {
            throw new AccountException("Could not create account for: " + holderName, e);
        }
    }

    @Override
    public AccountDTO getAccount(String holderName) throws AccountException {
        try {
            return bankDb.findAccountByName(holderName, true);
        } catch (Exception e) {
            throw new AccountException("Could not search for account.", e);
        }
    }

    @Override
    public void deleteAccount(String holderName) throws AccountException {
        try {
            Account account = bankDb.findAccountByName(holderName, false);
            bankDb.deleteAccount(account);
        } catch (Exception e) {
            throw new AccountException("Could not delete account for: " + holderName, e);
        }
    }

    @Override
    public void deposit(AccountDTO acctDTO, int amt) throws RejectedException, AccountException {
        Account acct = null;
        try {
            acct = bankDb.findAccountByName(acctDTO.getHolderName(), false);
            acct.deposit(amt);
            bankDb.updateAccount(acct);
        } catch (Exception e) {
            throw new AccountException("Could not deposit to account: " + acct, e);
        }
    }

    @Override
    public void withdraw(AccountDTO acctDTO, int amt) throws RejectedException, AccountException {
        Account acct = null;
        try {
            acct = bankDb.findAccountByName(acctDTO.getHolderName(), false);
            acct.withdraw(amt);
            bankDb.updateAccount(acct);
        } catch (Exception e) {
            throw new AccountException("Could not withdraw from account: " + acct, e);
        }
    }
}
