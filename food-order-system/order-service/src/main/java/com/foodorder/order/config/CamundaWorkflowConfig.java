package com.foodorder.order.config;

import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.configuration.Ordering;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordering.DEFAULT_ORDER + 1)
public class CamundaWorkflowConfig extends AbstractCamundaConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CamundaWorkflowConfig.class);

    @Override
    public void preInit(SpringProcessEngineConfiguration processEngineConfiguration) {
        log.info("Configuring Camunda Process Engine with custom settings...");
        
        // Ensure database schema updates are enabled
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        
        // Set default history level to audit or full
        processEngineConfiguration.setHistory("audit");
    }
}
