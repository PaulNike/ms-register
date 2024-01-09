package com.codigo.msregister.service.impl;

import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.aggregates.response.ResponseBase;
import com.codigo.msregister.constants.Constants;
import com.codigo.msregister.entity.DocumentsTypeEntity;
import com.codigo.msregister.entity.EnterprisesEntity;
import com.codigo.msregister.entity.EnterprisesTypeEntity;
import com.codigo.msregister.repository.EnterprisesRepository;
import com.codigo.msregister.service.EnterprisesService;
import com.codigo.msregister.util.EnterprisesValidations;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnterprisesServiceImpl implements EnterprisesService {

    private final EnterprisesRepository enterprisesRepository;
    private final EnterprisesValidations enterprisesValidations;

    public EnterprisesServiceImpl(EnterprisesRepository enterprisesRepository, EnterprisesValidations enterprisesValidations) {
        this.enterprisesRepository = enterprisesRepository;
        this.enterprisesValidations = enterprisesValidations;
    }

    @Override
    public ResponseBase createEnterprise(RequestEnterprises requestEnterprises) {
        boolean validate = enterprisesValidations.validateInput(requestEnterprises);
        if(validate){
            EnterprisesEntity enterprises = getEntity(requestEnterprises);
            enterprisesRepository.save(enterprises);
            List<EnterprisesEntity> generic = new ArrayList<>();
            generic.add(enterprises);
            return new ResponseBase(Constants.CODE_SUCCESS,Constants.MESS_SUCCESS,generic);
        }
        return new ResponseBase(Constants.CODE_ERROR,Constants.MESS_ERROR,null);
    }

    @Override
    public ResponseBase findOneEnterprise(Integer id) {
        return null;
    }

    @Override
    public ResponseBase findAllEnterprises() {
        return null;
    }

    @Override
    public ResponseBase updateEnterprise(Integer id, RequestEnterprises requestEnterprises) {
        return null;
    }

    private EnterprisesEntity getEntity(RequestEnterprises requestEnterprises){
        EnterprisesEntity entity = new EnterprisesEntity();
        entity.setNumDocument(requestEnterprises.getNumDocument());
        entity.setBusinessName(requestEnterprises.getBusinessName());
        entity.setTradeName(enterprisesValidations.isNullOrEmpty(requestEnterprises.getTradeName()) ? requestEnterprises.getBusinessName() : requestEnterprises.getTradeName());
        entity.setStatus(Constants.STATUS_ACTIVE);
        //Añadiendo FK
        EnterprisesTypeEntity enterprisesTypeEntity = new EnterprisesTypeEntity();
        enterprisesTypeEntity.setIdEnterprisesType(requestEnterprises.getEnterprisesTypeEntity());
        entity.setEnterprisesTypeEntity(enterprisesTypeEntity);

        DocumentsTypeEntity documentsTypeEntity = new DocumentsTypeEntity();
        documentsTypeEntity.setIdDocumentsType(requestEnterprises.getDocumentsTypeEntity());
        entity.setDocumentsTypeEntity(documentsTypeEntity);

        //Auditoria
        long currentTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTime);
        entity.setUserCreate(Constants.AUDIT_ADMIN);
        entity.setDateCreate(timestamp);

        return entity;
    }
}
