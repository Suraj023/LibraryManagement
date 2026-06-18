package com.library.controller;

import com.library.model.User;
import com.library.service.AuditLogService;
import com.library.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "APIs for managing library users")
public class UserController {

	private final UserService userService;
	private final AuditLogService auditLogService;

	public UserController(UserService userService, AuditLogService auditLogService) {
		this.userService = userService;
		this.auditLogService = auditLogService;
	}

	@Operation(summary = "Get all users", description = "Returns a list of all registered users")
	@ApiResponse(responseCode = "200", description = "Users retrieved successfully")
	@GetMapping
	public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
		try {
			List<User> users = userService.getAllUsers();

			// Audit success
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), null, // requestBody (not
																									// needed for GET)
					users.toString(), // responseBody
					HttpStatus.OK.value(), null, // errorCode
					true // success
			);

			return ResponseEntity.ok(users);

		} catch (Exception e) {
			// Audit failure
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), null, // requestBody
					e.getMessage(), // responseBody
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "USR-002", // custom error code
					false // success
			);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Create a user", description = "Registers a new user in the system")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "User created successfully"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@PostMapping
	public ResponseEntity<User> createUser(@RequestBody User user, HttpServletRequest request) {
		try {
			User savedUser = userService.saveUser(user);

			// Audit success
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), user.toString(), // requestBody
					savedUser.toString(), // responseBody
					HttpStatus.CREATED.value(), null, // errorCode
					true // success
			);

			return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

		} catch (Exception e) {
			// Audit failure
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), user.toString(),
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "USR-001", // custom error code
					false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Get user by ID", description = "Returns a single user by their ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User found"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@Parameter(description = "ID of the user") @PathVariable Long id, HttpServletRequest request) {
		try {
			return userService.getUserById(id).<ResponseEntity<?>>map(user -> {
				// Audit success
				auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id, // requestBody
																												// (just
																												// the
																												// ID)
						user.toString(), // responseBody
						HttpStatus.OK.value(), null, // errorCode
						true // success
				);
				return ResponseEntity.ok(user);
			}).orElseGet(() -> {
				// Audit "not found"
				auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id,
						"User with ID " + id + " not found", HttpStatus.NOT_FOUND.value(), "USR-003", // custom error
																										// code
						false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
			});
		} catch (Exception e) {
			// Audit failure
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "USR-003-EX", // custom error code for
																							// exception
					false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Update a user", description = "Updates an existing user's information")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User updated successfully"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@PutMapping("/update")
	public ResponseEntity<?> updateUserById(@RequestBody User user, HttpServletRequest request) {
		try {
			org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext()
					.getAuthentication();
			String currentUsername = authentication.getName();

			boolean updated = userService.updateUser(user);

			if (!updated) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("User : " + user.getUserName() + " with ID : " + user.getId() + " not found");
			}

			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), user.toString(), // requestBody
					user.toString(), // responseBody
					HttpStatus.CREATED.value(), null, // errorCode
					true // success
			);

		} catch (Exception e) {
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), user.toString(),
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "USR-001", // custom error code
					false // success
			);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed");
		}
		return ResponseEntity
				.ok("User : " + user.getUserName() + " with ID : " + user.getId() + " updated successfully");
	}

	@Operation(summary = "Delete a user", description = "Deletes a user from the system by their ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User deleted successfully"),
		@ApiResponse(responseCode = "404", description = "User not found")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteUser(@Parameter(description = "ID of the user to delete") @PathVariable Long id, HttpServletRequest request) {
		try {
			boolean deleted = userService.deleteUser(id);

			if (!deleted) {
				// Audit "not found"
				auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id,
						"User with ID " + id + " not found", HttpStatus.NOT_FOUND.value(), "USR-004", // custom error
																										// code
						false);

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
			}

			// Audit success
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					"User with ID " + id + " deleted successfully", HttpStatus.OK.value(), null, true);

			return ResponseEntity.ok("User with ID " + id + " deleted successfully");

		} catch (Exception e) {
			// Audit exception
			auditLogService.log("UserService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "USR-004-EX", // custom error code for
																							// exceptions
					false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
