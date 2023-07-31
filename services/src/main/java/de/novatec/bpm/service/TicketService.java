package de.novatec.bpm.service;

import de.novatec.bpm.model.Ticket;

import java.util.List;
import java.util.UUID;

public class TicketService {

    public static final long TICKET_PRICE = 12L;

    public Ticket generateTickets(String name, List<String> seats) {
        String code = UUID.randomUUID().toString();
        String info = Ticket.createInfo(name, seats, code);

        return new Ticket(code, info);
    }

    public long getTicketPrice(List<String> seats) {
        return seats.size() * TICKET_PRICE;
    }

}
