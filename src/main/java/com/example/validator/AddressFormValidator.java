package com.example.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.example.dto.AddressAccount;

@Component
public class AddressFormValidator {

	public boolean supports(Class<?> paramClass) {
		return AddressAccount.class.equals(paramClass);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "firstName", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "telephone", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "street", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "country", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "region", "common.required");
	}
}
