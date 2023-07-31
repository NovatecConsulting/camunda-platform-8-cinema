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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TicketWorker extends AbstractWorker {

    Logger logger = LoggerFactory.getLogger(TicketWorker.class);

    private final TicketService ticketService;
    private final QRCodeService qrCodeService;

    public TicketWorker(TicketService ticketService, QRCodeService qrCodeService) {
        this.ticketService = ticketService;
        this.qrCodeService = qrCodeService;
    }

    @JobWorker(type = "generate-ticket")
    public Map<String, Object> generateTicket(@Variable String userId, @Variable List<String> seats) throws IOException {
        logger.info("generating ticket");
        Ticket ticket = ticketService.generateTickets(userId, seats);
        File file = qrCodeService.generateQRCode(ticket.getCode());
        logger.info("Ticket generated: {}", ticket.getInfo());
        return Map.of(ProcessVariables.TICKET.getName(), ticket, "qrCode", file);
    }

    @JobWorker(type = "send-ticket", fetchAllVariables = true)
    public void sendTicket(final JobClient client, final ActivatedJob job, @Variable Ticket ticket) {
        logger.info("sending ticket {} to customer", ticket.getCode());
        logger.info(ticket.getInfo());
    }
}