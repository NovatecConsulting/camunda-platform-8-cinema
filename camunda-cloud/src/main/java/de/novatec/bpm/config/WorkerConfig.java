package de.novatec.bpm.config;

import de.novatec.bpm.service.PaymentService;
import de.novatec.bpm.service.SeatService;
import de.novatec.bpm.service.TicketService;
import de.novatec.bpm.worker.MoneyWorker;
import de.novatec.bpm.worker.SeatWorker;
import de.novatec.bpm.worker.TicketWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkerConfig {

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public MoneyWorker moneyWorker(PaymentService paymentService) {
        return new MoneyWorker(paymentService);
    }

    @Bean
    public SeatWorker seatWorker(SeatService seatService, TicketService ticketService) {
        return new SeatWorker(seatService, ticketService, port);
    }

    @Bean
    public TicketWorker ticketWorker(TicketService ticketService) {
        return new TicketWorker(ticketService);
    }

}
