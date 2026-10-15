package com.example;

import com.example.model.Reservation;
import com.example.service.ReservationRetryHandler;
import com.example.service.ReservationServiceImpl;

import java.time.LocalDateTime;

public class ConcurrentReservationWithRetry {

    public static void main(String[] args) {
        ReservationServiceImpl service = new ReservationServiceImpl();
        try {
            System.out.println("=== DÉMONSTRATION RÉSOLUTION CONFLIT AVEC RETRY ===");
            Reservation res = new Reservation("Salle Réunion 1", LocalDateTime.of(2026, 6, 2, 14, 0), LocalDateTime.of(2026, 6, 2, 16, 0));
            res = service.creerReservation(res);

            ReservationRetryHandler retryHandler = new ReservationRetryHandler(service, 3);
            Reservation updated = retryHandler.modifierAvecRetry(res.getId(), "Salle Réunion 1 - Mise à jour",
                    LocalDateTime.of(2026, 6, 2, 15, 30), LocalDateTime.of(2026, 6, 2, 17, 30));

            System.out.println("Réservation finale validée : " + updated);
        } finally {
            service.close();
        }
    }
}
