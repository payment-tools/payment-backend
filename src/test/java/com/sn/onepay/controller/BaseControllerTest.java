package com.sn.onepay.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/*Each subclass disables security filters via @AutoConfigureMockMvc(addFilters = false): authorization rules are covered by SecurityConfigTest*/
abstract class BaseControllerTest {

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    JwtDecoder jwtDecoder;
}
