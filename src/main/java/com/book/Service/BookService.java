package com.book.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//import com.book.Controller.AsyncController;
import com.book.Entity.Book;
import com.book.Repository.BookRepository;
import com.book.Store;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class BookService {
    private static Logger log = LoggerFactory.getLogger(BookService.class);

    @Autowired
    BookRepository repository;

    public final Path root = Paths.get("upload");
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return (Resource) resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    public String move(File file) throws IOException {

        Path path = Paths.get("C:\\Me\\Spring\\Excel\\book-master\\upload\\Tes\\"+ file.getName());


        if (Files.exists(path)) {
            System.out.println("File already exist");
        }

        if (Files.notExists(path)) {
            Path temp = Files.move(Paths.get("C:\\Me\\Spring\\Excel\\book-master\\upload\\" + file.getName()),
                    Paths.get("C:\\Me\\Spring\\Excel\\book-master\\upload\\Tes\\"+ file.getName()));

            System.out.println("file is not exist ");
            System.out.println("File renamed and moved successfully");
        }

        return path.toString();
    }


    @Async("asyncExecutor")
    public List<Store> save(File file) throws InterruptedException, IOException {

        List<Store> stores = new ArrayList<>();
        try {

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            // Read student data form excel file sheet1.
            XSSFSheet worksheet = workbook.getSheetAt(0);
            for (int index = 0; index < worksheet.getPhysicalNumberOfRows(); index++) {
                if (index > 0) {
                    XSSFRow row = worksheet.getRow(index);
                    Store store = new Store();

                    store.no = getCellValue(row, 0);
                    store.Nama = getCellValue(row, 1);
                    store.Buku = getCellValue(row, 2);
                    store.Tahun = getCellValue(row, 3);
                    store.Tanggal = getCellValue(row, 4);

                    stores.add(store);
                }
            }

            List<Book> entities = new ArrayList<>();
            if (stores.size() > 0) {
                stores.forEach(x->{
                    Book entity = new Book();
                    entity.no = x.no;
                    entity.Nama = x.Nama;
                    entity.Buku = x.Buku;
                    entity.Tahun = x.Tahun;
                    entity.Tanggal = String.valueOf(x.Tanggal);

                    entities.add(entity);
                });

                repository.saveAll(entities);
            }

        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        Thread.sleep(1000L);
        move(file);
        //Intentional delay
        log.info("Post completed");
        return stores;
    }

//    public ByteArrayInputStream load() {
//        List<Book> tutorials = repository.findAll();
//
//        ByteArrayInputStream in = ExcelHelper.tutorialsToExcel(tutorials);
//        return in;
//    }

    public List<Book> booksList() {
        return repository.findAll();
    }
    public List<Store> result = new ArrayList<>();

    @Async("asyncExecutor")
    public List<Store> gets() throws InterruptedException {
        List<Book> books = repository.findAll();

        if (books != null && books.size() > 0){
            books.forEach(x->{
                Store item = new Store();
                item.id = x.id;
                item.no = x.no;
                item.Nama = x.Nama;
                item.Buku = x.Buku;
                item.Tahun = x.Tahun;
                item.Tanggal = x.Tanggal;

                result.add(item);
            });
        }
//        System.out.println(result);
        return result;
    }

    private int convertStringToInt(String str) {
        int result = 0;

        if (str == null || str.isEmpty() || str.trim().isEmpty()) {
            return result;
        }

        result = Integer.parseInt(str);

        return result;
    }

    private String getCellValue(Row row, int cellNo) {
        DataFormatter formatter = new DataFormatter();

        Cell cell = row.getCell(cellNo);

        return formatter.formatCellValue(cell);
    }
}
