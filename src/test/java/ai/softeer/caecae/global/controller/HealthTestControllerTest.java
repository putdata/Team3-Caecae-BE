package ai.softeer.caecae.global.controller;

import ai.softeer.caecae.global.api.HealthTestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthTestController.class)
class HealthTestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext; // jpaMappingContext 빈 모킹


    @Test
    @DisplayName("HealthTest 컨트롤러를 테스트한다.")
    void healthTest() throws Exception {
        //given
        //when


        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }
}