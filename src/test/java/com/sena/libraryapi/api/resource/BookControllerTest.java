package com.sena.libraryapi.api.resource;

import com.sena.libraryapi.LibraryApiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Deve criar um livro com sucesso!")
    void createdBookTest(){

    }

    @Test
    @DisplayName("Deve deve lançar erro de validação quando não houver dados suficiente para criação do livro")
    public void createdInvalidBookTest(){

    }

    @Test
    @DisplayName("Deve deletar um livro com sucesso!")
    void deletedBookTest(){

    }
}
