package de.novatec.bpm.worker;

import de.novatec.bpm.process.ProcessVariables;
import de.novatec.bpm.service.SeatService;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @JobWorker(type = "check-seats", autoComplete = false)
    public void checkSeats(final JobClient client, final ActivatedJob job, @Variable List<String> seats) {
        logger.info("checking seat availability");
        if (seats != null && !seats.isEmpty()) {
            boolean available = seatService.seatsAvailable(seats);
            completeJob(client, job, Map.of(ProcessVariables.SEATS_AVAILABLE.getName(), available));
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "reserve-seats", autoComplete = false)
    public void reserveSeats(final JobClient client, final ActivatedJob job, @Variable List<String> seats) {
        logger.info("reserving seats");
        if (seats != null && !seats.isEmpty()) {
            seatService.reserveSeats(seats);
            long ticketPrice = ticketService.getTicketPrice(seats);
            completeJob(client, job, Map.of(ProcessVariables.TICKET_PRICE.getName(), ticketPrice));
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "alt-seats", autoComplete = false)
    public void alternativeSeats(final JobClient client, final ActivatedJob job, @Variable String reservationId, @Variable List<String> seats) {
        logger.info("getting alternative seats");
        if (seats != null && !seats.isEmpty()) {
            List<String> alternativeSeats = seatService.getAlternativeSeats(seats);
            offerAltSeats(alternativeSeats, reservationId);
            completeJob(client, job, Map.of(ProcessVariables.SEATS.getName(), alternativeSeats));
        } else {
            failJob(client, job, "no seats found");
        }
    }

    @JobWorker(type = "release-seats", autoComplete = false)
    public void releaseSeats(final JobClient client, final ActivatedJob job, @Variable List<String> seats) {
        logger.info("releasing seats");
        if (seats != null && !seats.isEmpty()) {
            seatService.releaseSeats(seats);
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
