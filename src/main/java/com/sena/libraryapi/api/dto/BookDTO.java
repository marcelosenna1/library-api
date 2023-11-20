package com.sena.libraryapi.api.dto;


import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;

}
