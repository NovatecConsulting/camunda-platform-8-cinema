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
                client.newCompleteCommand(job.getKey()).variables(reservation).send().join();
            } catch (PaymentException e) {
                client.newThrowErrorCommand(job.getKey()).errorCode(ERROR_CODE).errorMessage(e.getMessage()).send().join();
            }
        } else {
            client.newFailCommand(job.getKey()).retries(0).errorMessage("no reservation set").send().join();
        }
    }
}