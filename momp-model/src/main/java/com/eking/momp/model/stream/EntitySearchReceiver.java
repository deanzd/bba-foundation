package com.eking.momp.model.stream;

import com.eking.momp.model.service.EntitySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EntitySearchReceiver {

    private static final String UPDATE_ENTITY_INPUT = "update-entity-input";
    private static final String DELETE_ENTITY_INPUT = "delete-entity-input";
    private static final String UPDATE_MODEL_INPUT = "update-model-input";

    @Autowired
    private EntitySearchService entitySearchService;

    @StreamListener(UPDATE_ENTITY_INPUT)
    public void receiveUpdateEntityInput(String message) throws IOException {
        String[] strs = message.split(":");
        String index = strs[0];
        String id = strs[1];
        entitySearchService.update(index, id);
    }

    @StreamListener(DELETE_ENTITY_INPUT)
    public void receiveDeleteEntityInput(String message) throws IOException {
        String[] strs = message.split(":");
        String index = strs[0];
        String id = strs[1];
        entitySearchService.delete(index, id);
    }

    @StreamListener(UPDATE_MODEL_INPUT)
    public void receiveUpdateModelInput(String modelCode) throws IOException {
        entitySearchService.updateIndex(modelCode);
    }

    public interface Sink {

        @Input(UPDATE_ENTITY_INPUT)
        SubscribableChannel updateEntityInput();

        @Input(DELETE_ENTITY_INPUT)
        SubscribableChannel deleteEntityInput();

        @Input(UPDATE_MODEL_INPUT)
        SubscribableChannel updateModelInput();

    }
}
