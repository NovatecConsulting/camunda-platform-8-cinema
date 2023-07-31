package de.novatec.bpm.config;

import de.novatec.bpm.service.PaymentService;
import de.novatec.bpm.service.QRCodeService;
import de.novatec.bpm.service.SeatService;
import de.novatec.bpm.service.TicketService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Value("${cinema.payment.service.error:false}")
    public boolean throwErrors;

    @Bean
    public PaymentService paymentService() {
        return new PaymentService(throwErrors);
    }

    @Bean
    public SeatService seatService() {
        return new SeatService();
    }

    @Bean
    public TicketService ticketService() {
        return new TicketService();
    }

    @Bean
    public QRCodeService qrCodeService() {
        return new QRCodeService();
    }
}
