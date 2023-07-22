package de.novatec.bpm.worker;

import de.novatec.bpm.model.Ticket;
import de.novatec.bpm.process.ProcessVariables;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
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
    public void generateTicket(final JobClient client, final ActivatedJob job) {
        logger.info("generating ticket");
        Ticket ticket = ticketService.generateTickets(getReservation(job));
        Map<String, Object> variables = Collections.singletonMap(ProcessVariables.TICKET.getName(), ticket);
        completeJob(client, job, variables);
    }

    @JobWorker(type = "send-ticket")
    public void sendTicket(final JobClient client, final ActivatedJob job) {
        Ticket ticket = getTicket(job);
        logger.info("sending ticket {} to customer", ticket.getCode());
        completeJob(client, job);
        logger.info(ticket.getInfo());
    }
}