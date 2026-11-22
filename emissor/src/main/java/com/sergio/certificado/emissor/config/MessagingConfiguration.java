package com.sergio.certificado.emissor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.DefaultJacksonJavaTypeMapper;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

	public static final String QUEUE_NAME = "certificado.emitido";

	@Bean
	Queue certificateIssuedQueue() {
		return QueueBuilder.durable(QUEUE_NAME).build();
	}

	@Bean
	MessageConverter messageConverter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
		DefaultJacksonJavaTypeMapper typeMapper = new DefaultJacksonJavaTypeMapper();
		typeMapper.setTrustedPackages("com.sergio.certificado.contract");
		converter.setJavaTypeMapper(typeMapper);
		return converter;
	}
}
