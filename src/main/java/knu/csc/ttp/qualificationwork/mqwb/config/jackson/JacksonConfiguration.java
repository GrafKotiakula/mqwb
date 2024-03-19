package knu.csc.ttp.qualificationwork.mqwb.config.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

@Configuration
public class JacksonConfiguration {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(ApplicationContext context) {
        return builder -> builder
                .serializerByType(Page.class, context.getBean(PageSerializer.class))
                .modulesToInstall(context.getBean(JsonComponentModule.class))
                .featuresToDisable(
                        SerializationFeature.WRAP_EXCEPTIONS,
                        DeserializationFeature.WRAP_EXCEPTIONS
                );
    }
}
