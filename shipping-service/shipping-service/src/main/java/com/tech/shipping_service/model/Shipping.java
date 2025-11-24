package com.tech.shipping_service.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

@Entity
public class Shipping {
	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long id;
	 private String orderNumber;     
	 private String customerName;
	 private String address;
	 private String status;
	 private String trackingNumber;
	 
     @OneToMany(cascade =CascadeType.ALL)
	 @JoinColumn(name = "shipping_id")
	 private List<ShippingItems> shippingItems;

	 public Shipping() {}

	 
	 
	 public Shipping(Long id, String orderNumber, String customerName, String address, String status,
			String trackingNumber, List<ShippingItems> shippingItems) {
		super();
		this.id = id;
		this.orderNumber = orderNumber;
		this.customerName = customerName;
		this.address = address;
		this.status = status;
		this.trackingNumber = trackingNumber;
		this.shippingItems = shippingItems;
	}



	 public Long getId() {
		 return id;
	 }

	 public void setId(Long id) {
		 this.id = id;
	 }

	 public String getOrderNumber() {
		 return orderNumber;
	 }

	 public void setOrderNumber(String orderNumber) {
		 this.orderNumber = orderNumber;
	 }

	 public String getCustomerName() {
		 return customerName;
	 }

	 public void setCustomerName(String customerName) {
		 this.customerName = customerName;
	 }

	 public String getAddress() {
		 return address;
	 }

	 public void setAddress(String address) {
		 this.address = address;
	 }

	 public String getStatus() {
		 return status;
	 }

	 public void setStatus(String status) {
		 this.status = status;
	 }

	 public String getTrackingNumber() {
		 return trackingNumber;
	 }

	 public void setTrackingNumber(String trackingNumber) {
		 this.trackingNumber = trackingNumber;
	 }

	 public List<ShippingItems> getShippingItems() {
		 return shippingItems;
	 }

	 public void setShippingItems(List<ShippingItems> shippingItems) {
		 this.shippingItems = shippingItems;
	 }
	 
	 
}
	 