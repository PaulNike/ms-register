package com.codigo.msregister.util;

import com.codigo.msregister.aggregates.enums.ETypeDocuments;
import com.codigo.msregister.aggregates.request.RequestEnterprises;
import com.codigo.msregister.constants.Constants;
import org.springframework.stereotype.Component;

@Component
public class EnterprisesValidations {
    public boolean validateInput(RequestEnterprises requestEnterprises){
        if(requestEnterprises == null){
            return false;
        }
        if(ETypeDocuments.RUC.getValue() != requestEnterprises.getDocumentsTypeEntity()
            || requestEnterprises.getNumDocument().length() != Constants.LENGTH_RUC){
            return false;
        }
        if(isNullOrEmpty(requestEnterprises.getBusinessName()) || isNullOrEmpty(requestEnterprises.getNumDocument())){
            return false;
        }

        return true;
    }
    public boolean isNullOrEmpty(String data){
        return data == null || data.isEmpty();
    }
}
