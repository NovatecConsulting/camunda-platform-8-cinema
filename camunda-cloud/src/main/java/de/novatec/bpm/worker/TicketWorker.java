package de.novatec.bpm.worker;

import de.novatec.bpm.model.Ticket;
import de.novatec.bpm.process.ProcessVariables;
import de.novatec.bpm.service.QRCodeService;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TicketWorker extends AbstractWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;

    public TicketWorker(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @JobWorker(type = "generate-ticket")
    public Map<String, Object> generateTicket() throws IOException {
        logger.info("generating ticket");
        Ticket ticket = ticketService.generateTickets();
        logger.info("Ticket generated: {}", ticket);
        return Map.of(ProcessVariables.TICKET.getName(), ticket);
    }

    @JobWorker(type = "send-ticket")
    public void sendTicket(@Variable Ticket ticket, @Variable String name, @Variable List<String> seats) {
        logger.info("sending ticket {} to customer", ticket.getCode());
        logger.info(ticketService.generateMail(name, seats, ticket.getCode()));
    }
}