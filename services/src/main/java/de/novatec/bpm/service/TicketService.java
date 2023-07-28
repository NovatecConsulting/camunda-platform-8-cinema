package de.novatec.bpm.service;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.model.Ticket;

import java.util.UUID;

public class TicketService {

    public static final long TICKET_PRICE = 12L;

    public Ticket generateTickets(Reservation reservation) {
        String id = UUID.randomUUID().toString();
        String info = Ticket.createInfo(reservation.getUserId(), reservation.toString(), id);
        return new Ticket(id, info);
    }

    public long getTicketPrice(Reservation reservation) {
        return reservation.getSeats().size() * TICKET_PRICE;
    }

}
