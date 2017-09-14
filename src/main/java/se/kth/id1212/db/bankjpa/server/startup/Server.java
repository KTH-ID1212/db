package se.kth.id1212.db.bankjpa.server.startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import se.kth.id1212.db.bankjpa.common.Bank;
import se.kth.id1212.db.bankjpa.server.controller.Controller;

/**
 * Starts the bank server.
 */
public class Server {
    private static final String USAGE = "java bankjpa.Server [bank name in rmi registry]";
    private String bankName = Bank.BANK_NAME_IN_REGISTRY;

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.parseCommandLineArgs(args);
            server.startRMIServant();
            System.out.println("Bank server started.");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Failed to start bank server.");
        }
    }

    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller();
        Naming.rebind(bankName, contr);
    }

    private void parseCommandLineArgs(String[] args) {
        if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        if (args.length > 0) {
            bankName = args[0];
        }
    }
}
