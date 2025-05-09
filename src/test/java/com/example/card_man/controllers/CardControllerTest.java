package com.example.card_man.controllers;

import com.example.card_man.dtos.CardResp;
import com.example.card_man.dtos.PageResponse;
import com.example.card_man.models.CreditCard;
import com.example.card_man.repositories.UserRepository;
import com.example.card_man.services.CardService;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CardControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CardController cardController;

  @MockitoBean
  private CardService service;

  @Test
  public void whenUserControllerInjected_thenNotNull() {
    assertNotNull(cardController);
  }

  @Test
  @WithMockUser(username="admin", roles={"ADMIN"})
  void generate() throws Exception {
    Mockito.when(service.generate())
        .thenReturn("4000000000000000");

    mockMvc.perform(MockMvcRequestBuilders.get("/cards/admin/generate-card-number")
            .contentType(MediaType.TEXT_PLAIN))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(StringContains.containsString("4000000000000000")));
  }

  @Test
  @WithMockUser
  void whenGenerateAsUser_thenForbidden() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/cards/admin/generate-card-number"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  @WithMockUser(username="admin", roles={"ADMIN"})
  void whenFindReqWithInvalidParam_thenBadRequest() throws Exception{
    mockMvc.perform(MockMvcRequestBuilders.get("/cards/admin/list")
            .param("expiryDate", "13/13")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "id,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation failed"));
  }

  @Test
  @WithMockUser(username="admin", roles={"ADMIN"})
  void whenValidReqToAdminList_thenOk() throws Exception {
    List<CardResp> cardsList = List.of(
        new CardResp(1L, "TOM SMITH", "**** **** **** 1234", "01/26", CreditCard.CardStatus.ACTIVE, false, BigDecimal.TEN, 1L),
        new CardResp(2L, "TOM SMITH", "**** **** **** 5678", "02/27", CreditCard.CardStatus.ACTIVE, false, BigDecimal.TEN, 1L)
    );
    Pageable pageable = PageRequest.of(0, 10);
    long totalElements = 2;
    Page<CardResp> page = new PageImpl<>(cardsList, pageable, totalElements);
    PageResponse<CardResp> pageResponse = new PageResponse<>(page);

    Mockito.when(service.findByCriteria(Mockito.any(), Mockito.any()))
        .thenReturn(pageResponse);

    mockMvc.perform(MockMvcRequestBuilders.get("/cards/admin/list")
            .param("expiryDate", "10/13")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "id,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].expiryDate").value("01/26"));
  }
}