package com.example.demo.integration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.defer-datasource-initialization=true",
        "spring.sql.init.mode=always",
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CheckoutIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculateTotalPricePairPromotion() throws Exception {
        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                ["A","C"]
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("50.00"));
    }
    @Test
    void calculateTotalPriceWithoutPromotion() throws Exception {
        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                ["A"]
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("40.00"));
    }
    @Test
    void calculateTotalPricePairPromotionWithRemainingItem() throws Exception {
        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                ["A","A","C"]
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("90.00"));
    }
}