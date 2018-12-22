package frontend;

import backend.accounts.AccountsManager;
import backend.audit.Auditor;
import backend.transitsystem.BusStop;
import backend.transitsystem.RoutesManager;
import backend.transitsystem.StationManager;

import java.util.ArrayList;

public class MainSystem {
    private static StationManager stationManager = new StationManager();
    private static AccountsManager accountsManager = new AccountsManager();
    //private static Auditor auditor = new Auditor();
    private static RoutesManager routesManager = new RoutesManager();


    public static AccountsManager getAccountsManager() {
        System.out.println(accountsManager);
        return accountsManager;
    }

    //public static Auditor getAuditor() {
    //    return auditor;
    //}

    public static  RoutesManager getRoutesManager(){
        return routesManager;
    }

    public static StationManager getStationManager() {
        return stationManager;
    }
}
