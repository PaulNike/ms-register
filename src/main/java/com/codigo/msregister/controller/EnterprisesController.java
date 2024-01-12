package com.codigo.msregister.controller;

import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.service.EnterprisesService;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("{id}") //APUESTA LA CERVEZA PARA CADA UNO SI ES QUE NO FUNCIONA
    public ResponseBase findOne(@PathVariable int id){
        ResponseBase responseBase = enterprisesService.findOneEnterprise(id);
        return responseBase;
    }
    @GetMapping()
    public ResponseBase findAll(){
        ResponseBase responseBase = enterprisesService.findAllEnterprises();
        return responseBase;
    }
    @PatchMapping("{id}")
    public ResponseBase updateEnterprises(@PathVariable int id, @RequestBody RequestEnterprises requestEnterprises){
        ResponseBase responseBase = enterprisesService.updateEnterprise(id,requestEnterprises);
        return responseBase;
    }
}
