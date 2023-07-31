package de.novatec.bpm.service;

import de.novatec.bpm.model.Ticket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TicketService {

    public static final long TICKET_PRICE = 12L;


    private final QRCodeService qrCodeService;

    public TicketService(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    public Ticket generateTickets() throws IOException {
        String code = UUID.randomUUID().toString();
        try (FileInputStream input = new FileInputStream(qrCodeService.generateQRCode(code))) {
            return new Ticket(code, input.readAllBytes());
        }
    }

    public long getTicketPrice(List<String> seats) {
        return seats.size() * TICKET_PRICE;
    }

    public String generateMail(String name, List<String> seats, String ticketId) {
        String movieStart = new SimpleDateFormat("dd.MM.yy").format(new Date());
        String now = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
        return String.format("""
                        Hello there %s,
                        Thanks for your reservation at %s.
                        Here are all the details you need:
                        Movie: Not another BPM movie, %s 21:00 Uhr
                        Seats: %s
                        Ticket id: %s
                        Have fun at the movies

                        Your Novatec Cinema Team!""",
                name, now, movieStart, String.join(", ", seats), ticketId);
    }

}
