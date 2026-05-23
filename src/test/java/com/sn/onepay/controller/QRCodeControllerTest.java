package com.sn.onepay.controller;

import com.sn.onepay.dto.QRCodeCashierDTO;
import com.sn.onepay.dto.QRCodeClientDTO;
import com.sn.onepay.services.QRCodeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QRCodeController.class)
class QRCodeControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    QRCodeService qrCodeService;

    @Test
    void getClientQRCode_returns200() throws Exception {
        when(qrCodeService.getClientQrCode(anyLong()))
                .thenReturn(new QRCodeClientDTO(1L, 500.0, 200.0, 100.0, 300.0));

        mockMvc.perform(get("/v1/onepay/qrcode/client").param("clientId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getCashierQRCode_returns200() throws Exception {
        when(qrCodeService.getCashierQrCode(anyLong()))
                .thenReturn(new QRCodeCashierDTO(1L, 100.0, 10.0));

        mockMvc.perform(get("/v1/onepay/qrcode/cashier").param("cashierId", "1"))
                .andExpect(status().isOk());
    }
}