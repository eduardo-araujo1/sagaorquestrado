package com.eduardo.orchestratorservice.core.service;

import com.eduardo.orchestratorservice.core.dto.Event;
import com.eduardo.orchestratorservice.core.dto.History;
import com.eduardo.orchestratorservice.core.enums.EEventSource;
import com.eduardo.orchestratorservice.core.enums.ESagaStatus;
import com.eduardo.orchestratorservice.core.enums.ETopics;
import com.eduardo.orchestratorservice.core.producer.SagaOrchestratorProducer;
import com.eduardo.orchestratorservice.core.saga.SagaExecutionController;
import com.eduardo.orchestratorservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.eduardo.orchestratorservice.core.enums.EEventSource.ORCHESTRATOR;
import static com.eduardo.orchestratorservice.core.enums.ESagaStatus.FAIL;
import static com.eduardo.orchestratorservice.core.enums.ESagaStatus.SUCCESS;
import static com.eduardo.orchestratorservice.core.enums.ETopics.NOTIFY_ENDING;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestrationService {

    private final SagaOrchestratorProducer producer;
    private final JsonUtil jsonUtil;
    private final SagaExecutionController sagaExecutionController;

    public void startSaga(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED!");
        addHistory(event, "Saga started!");
        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished successfully!");
        notifyFinishedSaga(event);
    }
    public void finishSagaFail(Event event){
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAIL);
        log.info("SAGA FINISHED WITH ERRORS FOR EVENT {}", event.getId());
        addHistory(event, "Saga finished with errors!");
        notifyFinishedSaga(event);
    }
    public void continueSaga(Event event){
        var topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducerWithTopic(event, topic);
    }

    private ETopics getTopic(Event event){
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(Event event, String message) {
        var history = History
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void notifyFinishedSaga(Event event){
        producer.sendEvent(jsonUtil.toJson(event), NOTIFY_ENDING.getTopic());
    }

    private void sendToProducerWithTopic(Event event, ETopics topic){
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }
}
