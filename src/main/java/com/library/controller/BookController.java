package com.library.controller;

import com.library.model.Book;
import com.library.service.AuditLogService;
import com.library.service.BookService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;
	private final AuditLogService auditLogService;

	public BookController(BookService bookService, AuditLogService auditLogService) {
		this.bookService = bookService;
		this.auditLogService = auditLogService;
	}

	// GET all books
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

	// CREATE book
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

	// GET book by ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getBookById(@PathVariable Long id, HttpServletRequest request) {
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

	// DELETE book
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable Long id, HttpServletRequest request) {
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
