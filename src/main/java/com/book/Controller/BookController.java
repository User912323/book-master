package com.book.Controller;

import com.book.Entity.Book;
import com.book.Repository.BookRepository;
import com.book.ResponseMessage;
import com.book.Service.BookService;
import com.book.Store;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
public class BookController {

    private static Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    BookService bookService;
//    public BookController(BookService bookService) {
//        this.bookService = bookService;
//    }

    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadfile(@RequestParam("file") MultipartFile file) throws IOException{
        String message = "";
        log.info(" Upload Start");

        try {
          bookService.save(file);
//          move(file);

          message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return new ResponseEntity<>(message, HttpStatus.OK);
      }catch (Exception e) {
          message = "Could not upload the file: " + file.getOriginalFilename() + "!";
          return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
      }

    }

    @GetMapping("/list")
    public ResponseEntity<?> findall() {
        log.info("get List Start");
        try {
            List<Book> books = bookRepository.findAll();
//            bookService.gets();
            System.out.println(books);

            if (books.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
