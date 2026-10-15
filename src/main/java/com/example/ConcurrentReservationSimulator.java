package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationService;
import com.example.service.ReservationServiceImpl;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ConcurrentReservationSimulator {

    public static void main(String[] args) {
        ReservationServiceImpl service = new ReservationServiceImpl();
        try {
            System.out.println("=== 1. CRÉATION D'UNE RÉSERVATION INITIALE ===");
            Reservation initial = new Reservation("Salle A", LocalDateTime.of(2026, 5, 16, 10, 6), LocalDateTime.of(2026, 6, 1, 11, 0));
            initial = service.creerReservation(initial);
            System.out.println("Créée : " + initial + " | Version: " + initial.getVersion());

            System.out.println("\n=== 2. LECTURE PAR DEUX UTILISATEURS CONCURRENTS (TX1 et TX2) ===");
            Reservation user1 = service.trouverParId(initial.getId());
            Reservation user2 = service.trouverParId(initial.getId());

            System.out.println("User 1 lit : version " + user1.getVersion());
            System.out.println("User 2 lit : version " + user2.getVersion());

            System.out.println("\n=== 3. USER 1 MODIFIE ET COMMITE ===");
            user1.setSalle("Salle A - Modifiée par User 1");
            user1 = service.modifierReservation(user1);
            System.out.println("User 1 a validé : " + user1 + " | Nouvelle Version: " + user1.getVersion());

            System.out.println("\n=== 4. USER 2 TENTE DE MODIFIER AVEC L'ANCIENNE VERSION ===");
            user2.setSalle("Salle B - Tentative par User 2");
            try {
                service.modifierReservation(user2);
                System.out.println("ERREUR : La mise à jour aurait dû échouer !");
            } catch (OptimisticLockException e) {
                System.out.println("SUCCÈS DE LA DÉTECTION CONCURRENTE !");
                System.out.println("Exception levée avec succès : " + e.getClass().getName() + " -> " + e.getMessage());
            }
        } finally {
            service.close();
        }
    }
}
