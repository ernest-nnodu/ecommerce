package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		logger.info("SUBMIT_ORDER_REQUEST - Submitting order for user {}", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			logError("SUBMIT_ORDER_FAILURE", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);

		logger.info("SUBMIT_ORDER_SUCCESS - Submitted order for user {}", username);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		logger.info("ORDER_HISTORY_REQUEST - Retrieving order history for user {}", username);

		User user = userRepository.findByUsername(username);
		if(user == null) {
			logError("ORDER_HISTORY_FAILURE", username);
			return ResponseEntity.notFound().build();
		}

		List<UserOrder> orders = orderRepository.findByUser(user);
		logger.info("ORDER_HISTORY_SUCCESS - Retrieved order history for user {}", username);
		return ResponseEntity.ok(orders);
	}

	private void logError(String error, String username) {
		logger.error("{} - User {} not found in database", error, username);
	}
}
