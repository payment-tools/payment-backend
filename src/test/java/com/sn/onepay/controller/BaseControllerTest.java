package com.sn.onepay.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

abstract class BaseControllerTest {

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;
}