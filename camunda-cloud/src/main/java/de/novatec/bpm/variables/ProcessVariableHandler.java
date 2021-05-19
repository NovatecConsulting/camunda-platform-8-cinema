package de.novatec.bpm.variables;

import de.novatec.bpm.model.Reservation;
import io.camunda.zeebe.client.api.response.ActivatedJob;

import java.util.List;

public class ProcessVariableHandler {

    public static Reservation getReservation(ActivatedJob job) {
        return job.getVariablesAsType(Reservation.class);
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
