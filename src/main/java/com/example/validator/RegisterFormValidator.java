package com.example.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.dto.RegisterModel;

@Component
public class RegisterFormValidator implements Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final int PASSWORD_LENGTH = 6;
	private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	
	public boolean supports(Class<?> paramClass) {
		return RegisterModel.class.equals(paramClass);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "firstName", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "email", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "password", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "common.required");
		RegisterModel register = (RegisterModel) obj;
		if (!"".equals(register.getEmail()) 
				&& !pattern.matcher(register.getEmail()).matches()) {
			errors.rejectValue("email","common.validation.email");
		}
		if (register.getPassword().length() < PASSWORD_LENGTH 
				&& register.getPassword().length() > 1) {
			errors.rejectValue("password","common.validation.password");
		}
		if(!"".equals(register.getConfirmPassword()) 
				&& !register.getConfirmPassword().equals(register.getPassword())){
			errors.rejectValue("confirmPassword","confirmPassword.notequal");
		}
	}
}