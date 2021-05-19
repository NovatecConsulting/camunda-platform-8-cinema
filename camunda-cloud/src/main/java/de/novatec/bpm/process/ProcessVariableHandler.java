package de.novatec.bpm.process;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.model.Ticket;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.List;

public class ProcessVariableHandler {

    public static Reservation getReservation(ActivatedJob job) {
        return job.getVariablesAsType(Reservation.class);
    }

    public static Ticket getTicket(ActivatedJob job) {
        return job.getVariablesAsType(Ticket.class);
    }

    public static List<String> getSeats(ActivatedJob job) {
        Reservation reservation = getReservation(job);
        return reservation.getSeats();
    }

    public static String getUser(ActivatedJob job) {
        Reservation reservation = getReservation(job);
        return reservation.getUserId();
    }

    public static String getReservationId(ActivatedJob job) {
        Reservation reservation = getReservation(job);
        return reservation.getReservationId();
    }

}
