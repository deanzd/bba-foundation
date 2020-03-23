package com.eking.momp.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "momp.elasticsearch")
public class ElasticsearchProperties {
    private String host;
    private Integer port;
}
