package com.library.controller;

import com.library.model.Book;
import com.library.service.AuditLogService;
import com.library.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "APIs for managing library books")
public class BookController {

	private final BookService bookService;
	private final AuditLogService auditLogService;

	public BookController(BookService bookService, AuditLogService auditLogService) {
		this.bookService = bookService;
		this.auditLogService = auditLogService;
	}

	@Operation(summary = "Get all books", description = "Returns a list of all books in the library")
	@ApiResponse(responseCode = "200", description = "Books retrieved successfully")
	@GetMapping
	public ResponseEntity<List<Book>> getAllBooks(HttpServletRequest request) {
		try {
			List<Book> books = bookService.getAllBooks();

			// Audit success
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), null, books.toString(),
					HttpStatus.OK.value(), null, true);

			return ResponseEntity.ok(books);

		} catch (Exception e) {
			// Audit failure
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), null, e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), "BOOK-001", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Create a book", description = "Adds a new book to the library")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "Book created successfully"),
		@ApiResponse(responseCode = "500", description = "Internal server error")
	})
	@PostMapping
	public ResponseEntity<Book> createBook(@RequestBody Book book, HttpServletRequest request) {
		try {
			Book savedBook = bookService.saveBook(book);

			// Audit success
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), book.toString(),
					savedBook.toString(), HttpStatus.CREATED.value(), null, true);

			return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);

		} catch (Exception e) {
			// Audit failure
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), book.toString(),
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "BOOK-002", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Get book by ID", description = "Returns a single book by its ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Book found"),
		@ApiResponse(responseCode = "404", description = "Book not found")
	})
	@GetMapping("/{id}")
	public ResponseEntity<?> getBookById(@Parameter(description = "ID of the book") @PathVariable Long id, HttpServletRequest request) {
		try {
			return bookService.getBookById(id).<ResponseEntity<?>>map(book -> {
				// Audit success
				auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
						book.toString(), HttpStatus.OK.value(), null, true);
				return ResponseEntity.ok(book);
			}).orElseGet(() -> {
				// Audit not found
				auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
						"Book with ID " + id + " not found", HttpStatus.NOT_FOUND.value(), "BOOK-003", false);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID " + id + " not found");
			});

		} catch (Exception e) {
			// Audit exception
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "BOOK-003-EX", false);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Operation(summary = "Delete a book", description = "Deletes a book from the library by its ID")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Book deleted successfully"),
		@ApiResponse(responseCode = "404", description = "Book not found")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@Parameter(description = "ID of the book to delete") @PathVariable Long id, HttpServletRequest request) {
		try {
			boolean deleted = bookService.deleteBook(id);

			if (!deleted) {
				// Audit not found
				auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
						"Book with ID " + id + " not found", HttpStatus.NOT_FOUND.value(), "BOOK-004", false);

				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with ID " + id + " not found");
			}

			// Audit success
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					"Book with ID " + id + " deleted successfully", HttpStatus.OK.value(), null, true);

			return ResponseEntity.ok("Book with ID " + id + " deleted successfully");

		} catch (Exception e) {
			// Audit exception
			auditLogService.log("BookService", request.getMethod(), request.getRequestURI(), "ID: " + id,
					e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "BOOK-004-EX", false);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
