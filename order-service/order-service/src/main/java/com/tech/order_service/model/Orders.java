package com.tech.order_service.model;


import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Orders {
	 	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	private String orderNumber;
	private String customerName;
	private String address;
	private String status;
	private String trackingNumber;
	private String transactionId;
	@OneToMany(cascade =CascadeType.ALL)
	@JoinColumn(name = "orders_id")
	private List<OrderItems> orderItems;


}
