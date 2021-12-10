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

public class MoneyWorker extends AbstractWorker {

    private final String ERROR_CODE = "Transaction_Error";
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
                completeJob(client, job);
            } catch (PaymentException e) {
                throwError(client, job, ERROR_CODE, e.getMessage());
            }
        } else {
            failJob(client, job, "no reservation set");
        }
    }
}