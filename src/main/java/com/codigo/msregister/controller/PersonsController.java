package com.codigo.msregister.controller;

import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.service.PersonsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/persons")
public class PersonsController {

    private final PersonsService personsService;

    public PersonsController(PersonsService personsService) {
        this.personsService = personsService;
    }

    @GetMapping("{numero}")
    public ResponseBase getInfoReniec(@PathVariable String numero){
        ResponseBase responseBase = personsService.getInfoReniec(numero);
        return responseBase;
    }

    @PostMapping()
    public ResponseBase createPerson(@RequestBody RequestPersons requestPersons){
        ResponseBase responseBase = personsService.createPersons(requestPersons);
        return responseBase;
    }
}
