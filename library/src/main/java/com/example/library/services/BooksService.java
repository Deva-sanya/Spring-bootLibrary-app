package com.example.library.services;

import com.example.library.models.Book;
import com.example.library.models.Person;
import com.example.library.repositories.BookRepository;
import com.example.library.repositories.PersonRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BookRepository bookRepository;

    @Autowired
    public BooksService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAllBooks() {
        return bookRepository.findAll(PageRequest.of(0, 50, Sort.by("year"))).getContent();
    }

    public Book findBookById(int id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        return foundBook.orElse(null);
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void updateBook(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public Person getBookOwner(int id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Hibernate.initialize(book.get().getOwner());
            return book.get().getOwner();
        } else {
            return (Person) Collections.emptyList();
        }
    }

    @Transactional
    public void release(int id) {
        Optional<Book> foundBook = bookRepository.findById(id);
        if (foundBook.isPresent()) {
            foundBook.get().setOwner(null);
            foundBook.get().setTakenAt(null);
        }
    }

    @Transactional
    public void assign(int id, Person selectedPerson) {
        Optional<Book> currentBook = bookRepository.findById(id);
        if (currentBook.isPresent()) {
            currentBook.get().setOwner(selectedPerson);
            currentBook.get().setTakenAt(new Date());
        }
    }

    public List<Book> findBook(String name) {
        return bookRepository.findBookByNameStartsWith(name);
    }
}
