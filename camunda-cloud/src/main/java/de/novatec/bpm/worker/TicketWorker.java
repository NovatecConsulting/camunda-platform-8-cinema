package de.novatec.bpm.worker;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.model.Ticket;
import de.novatec.bpm.process.ProcessVariables;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

import static de.novatec.bpm.process.ProcessVariableHandler.getReservation;
import static de.novatec.bpm.process.ProcessVariableHandler.getTicket;

public class TicketWorker extends AbstractWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;

    public TicketWorker(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @JobWorker(type = "generate-ticket")
    public Map<String, Object> generateTicket(@VariablesAsType Reservation reservation) {
        logger.info("generating ticket");
        Ticket ticket = ticketService.generateTickets(reservation);
        logger.info("Ticket generated: {}", ticket.getInfo());
        return Collections.singletonMap(ProcessVariables.TICKET.getName(), ticket);
    }

    @JobWorker(type = "send-ticket", fetchAllVariables = true)
    public void sendTicket(final JobClient client, final ActivatedJob job, @Variable Ticket ticket) {
        logger.info("sending ticket {} to customer", ticket.getCode());
        logger.info(ticket.getInfo());
    }
}