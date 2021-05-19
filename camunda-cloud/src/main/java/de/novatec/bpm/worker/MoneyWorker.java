package de.novatec.bpm.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.novatec.bpm.exception.PaymentException;
import de.novatec.bpm.service.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.novatec.bpm.variables.ProcessVariableHandler.getSeats;

public class MoneyWorker {

    public static final int TICKET_PRICE = 12;
    Logger logger = LoggerFactory.getLogger(MoneyWorker.class);

    private final PaymentService paymentService;

    public MoneyWorker(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ZeebeWorker(type = "get-money")
    public void getMoney(final JobClient client, final ActivatedJob job) throws JsonProcessingException {
        logger.info("withdrawing money");
        List<String> seats = getSeats(job);
        if (seats != null) {
            try {
                paymentService.issueMoney(seats.size() * TICKET_PRICE, "DE12345678901234", "VOBA123456XX");
            } catch (PaymentException e) {
                fail(client, job, e.getMessage());
            }
            client.newCompleteCommand(job.getKey()).send().join();
        } else {
            fail(client, job, "error issuing money");
        }
    }

    private void fail(JobClient client, ActivatedJob job, String message) {
        client.newFailCommand(job.getKey()).retries(0).errorMessage(message).send();
    }
}