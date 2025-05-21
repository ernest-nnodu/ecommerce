package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	private static final Logger logger = LoggerFactory.getLogger(CartController.class);
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		String username = request.getUsername();
		long itemId = request.getItemId();

		logger.info("Processing cart request for user {} to add item {}", username, itemId);

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logError(username);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logError(itemId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));

		logger.info("Item {} added to cart for user {}", itemId, username);
		return ResponseEntity.ok(cartRepository.save(cart));
	}

	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		String username = request.getUsername();
		long itemId = request.getItemId();

		logger.info("Processing cart request for user {} to remove item {}", username, itemId);

		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			logError(username);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logError(itemId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));

		logger.info("Item {} removed from cart for user {}", itemId, username);
		return ResponseEntity.ok(cartRepository.save(cart));
	}

	private void logError(long itemId) {
		logger.error("Item {} not found in database", itemId);
	}

	private void logError(String username) {
		logger.error("User {} not found in database", username);
	}
}
