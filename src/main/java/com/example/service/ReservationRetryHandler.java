package com.example.service;

import com.example.model.Reservation;
import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

public class ReservationRetryHandler {

    private final ReservationService service;
    private final int maxRetries;

    public ReservationRetryHandler(ReservationService service, int maxRetries) {
        this.service = service;
        this.maxRetries = maxRetries;
    }

    public Reservation modifierAvecRetry(Long id, String nouvelleSalle, LocalDateTime debut, LocalDateTime fin) {
        int tentatives = 0;
        while (tentatives < maxRetries) {
            try {
                tentatives++;
                System.out.println("[RETRY] Tentative " + tentatives + "/" + maxRetries + " pour mise à jour réservation " + id);
                Reservation r = service.trouverParId(id);
                if (r == null) {
                    throw new IllegalArgumentException("Réservation introuvable id=" + id);
                }
                r.setSalle(nouvelleSalle);
                r.setDateDebut(debut);
                r.setDateFin(fin);
                Reservation resultat = service.modifierReservation(r);
                System.out.println("[RETRY] Succès à la tentative " + tentatives + "! Version actuelle: " + resultat.getVersion());
                return resultat;
            } catch (OptimisticLockException e) {
                System.out.println("[RETRY] OptimisticLockException détectée à la tentative " + tentatives + ".");
                if (tentatives >= maxRetries) {
                    throw e;
                }
                try {
                    Thread.sleep(50); // Pause avant retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IllegalStateException("Échec de la mise à jour après " + maxRetries + " tentatives.");
    }
}
