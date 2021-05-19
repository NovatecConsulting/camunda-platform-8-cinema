package de.novatec.bpm.worker;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.service.SeatService;
import de.novatec.bpm.process.ProcessVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.novatec.bpm.process.ProcessVariableHandler.getReservation;
import static de.novatec.bpm.process.ProcessVariableHandler.getSeats;

public class SeatWorker {

    private final Logger logger = LoggerFactory.getLogger(SeatWorker.class);
    private final SeatService seatService;

    public SeatWorker(SeatService seatService) {
        this.seatService = seatService;
    }

    @Value("${server.port:8080}")
    private int port;

    @ZeebeWorker(type = "check-seats")
    public void checkSeats(final JobClient client, final ActivatedJob job) {
        logger.info("checking seat availability");
        List<String> seats = getSeats(job);
        if (seats != null && !seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            Map<String, Boolean> variables = Collections.singletonMap(ProcessVariables.SEATS_AVAILABLE.getName(), available);
            client.newCompleteCommand(job.getKey()).variables(variables).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    @ZeebeWorker(type = "reserve-seats")
    public void reserveSeats(final JobClient client, final ActivatedJob job) {
        logger.info("reserving seats");
        List<String> seats = getSeats(job);
        if (seats != null && !seats.isEmpty()) {
            seatService.reserveSeats(seats);
            client.newCompleteCommand(job.getKey()).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    @ZeebeWorker(type = "alt-seats")
    public void alternativeSeats(final JobClient client, final ActivatedJob job) {
        logger.info("getting alternative seats");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(reservation.getSeats());
            reservation.setSeats(alternativeSeats);
            offerAltSeats(alternativeSeats, reservation.getReservationId());
            client.newCompleteCommand(job.getKey()).variables(reservation).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    @ZeebeWorker(type = "release-seats")
    public void releaseSeats(final JobClient client, final ActivatedJob job) {
        logger.info("releasing seats");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            seatService.releaseSeats(reservation.getSeats());
            client.newCompleteCommand(job.getKey()).send().join();
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no seats found").send();
        }
    }

    public void offerAltSeats(List<String> seats, String reservationId) {
        logger.info("The seats you selected are not available. Alternative seats are {}", seats);
        logger.info("To accept these seats, click the following link: http://localhost:{}/offer/{}", port, reservationId);
    }
}
