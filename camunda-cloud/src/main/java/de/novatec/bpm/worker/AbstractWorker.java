package de.novatec.bpm.worker;

import de.novatec.bpm.model.Reservation;
import de.novatec.bpm.model.Ticket;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;

import java.util.Map;

public class AbstractWorker {

    protected void completeJob(JobClient client, ActivatedJob job) {
        client.newCompleteCommand(job.getKey())
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not complete job " + job, throwable);
                        }
                );
    }

    protected void completeJob(JobClient client, ActivatedJob job, Reservation reservation) {
        client.newCompleteCommand(job.getKey())
                .variables(reservation)
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not complete job " + job, throwable);
                        }
                );
    }

    protected void completeJob(JobClient client, ActivatedJob job, Ticket ticket) {
        client.newCompleteCommand(job.getKey())
                .variables(ticket)
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not complete job " + job, throwable);
                        }
                );
    }

    protected void completeJob(JobClient client, ActivatedJob job, Map<String, Object> variables) {
        client.newCompleteCommand(job.getKey())
                .variables(variables)
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not complete job " + job, throwable);
                        }
                );
    }

    protected void failJob(JobClient client, ActivatedJob job, String errorMsg) {
        client.newFailCommand(job.getKey())
                .retries(0)
                .errorMessage(errorMsg)
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not fail job " + job, throwable);
                        }
                );
    }

    protected void throwError(JobClient client, ActivatedJob job, String errorCode, String message) {
        client.newThrowErrorCommand(job.getKey())
                .errorCode(errorCode)
                .errorMessage(message)
                .send()
                .exceptionally(
                        throwable -> {
                            throw new RuntimeException("Could not throw error command for job " + job, throwable);
                        }
                );
    }
}
