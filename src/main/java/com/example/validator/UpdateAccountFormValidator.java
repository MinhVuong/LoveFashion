package com.example.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.example.dto.UpdateAccount;

@Component
public class UpdateAccountFormValidator implements Validator {

	private static final int PASSWORD_LENGTH = 6;
	
	public boolean supports(Class<?> paramClass) {
		return UpdateAccount.class.equals(paramClass);
	}

	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "firstName", "common.required");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "common.required");
		UpdateAccount account = (UpdateAccount) obj;
		if(account.isChangePassword()){
			ValidationUtils.rejectIfEmpty(errors, "currentPassword", "common.required");
			ValidationUtils.rejectIfEmpty(errors, "password", "common.required");
			ValidationUtils.rejectIfEmpty(errors, "confirmPassword", "common.required");
			if (account.getPassword().length() < PASSWORD_LENGTH 
					&& account.getPassword().length() > 1) {
				errors.rejectValue("password","common.validation.password");
			}
			if(!"".equals(account.getConfirmPassword()) 
					&& !account.getConfirmPassword().equals(account.getPassword())){
				errors.rejectValue("confirmPassword","confirmPassword.notequal");
			}
		}
	}
}
