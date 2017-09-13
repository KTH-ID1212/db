package se.kth.id1212.db.bankjpa.common;

import java.io.Serializable;
import se.kth.id1212.db.bankjpa.server.model.Holder;

/**
 * Specifies a read-only view of n account.
 */
public interface AccountDTO extends Serializable {
    /**
     * @return The balance.
     */
    public int getBalance();

    /**
     * @return The holder's name.
     */
    public String getHolderName();
}
