package com.codigo.msregister.controller;

import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.service.EnterprisesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/enterprises")
public class EnterprisesController {
    private final EnterprisesService enterprisesService;

    public EnterprisesController(EnterprisesService enterprisesService) {
        this.enterprisesService = enterprisesService;
    }

    @PostMapping
    public ResponseBase createEnterprise(@RequestBody RequestEnterprises requestEnterprises){
        ResponseBase responseBase = enterprisesService.createEnterprise(requestEnterprises);
        return responseBase;
    }
}
