package com.eking.momp.model.stream;

import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class EntitySearchSender {

    private static final String OUTPUT = "output";

    private static final String TAG_UPDATE_ENTITY = "update-entity";
    private static final String TAG_DELETE_ENTITY = "delete-entity";
    private static final String TAG_UPDATE_MODEL = "update-model";
    
    @Autowired
    private Source source;

    public void saveOrUpdateEntity(String modelCode, String id) {
        this.send(modelCode + ":" + id, TAG_UPDATE_ENTITY);
    }

    public void deleteEntity(String modelCode, String id) {
        this.send(modelCode + ":" + id, TAG_DELETE_ENTITY);
    }

    public void updateModel(String code) {
        this.send(code, TAG_UPDATE_MODEL);
    }

    private  <T> void send(T content, String tags) {
        Message<T> message = (Message<T>) MessageBuilder.withPayload(content)
                .setHeader(MessageConst.PROPERTY_TAGS, tags)
                .build();
        source.output().send(message);
    }

    public interface Source {

        @Output(OUTPUT)
        MessageChannel output();

    }
}
