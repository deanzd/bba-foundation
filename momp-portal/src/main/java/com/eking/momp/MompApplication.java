package com.eking.momp;

import com.eking.momp.model.stream.EntitySearchReceiver;
import com.eking.momp.model.stream.EntitySearchSender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.stream.annotation.EnableBinding;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties
@EnableCaching
@EnableBinding({EntitySearchSender.Source.class, EntitySearchReceiver.Sink.class})
public class MompApplication {

	public static void main(String[] args) {
		SpringApplication.run(MompApplication.class, args);
	}

}
