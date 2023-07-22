package de.novatec.bpm.worker;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.process.ProcessVariables;
import de.novatec.bpm.service.SeatService;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static de.novatec.bpm.process.ProcessVariableHandler.getReservation;
import static de.novatec.bpm.process.ProcessVariableHandler.getSeats;

public class SeatWorker extends AbstractWorker {

    private final Logger logger = LoggerFactory.getLogger(SeatWorker.class);
    private final SeatService seatService;
    private final TicketService ticketService;
    private final int port;

    public SeatWorker(SeatService seatService, TicketService ticketService, int port) {
        this.seatService = seatService;
        this.ticketService = ticketService;
        this.port = port;
    }

    @JobWorker(type = "check-seats")
    public void checkSeats(final JobClient client, final ActivatedJob job) {
        logger.info("checking seat availability");
        List<String> seats = getSeats(job);
        if (seats != null && !seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            Map<String, Object> variables = Collections.singletonMap(ProcessVariables.SEATS_AVAILABLE.getName(), available);
            completeJob(client, job, variables);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "reserve-seats")
    public void reserveSeats(final JobClient client, final ActivatedJob job) {
        logger.info("reserving seats");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            seatService.reserveSeats(reservation.getSeats());
            long ticketPrice = ticketService.getTicketPrice(reservation);
            reservation.setPrice(ticketPrice);
            completeJob(client, job, reservation);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "alt-seats")
    public void alternativeSeats(final JobClient client, final ActivatedJob job) {
        logger.info("getting alternative seats");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(reservation.getSeats());
            reservation.setSeats(alternativeSeats);
            offerAltSeats(alternativeSeats, reservation.getReservationId());
            completeJob(client, job, reservation);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "release-seats")
    public void releaseSeats(final JobClient client, final ActivatedJob job) {
        logger.info("releasing seats");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            seatService.releaseSeats(reservation.getSeats());
            completeJob(client, job);
        } else {
            failJob(client, job, "no seats found");
        }
    }

    public void offerAltSeats(List<String> seats, String reservationId) {
        logger.info("The seats you selected are not available. Alternative seats are {}", seats);
        logger.info("To accept these seats, click the following link: http://localhost:{}/reservation/offer/{}", port, reservationId);
    }
}
