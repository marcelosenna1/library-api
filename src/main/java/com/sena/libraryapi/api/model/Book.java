package com.sena.libraryapi.api.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
}
