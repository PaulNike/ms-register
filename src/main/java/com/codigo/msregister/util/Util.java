package com.codigo.msregister.util;

import com.codigo.msregister.aggregates.response.ResponseReniec;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class Util {
    public static String convertToJson(ResponseReniec responseReniec) { // convertir a universal
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(responseReniec);
        } catch (Exception e) {
            // Manejar la excepción (puede ser JsonProcessingException)
            e.printStackTrace();
            return null;
        }
    }
    public static <T> T convertFromJson(String json, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            // Manejar la excepción (puede ser IOException o JsonProcessingException)
            e.printStackTrace();
            return null;
        }
    }
}
