package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {

		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		String username = createUserRequest.getUsername();

		logger.info("CREATE_USER_REQUEST - Creating user {}", username);

		if (createUserRequest.getPassword().length() < 8) {
			logger.error("CREATE_USER_FAILURE - Unable to create user {}, Password for user {} less than eight characters",
					username, username);
			throw new IllegalArgumentException("Password must be min 8 characters long");
		}

		if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			logger.error("CREATE_USER_FAILURE - Unable to create user {}, Password for user {} did not match confirmPassword",
					username, username);
			throw new IllegalArgumentException("Passwords do not match");
		}

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		userRepository.save(user);

		logger.info("CREATE_USER_SUCCESS - User {} successfully created", username);
		return ResponseEntity.ok(user);
	}
}
