package com.ddkolesnik.ddkapi;

import com.ddkolesnik.ddkapi.configuration.exception.ApiSuccessResponse;
import com.ddkolesnik.ddkapi.dto.cash.InvestorCashDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static com.ddkolesnik.ddkapi.util.Constant.PATH_INVESTOR_CASH;
import static com.ddkolesnik.ddkapi.util.Constant.PATH_INVESTOR_CASH_UPDATE;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alexandr Stegnin
 */

@Transactional
@SpringBootTest
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера для создания/обновления сумм на сервере")
public class InvestorCashControllerTestIT {

    private static final String NOT_MONEY_IN_RESPONSE_MSG = "В ответ не было получено суммы";

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext wac;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Order(1)
    @DisplayName("Тест создания суммы на сервере")
    public void createInvestorCash() throws Exception {
        InvestorCashDTO investorCashDTO = getNewInvestorCashDTO();
        String inputJson = mapper.writeValueAsString(investorCashDTO);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post(PATH_INVESTOR_CASH + PATH_INVESTOR_CASH_UPDATE, "dc2fdef45a824c50")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(inputJson))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertFalse(content.isEmpty(), NOT_MONEY_IN_RESPONSE_MSG);
        ApiSuccessResponse successResponse = mapper.readValue(content, ApiSuccessResponse.class);
        assertNotNull(successResponse, NOT_MONEY_IN_RESPONSE_MSG);
        assertEquals(HttpStatus.OK, successResponse.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Тест создания суммы на сервере")
    public void updateInvestorCash() throws Exception {
        InvestorCashDTO investorCashDTO = getInvestorCashDTOToUpdate();
        String inputJson = mapper.writeValueAsString(investorCashDTO);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post(PATH_INVESTOR_CASH + PATH_INVESTOR_CASH_UPDATE, "dc2fdef45a824c50")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(inputJson))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertFalse(content.isEmpty(), NOT_MONEY_IN_RESPONSE_MSG);
        ApiSuccessResponse successResponse = mapper.readValue(content, ApiSuccessResponse.class);
        assertNotNull(successResponse, NOT_MONEY_IN_RESPONSE_MSG);
        assertEquals(HttpStatus.OK, successResponse.getStatus());
    }

    private InvestorCashDTO getNewInvestorCashDTO() {
        InvestorCashDTO dto = new InvestorCashDTO();
        dto.setInvestorCode("017");
        dto.setCashSource("40802810838320000244");
        dto.setTransactionUUID(UUID.randomUUID().toString());
        dto.setGivenCash(new BigDecimal("10000"));
        dto.setDateGiven(LocalDate.now());
        dto.setFacility("72001 Чаплина, 127а");
        return dto;
    }

    private InvestorCashDTO getInvestorCashDTOToUpdate() {
        InvestorCashDTO dto = new InvestorCashDTO();
        dto.setInvestorCode("017");
        dto.setCashSource("40802810838320000244");
        dto.setTransactionUUID("1858cf74-3c0c-4333-9457-03c528bf298d");
        dto.setGivenCash(new BigDecimal("5000"));
        dto.setDateGiven(LocalDate.now());
        dto.setFacility("72001 чаплина, 127а");
        return dto;
    }

}
