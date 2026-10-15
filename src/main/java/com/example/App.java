package com.example;

public class App {
    public static void main(String[] args) {
        System.out.println("############################################################");
        System.out.println("#  TP 6 : DÉMONSTRATION OPTIMISTIC LOCKING AVEC @Version   #");
        System.out.println("############################################################\n");

        System.out.println("--- Étape 1 : Simulation du Conflit concurrent ---");
        ConcurrentReservationSimulator.main(args);

        System.out.println("\n--- Étape 2 : Simulation avec Gestion et Retry ---");
        ConcurrentReservationWithRetry.main(args);
    }
}
