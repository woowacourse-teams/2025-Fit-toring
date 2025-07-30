package fittoring.mentoring.business.model.password;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PasswordConverter implements AttributeConverter<Password, String> {

    @Override
    public String convertToDatabaseColumn(Password attribute) {
        return attribute.getPassword();
    }

    @Override
    public Password convertToEntityAttribute(String dbData) {
        return Password.formEncrypt(dbData);
    }
}
