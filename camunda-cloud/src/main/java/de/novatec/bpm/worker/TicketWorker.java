package de.novatec.bpm.worker;

import de.novatec.bpm.model.Ticket;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.novatec.bpm.process.ProcessVariableHandler.*;

public class TicketWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;

    public TicketWorker(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @ZeebeWorker(type = "generate-ticket")
    public void generateTicket(final JobClient client, final ActivatedJob job) {
        logger.info("generating ticket");
        Ticket ticket = ticketService.generateTickets(getUser(job), getReservation(job).toString(), getReservationId(job));
        client.newCompleteCommand(job.getKey()).variables(ticket).send().join();
    }
}