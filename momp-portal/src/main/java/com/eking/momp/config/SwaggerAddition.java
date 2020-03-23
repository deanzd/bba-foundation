package com.eking.momp.config;

import com.fasterxml.classmate.TypeResolver;
import org.apache.commons.compress.utils.Sets;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.*;

@Component
public class SwaggerAddition implements ApiListingScannerPlugin {
    @Override
    public List<ApiDescription> apply(DocumentationContext context) {
        return Arrays.asList(new ApiDescription("login",
                        "/api/v1/login",
                        "用户登录",
                        Collections.singletonList(new OperationBuilder(new CachingOperationNameGenerator())
                                .method(HttpMethod.POST)
                                .produces(Sets.newHashSet(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                                .summary("登录")
                                .tags(Sets.newHashSet("登入登出"))
                                .parameters(Arrays.asList(
                                        new ParameterBuilder()
                                                .type(new TypeResolver().resolve(String.class))
                                                .name("username")
                                                .parameterType("query")
                                                .modelRef(new ModelRef("string"))
                                                .required(true)
                                                .build(),
                                        new ParameterBuilder()
                                                .type(new TypeResolver().resolve(String.class))
                                                .name("password")
                                                .parameterType("query")
                                                .modelRef(new ModelRef("string"))
                                                .required(true)
                                                .build()
                                ))
                                .responseMessages(Sets.newHashSet(
                                        new ResponseMessage(HttpStatus.OK.value(), "成功", new ModelRef("string"),
                                                new HashMap<>(),
                                                new ArrayList<>()),
                                        new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "失败", new ModelRef(
                                                "string"),
                                                new HashMap<>(),
                                                new ArrayList<>())
                                ))
                                .build()
                        ),
                        false),
                new ApiDescription("logout",
                        "/api/v1/logout",
                        "用户退出",
                        Collections.singletonList(new OperationBuilder(new CachingOperationNameGenerator())
                                .method(HttpMethod.POST)
                                .summary("用户退出")
                                .tags(Sets.newHashSet("登入登出"))
                                .responseMessages(Sets.newHashSet(
                                        new ResponseMessage(HttpStatus.OK.value(), "成功", new ModelRef("string"),
                                                new HashMap<>(),
                                                new ArrayList<>()),
                                        new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "失败",
                                                new ModelRef("string"),
                                                new HashMap<>(),
                                                new ArrayList<>())
                                ))
                                .build()
                        ),
                        false)

        );
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
}
