package com.codigo.msregister.service.impl;

import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.aggregates.response.ResponseReniec;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.entity.EnterprisesEntity;
import com.codigo.msregister.entity.PersonsEntity;
import com.codigo.msregister.feignClient.ReniecClient;
import com.codigo.msregister.repository.PersonsRepository;
import com.codigo.msregister.service.PersonsService;
import com.codigo.msregister.util.PersonsValidations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class PersonsServiceImpl implements PersonsService {
    private final ReniecClient reniecClient;
    private final PersonsValidations personsValidations;
    private final PersonsRepository personsRepository;


    public PersonsServiceImpl(ReniecClient reniecClient, PersonsValidations personsValidations, PersonsRepository personsRepository) {
        this.reniecClient = reniecClient;
        this.personsValidations = personsValidations;
        this.personsRepository = personsRepository;
    }

    @Value("${token.api.reniec}")
    private String tokenReniec;
    @Override
    public ResponseBase getInfoReniec(String numero) {
        ResponseReniec reniec = getExecutionReniec(numero);
        if(reniec != null){
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS, Optional.of(reniec));
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_NON_DATA_RENIEC, Optional.empty());
        }

    }

    @Override
    public ResponseBase createPersons(RequestPersons requestPersons) {
        boolean validatePersons = personsValidations.validateInput(requestPersons);
        if(validatePersons){
            PersonsEntity personsEntity = getPersonsEntity(requestPersons);
            if(personsEntity != null){
                personsRepository.save(personsEntity);
                return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,Optional.of(personsEntity));
            }else {
                return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR,Optional.empty());
            }
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR_DATA_NOT_VALID,Optional.empty());
        }
    }

    @Override
    public ResponseBase findOne(int id) {
        return null;
    }

    @Override
    public ResponseBase findAll() {
        return null;
    }

    @Override
    public ResponseBase updatePersons(int id, RequestPersons requestPersons) {
        return null;
    }

    private PersonsEntity getPersonsEntity(RequestPersons requestPersons){
        PersonsEntity personsEntity = new PersonsEntity();
        personsEntity.setNumDocument(requestPersons.getNumDocument());
        //Ejecutando Reniec
        ResponseReniec reniec = getExecutionReniec(requestPersons.getNumDocument());
        if(reniec != null){
            personsEntity.setName(reniec.getNombres());
            personsEntity.setLastName(reniec.getApellidoPaterno() + " " + reniec.getApellidoMaterno());
        }else{
            return null;
        }
        personsEntity.setEmail(requestPersons.getEmail());
        personsEntity.setTelephone(requestPersons.getTelephone());
        personsEntity.setStatus(Constants.STATUS_ACTIVE);
        personsEntity.setDocumentsTypeEntity(getDocumentsType(requestPersons));
        personsEntity.setEnterprisesEntity(getEnterprisesEntity(requestPersons));
        personsEntity.setUserCreate(Constants.AUDIT_ADMIN);
        personsEntity.setDateCreate(getTimestamp());
        return  personsEntity;
    }

    private ResponseReniec getExecutionReniec(String numero){
        String authorization = "Bearer "+tokenReniec;
        ResponseReniec reniec = reniecClient.getInfoReniec(numero,authorization);
        return  reniec;
    }
    private DocumentsTypeEntity getDocumentsType(RequestPersons requestPersons){
        DocumentsTypeEntity typeEntity = new DocumentsTypeEntity();
        typeEntity.setIdDocumentsType(requestPersons.getDocuments_type_id_documents_type());
        return  typeEntity;
    }

    private EnterprisesEntity getEnterprisesEntity(RequestPersons requestPersons){
        EnterprisesEntity enterprisesEntity = new EnterprisesEntity();
        enterprisesEntity.setIdEnterprises(requestPersons.getEnterprises_id_enterprises());
        return  enterprisesEntity;
    }

    private Timestamp getTimestamp(){
        long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        return timestamp;
    }
}
