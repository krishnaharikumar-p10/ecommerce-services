package com.tech.order_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tech.order_service.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SchedulerService {

	private final OrderRepository orderRepository;
	
	private final Logger log = LoggerFactory.getLogger(SchedulerService.class);
	
	@Scheduled(cron = "0 1 19 * * *") 
	@Transactional
	public void removeFailedOrders() {
		
		int deleteCount =orderRepository.deleteByStatus("FAILED");
		log.info("{} Failed Orders Delated" + deleteCount);
		
		
	}
	
}