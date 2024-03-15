package knu.csc.ttp.qualificationwork.mqwb.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import knu.csc.ttp.qualificationwork.mqwb.config.jackson.PageSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;

@Configuration
public class JacksonConfiguration {

    protected Module buildPageModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Page.class, new PageSerializer());
        return module;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.modules(buildPageModule());
    }
}
