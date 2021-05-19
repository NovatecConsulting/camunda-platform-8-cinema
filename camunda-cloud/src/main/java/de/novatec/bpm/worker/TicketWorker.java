package de.novatec.bpm.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.model.Ticket;
import de.novatec.bpm.service.TicketService;
import de.novatec.bpm.variable.ProcessVariables;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.novatec.bpm.variables.ProcessVariableHandler.*;

public class TicketWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;

    public TicketWorker(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @ZeebeWorker(type = "generate-ticket")
    public void generateTicket(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        logger.info("generating ticket");

        Ticket ticket = ticketService.generateTickets(getUser(job), "Sitze: " + String.join(", ", getSeats(job)), getReservationId(job));
        Map<String, Ticket> variables = Collections.singletonMap(ProcessVariables.TICKET.getName(), ticket);
        client.newCompleteCommand(job.getKey()).variables(variables).send().join();
    }

    private void fail(JobClient client, ActivatedJob job, String message) {
        client.newFailCommand(job.getKey()).retries(0).errorMessage(message).send();
    }
}