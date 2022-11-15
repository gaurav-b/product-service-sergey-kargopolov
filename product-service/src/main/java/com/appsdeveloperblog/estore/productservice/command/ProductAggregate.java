package com.appsdeveloperblog.estore.productservice.command;

import java.math.BigDecimal;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import com.appsdeveloperblog.estore.productservice.core.events.ProductCreatedEvent;

import lombok.NoArgsConstructor;

@Aggregate @NoArgsConstructor
public class ProductAggregate {
	
	@AggregateIdentifier
	private String productId;
	private String title;
	private BigDecimal price;
	private Integer quantity;

	@CommandHandler
	public ProductAggregate(CreateProductCommand createProductCommand) {
		// Validation logic goes here for CreateProductCommand
		if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO)<=0) {
			throw new IllegalArgumentException("price can't be less than or equal to zero");
		}
		
		if (createProductCommand.getTitle()==null 
				|| createProductCommand.getTitle().isBlank()
				|| createProductCommand.getTitle().isEmpty()) {
			throw new IllegalArgumentException("title can't be empty");
		}
		
		ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();
		BeanUtils.copyProperties(createProductCommand, productCreatedEvent);
		
		AggregateLifecycle.apply(productCreatedEvent);
	}
	
	@EventSourcingHandler
	public void on(ProductCreatedEvent productCreatedEvent) {
		this.productId=productCreatedEvent.getProductId();
		this.price=productCreatedEvent.getPrice();
		this.title=productCreatedEvent.getTitle();
		this.quantity=productCreatedEvent.getQuantity();
	}
}
