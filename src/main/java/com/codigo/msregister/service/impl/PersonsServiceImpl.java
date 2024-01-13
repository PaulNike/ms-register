package com.codigo.msregister.service.impl;

import com.codigo.msregister.aggregates.request.RequestPersons;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.aggregates.response.ResponseReniec;
import com.codigo.msregister.config.RedisService;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.entity.EnterprisesEntity;
import com.codigo.msregister.entity.PersonsEntity;
import com.codigo.msregister.feignClient.ReniecClient;
import com.codigo.msregister.repository.PersonsRepository;
import com.codigo.msregister.service.PersonsService;
import com.codigo.msregister.util.PersonsValidations;
import com.codigo.msregister.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class PersonsServiceImpl implements PersonsService {
    private final ReniecClient reniecClient;
    private final PersonsValidations personsValidations;
    private final PersonsRepository personsRepository;
    private final RedisService redisService;
    private final Util util;



    public PersonsServiceImpl(ReniecClient reniecClient, PersonsValidations personsValidations, PersonsRepository personsRepository, RedisService redisService, Util util) {
        this.reniecClient = reniecClient;
        this.personsValidations = personsValidations;
        this.personsRepository = personsRepository;
        this.redisService = redisService;
        this.util = util;
    }

    @Value("${token.api.reniec}")
    private String tokenReniec;

    @Value("${time.expiration.reniec.info}")
    private String timeExpirationReniecInfo;

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
        Optional<PersonsEntity> optional = personsRepository.findById(id);
        if(optional.isPresent()){
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,optional);
        }else {
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ZERO_ROWS,Optional.empty());
        }
    }

    @Override
    public ResponseBase findAll() {
        List<PersonsEntity> entityList = personsRepository.findAll();
        if(entityList.size() >= 1){
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,Optional.of(entityList));
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ZERO_ROWS,Optional.empty());
        }
    }

    @Override
    public ResponseBase updatePersons(int id, RequestPersons requestPersons) {
        boolean existsPerson = personsRepository.existsById(id);
        if(existsPerson){
            Optional<PersonsEntity> personsEntity = personsRepository.findById(id);
            boolean validatInput = personsValidations.validateInput(requestPersons);
            if(validatInput){
                PersonsEntity personsUpdate = getPerson(requestPersons,personsEntity.get(),true);
                personsRepository.save(personsUpdate);
                return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,Optional.of(personsUpdate));
            }else{
                return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR_DATA_NOT_VALID,Optional.empty());
            }
        }else{
            return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR_NOT_UPDATE_PERSON,Optional.empty());
        }
    }

    private PersonsEntity getPersonsEntity(RequestPersons requestPersons){
        PersonsEntity personsEntity = new PersonsEntity();
        //Ejecutando Reniec
        ResponseReniec reniec = getExecutionReniec(requestPersons.getNumDocument());
        if(reniec != null){
            personsEntity.setName(reniec.getNombres());
            personsEntity.setLastName(reniec.getApellidoPaterno() + " " + reniec.getApellidoMaterno());
        }else{
            return null;
        }
        return  getPerson(requestPersons,personsEntity,false);
    }
    /*private PersonsEntity getPersonsEntityUpdate(RequestPersons requestPersons, PersonsEntity personsEntity){
        return  getPerson(requestPersons,personsEntity,true);
    }*/

    private PersonsEntity getPerson(RequestPersons requestPersons, PersonsEntity personsEntity, boolean isUpdate){
        personsEntity.setNumDocument(requestPersons.getNumDocument());
        personsEntity.setEmail(requestPersons.getEmail());
        personsEntity.setTelephone(requestPersons.getTelephone());
        personsEntity.setStatus(Constants.STATUS_ACTIVE);
        personsEntity.setDocumentsTypeEntity(getDocumentsType(requestPersons));
        personsEntity.setEnterprisesEntity(getEnterprisesEntity(requestPersons));
        if(isUpdate){
            personsEntity.setUserModif(Constants.AUDIT_ADMIN);
            personsEntity.setDateModif(getTimestamp());
        }else {
            personsEntity.setUserCreate(Constants.AUDIT_ADMIN);
            personsEntity.setDateCreate(getTimestamp());
        }

        return personsEntity;
    }


    public ResponseReniec getExecutionReniec(String numero){
        String redisCache = redisService.getValueByKey(Constants.REDIS_KEY_INFO_RENIEC+numero);
        if(redisCache!= null){
            ResponseReniec reniec = util.convertFromJson(redisCache,ResponseReniec.class);
            return reniec;
        }else{
            String authorization = "Bearer "+tokenReniec;
            ResponseReniec reniec = reniecClient.getInfoReniec(numero,authorization);
            String redisData = util.convertToJson(reniec);
            redisService.saveKeyValue(Constants.REDIS_KEY_INFO_RENIEC+numero,redisData,Integer.valueOf(timeExpirationReniecInfo));
            return  reniec;
        }
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
