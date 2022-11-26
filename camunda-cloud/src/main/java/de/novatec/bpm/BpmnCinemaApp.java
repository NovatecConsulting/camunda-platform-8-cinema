package de.novatec.bpm;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:reserve-tickets.bpmn")
public class BpmnCinemaApp {

    public static void main(String[] args) {
        SpringApplication.run(BpmnCinemaApp.class, args);
    }

}
