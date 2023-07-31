package de.novatec.bpm.worker;

import de.novatec.bpm.exception.PaymentException;
import de.novatec.bpm.service.PaymentService;
import de.novatec.bpm.service.TicketService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class MoneyWorker extends AbstractWorker {

    private final Logger logger = LoggerFactory.getLogger(MoneyWorker.class);
    private final PaymentService paymentService;

    private final TicketService ticketService;

    public MoneyWorker(PaymentService paymentService, TicketService ticketService) {
        this.paymentService = paymentService;
        this.ticketService = ticketService;
    }

    @JobWorker(type = "get-money")
    public Map<String, Object> getMoney(final JobClient client, final ActivatedJob job, @Variable List<String> seats) {
        logger.info("withdrawing money");
        try {
            long ticketPrice = ticketService.getTicketPrice(seats);
            paymentService.issueMoney(ticketPrice, "DE12345678901234", "VOBA123456XX");
            return Map.of("transactionSuccessful", true);
        } catch (PaymentException e) {
            String ERROR_CODE = "Transaction_Error";
            throwError(client, job, ERROR_CODE, e.getMessage());
            return Map.of("transactionSuccessful", false);
        }
    }
}