package de.novatec.bpm.worker;

import de.novatec.bpm.exception.PaymentException;
import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.service.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.novatec.bpm.process.ProcessVariableHandler.getReservation;

public class MoneyWorker {

    private final Logger logger = LoggerFactory.getLogger(MoneyWorker.class);
    private final PaymentService paymentService;

    public MoneyWorker(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ZeebeWorker(type = "get-money")
    public void getMoney(final JobClient client, final ActivatedJob job) {
        logger.info("withdrawing money");
        Reservation reservation = getReservation(job);
        if (reservation != null) {
            try {
                paymentService.issueMoney(reservation.getPrice(), "DE12345678901234", "VOBA123456XX");
                reservation.setTransactionSuccessful(true);
            } catch (PaymentException e) {
                fail(client, job, e.getMessage());
            }
            client.newCompleteCommand(job.getKey()).variables(reservation).send().join();
        } else {
            fail(client, job, "error issuing money");
        }
    }

    private void fail(JobClient client, ActivatedJob job, String message) {
        client.newFailCommand(job.getKey()).retries(0).errorMessage(message).send();
    }
}