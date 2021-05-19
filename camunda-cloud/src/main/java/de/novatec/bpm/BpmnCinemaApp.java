package de.novatec.bpm;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(classPathResources = {"reserve-tickets.bpmn"})
public class BpmnCinemaApp {

    public static void main(String[] args) {
        SpringApplication.run(BpmnCinemaApp.class, args);
    }

}
