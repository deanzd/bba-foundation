package com.eking.momp.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "momp")
public class MompProperties {
    private String localeCookieName;
    private ElasticsearchProperties elasticsearch;
}
