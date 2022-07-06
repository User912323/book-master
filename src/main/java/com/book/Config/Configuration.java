package com.book.Config;

import com.book.Model.FileInfo;
import com.book.Service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@org.springframework.context.annotation.Configuration
@Slf4j
@EnableScheduling
public class Configuration implements AsyncConfigurer {
    static final Logger logg = Logger.getLogger(Configuration.class.getName());

    @Override
    @Bean(name = "asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                System.out.println("Throwable Exception message : " + ex.getMessage());
                System.out.println("Method name                 : " + method.getName());
                for (Object param : params) {
                    System.out.println("Parameter value             : " + param);
                }
                System.out.println("stack Trace ");
                ex.printStackTrace();
            }

        };
    }

    @Autowired
    BookService bookService;

    @Scheduled(initialDelay = 2000, fixedRate = 3000)
    @Async
    public void refreshPricingParameters() {
//        File file = new File("C:\\Users\\dartmedia\\Documents\\Office\\Excel\\tes\\Tutorial");
//        File file = new File(String.valueOf(bookService.root));
//        log.info(String.valueOf(file));

        // update pricing parameters
//        log.info("computing b price at "+ LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));


        List<FileInfo> list = new ArrayList<FileInfo>();
        list = bookService.loadAll().map(path -> {
            String filename = path.toString();
            File file = new File(  "upload\\" + filename);
            if (file.exists()) {
                System.out.println("ada");
                try {
                    bookService.save((MultipartFile) file);
                    bookService.move((MultipartFile) file);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("tidakada");
            }
            return new FileInfo(filename);
        }).collect(Collectors.toList());

        log.info("file info " + list);

    }
}
