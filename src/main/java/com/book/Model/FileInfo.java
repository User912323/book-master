package com.book.Model;

import lombok.Data;

@Data
public class FileInfo {
    private String name;

    public FileInfo(String name) {
        this.name = name;
    }
}
