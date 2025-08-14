package com.library.controller;

import com.library.model.Book;
import com.library.model.User;
import com.library.repository.BookRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
    	List<Book> books = bookRepository.findAll();
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
    	 Book savedBook = bookRepository.save(book);
         return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .<ResponseEntity<?>>map(book -> ResponseEntity.ok(book))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Book with ID " + id + " not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book with ID " + id + " not found");
        }
        bookRepository.deleteById(id);
        return ResponseEntity.ok("Book with ID " + id + " deleted successfully");
    }
}
