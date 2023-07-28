package de.novatec.bpm.worker;

import de.novatec.bpm.exception.PaymentException;
import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.service.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.novatec.bpm.process.ProcessVariableHandler.getReservation;

public class MoneyWorker extends AbstractWorker {

    private final Logger logger = LoggerFactory.getLogger(MoneyWorker.class);
    private final PaymentService paymentService;

    public MoneyWorker(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @JobWorker(type = "get-money")
    public Reservation getMoney(final JobClient client, final ActivatedJob job, @VariablesAsType Reservation reservation) {
        logger.info("withdrawing money");
        if (reservation != null) {
            try {
                paymentService.issueMoney(reservation.getPrice(), "DE12345678901234", "VOBA123456XX");
                reservation.setTransactionSuccessful(true);
                return reservation;
            } catch (PaymentException e) {
                String ERROR_CODE = "Transaction_Error";
                throwError(client, job, ERROR_CODE, e.getMessage());
            }
        } else {
            failJob(client, job, "no reservation set");
        }
        return reservation;
    }
}