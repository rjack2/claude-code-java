package com.vinsguru;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crudLifecycle() throws Exception {
        var location = this.mockMvc.perform(post("/api/students")
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .content("""
                                               {"name":"Alice","age":14,"className":"9A"}
                                               """))
                                   .andExpect(status().isCreated())
                                   .andExpect(header().exists("Location"))
                                   .andExpect(jsonPath("$.id").isNumber())
                                   .andExpect(jsonPath("$.name").value("Alice"))
                                   .andReturn()
                                   .getResponse()
                                   .getHeader("Location");

        this.mockMvc.perform(get(location))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.className").value("9A"));

        this.mockMvc.perform(put(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"Alice B","age":15,"className":"10A"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Alice B"))
                    .andExpect(jsonPath("$.age").value(15));

        this.mockMvc.perform(get("/api/students"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

        this.mockMvc.perform(delete(location))
                    .andExpect(status().isNoContent());

        this.mockMvc.perform(get(location))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.title").value("Student Not Found"))
                    .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void invalidRequestReturnsProblemDetail() throws Exception {
        this.mockMvc.perform(post("/api/students")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name":"","age":0}
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid Request"))
                    .andExpect(jsonPath("$.errors.name").exists())
                    .andExpect(jsonPath("$.errors.age").exists())
                    .andExpect(jsonPath("$.errors.className").exists());
    }

    @Test
    void deleteUnknownStudentReturns404() throws Exception {
        this.mockMvc.perform(delete("/api/students/9999"))
                    .andExpect(status().isNotFound());
    }

}
