package se.kth.id1212.db.bankjpa.server.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity(name = "Holder")
public class Holder implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long accountId;

    @Column(name = "name", nullable = false)
    private String name;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    public Holder() {
        this(null);
    }

    public Holder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}