package com.example.controllerAPI;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import com.example.dto.AddressAccount;
import com.example.dto.OrderModel;
import com.example.dto.RegisterModel;
import com.example.dto.UpdateAccount;
import com.example.entity.CustomerAddressEntity;
import com.example.entity.CustomerEntity;
import com.example.entity.EmailTemplate;
import com.example.entity.SalesOrder;
import com.example.entity.VerificationToken;
import com.example.event.OnRegistrationCompleteEvent;
import com.example.modelAPI.PackageAPI;
import com.example.modelAPI.ShowDashBoard;
import com.example.service.ICustomerService;
import com.example.service.IEmailTemplateService;
import com.example.service.IProductSevice;
import com.example.validator.RegisterFormValidator;




@RestController
@RequestMapping(value="/api/customer")
public class CustomerControllerAPI {

	@Autowired
	private IProductSevice proService;
	@Autowired
	private ICustomerService customerService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private RegisterFormValidator validator;
	@Autowired
	private ApplicationEventPublisher eventPublisher;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private IEmailTemplateService emailService;

	private Map<String, String> countryList;

	private Map<String, String> regionList;

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final int PASSWORD_LENGTH = 6;
	private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	@RequestMapping(value = { "/{id}" },method = { RequestMethod.GET },produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showDashboard(@PathVariable("id") int id) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer == null) {
			return new ResponseEntity<PackageAPI>( HttpStatus.valueOf(500));
		} else {
			ShowDashBoard showDashBoard = new ShowDashBoard();
			showDashBoard.setDefaultBilling(null);
			showDashBoard.setDefaultShipping(null);
			if (customer.getDefaultBilling() != null) {
				showDashBoard.setDefaultBilling(customerService.getCustomerAddress(new Integer(customer.getDefaultBilling()))); 
			}
			if (customer.getDefaultShipping() != null) {
				showDashBoard.setDefaultShipping(customerService.getCustomerAddress(new Integer(customer.getDefaultShipping())));
			}
			return new ResponseEntity<PackageAPI>(showDashBoard, HttpStatus.valueOf(200));
		}
	}
	
	@RequestMapping(value = { "/login " },
			method = { RequestMethod.GET },
			produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<CustomerEntity> showLogin() {
		//System.out.println("api get login ");
		CustomerEntity customer = new CustomerEntity();
		ResponseEntity<CustomerEntity> entity = new ResponseEntity<CustomerEntity>(customer, HttpStatus.valueOf(200));
		return entity;
	}
	@RequestMapping(value = { "/login " },
			method = { RequestMethod.PUT },
			consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<CustomerEntity> submitLogin(@RequestBody CustomerEntity customer_Client) throws NoSuchAlgorithmException {
		//System.out.println("api put login");
		String email = customer_Client.getEmail();
		String password = customer_Client.getPassword();
		
		if (email == null || "".equals(email)) {
			return new ResponseEntity<CustomerEntity>(HttpStatus.BAD_REQUEST);
		} else if (password == null || "".equals(password)) {
			return new ResponseEntity<CustomerEntity>(HttpStatus.BAD_REQUEST);
		} else if (!pattern.matcher(email).matches()) {
			return new ResponseEntity<CustomerEntity>(HttpStatus.BAD_REQUEST);
		} else if (password.length() < PASSWORD_LENGTH) {
			return new ResponseEntity<CustomerEntity>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = customerService.getCustomer(email, password);
			if (customer == null) {
				return new ResponseEntity<CustomerEntity>(HttpStatus.BAD_REQUEST);
			} else {
				customer.setLogdate(new Date());
				customer.setLognum((short) (customer.getLognum() + 1));
				customerService.update(customer);
				
				/*if (url != null && !"".equals(url)) {
					return "redirect:" + url;
				}*/
				return new ResponseEntity<CustomerEntity>(customer, HttpStatus.valueOf(200));
			}
		}
	}
	
	
	@RequestMapping(value = "/register", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<RegisterModel> showRegister() {
		return new ResponseEntity<RegisterModel>(new RegisterModel(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> register(
			@RequestBody RegisterModel account,
			BindingResult result
			)
			throws NoSuchAlgorithmException {
		if (result.hasErrors()) {
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = customerService.register(account);
			if (customer == null) {
				//model.addAttribute("error_register", messageSource.getMessage("customer.account.register.error.E1", null, null));
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			} else {
				//String appUrl = this.getBaseUrl(request);
				//eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, customer, request.getLocale(), appUrl));
				return new ResponseEntity<PackageAPI>(HttpStatus.OK);
			}
		}
	}
	private String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
			return request.getScheme() + "://" + request.getServerName()
					+ request.getContextPath();
		else
			return request.getScheme() + "://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath();
	}
	
	// khong can API
	@RequestMapping(value = "/baduser", method = RequestMethod.GET)
	public String showBadUser() {
		return "bad-user";
	}
	
	@RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> confirmRegistration(@RequestParam("token") String token) {
		VerificationToken verificationToken = customerService
				.getVerificationToken(token);
		if (verificationToken == null) {
			//model.addAttribute("message", "Not token extits in database!");
			//return "redirect:/customer/account/baduser";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
		CustomerEntity customer = verificationToken.getCustomerEntity();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			//model.addAttribute("message", "Token has expired!");
			//return "redirect:/customer/account/baduser";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
		customer.setIsActive((short) 1);
		customerService.update(customer);
		//return "redirect:/customer/account/login";
		return new ResponseEntity<PackageAPI>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/resendEmail", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> resendRegistrationToken(HttpServletRequest request,@RequestParam("token") String existingToken) {
		VerificationToken newToken = customerService
				.generateNewVerificationToken(existingToken);
		if (newToken != null) {
			CustomerEntity customer = newToken.getCustomerEntity();
			String appUrl = this.getBaseUrl(request);
			SimpleMailMessage email = constructResendVerificationTokenEmail(
					appUrl, request.getLocale(), newToken, customer);
			mailSender.send(email);
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		}
		//model.addAttribute("message", "Not token extits in database!");
		//return "redirect:/customer/account/baduser";
		return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
	}
	private SimpleMailMessage constructResendVerificationTokenEmail(
			String contextPath, Locale locale, VerificationToken newToken,
			CustomerEntity customer) {
		EmailTemplate template = emailService.getEmailTemplate("register");
		String recipientAddress = customer.getEmail();
		String confirmationUrl = contextPath
				+ "/customer/account/regitrationConfirm?token=" + newToken;
		String resendEmail = contextPath
				+ "/customer/account/resendEmail?token=" + newToken;
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject("Resgiter Confirm");
		String text = String.format(template.getTemplateText(),
				customer.getLastname() + " " + customer.getFirstname(),
				confirmationUrl, resendEmail);
		email.setText(text);
		return email;
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showUpdateAccount(
			Model model,
			HttpSession session,
			@RequestParam(value = "changePassword", required = false) Integer changePassword) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			UpdateAccount update = new UpdateAccount();
			if (changePassword != null)
				update.setChangePassword(true);
			model.addAttribute("update", update);
			//return "update-account";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		}
		//return "redirect:/customer/account/login";
		return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> updateAccount(
			@RequestBody UpdateAccount account,
			Model model, BindingResult result, HttpSession session)
			throws NoSuchAlgorithmException {
		if (result.hasErrors()) {
			//return "update-account";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = (CustomerEntity) session
					.getAttribute("customer");
			if (customer != null) {
				customer.setFirstname(account.getFirstName());
				customer.setLastname(account.getLastName());
				if (account.isChangePassword()) {
					if (customer.getPassword().equals(
							customerService.hashPassword(account
									.getCurrentPassword()))) {
						customer.setPassword(customerService
								.hashPassword(account.getPassword()));
					} else {
						model.addAttribute("error_update",
								"Invalid current password.");
						//return "update-account";
						return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
					}
				}
				customerService.update(customer);
				model.addAttribute("message_update",
						"The account information has been saved.");
				//return "update-account";
				return new ResponseEntity<PackageAPI>(HttpStatus.OK);
			} else {
				//return "redirect:/customer/account/login";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	
	@RequestMapping(value = "/address", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showAddress(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			CustomerAddressEntity defaultBilling = null;
			CustomerAddressEntity defaultShipping = null;
			if (customer.getDefaultBilling() != null) {
				defaultBilling = customerService
						.getCustomerAddress(new Integer(customer
								.getDefaultBilling()));
			}
			if (customer.getDefaultShipping() != null) {
				defaultShipping = customerService
						.getCustomerAddress(new Integer(customer
								.getDefaultShipping()));
			}
			List<CustomerAddressEntity> listAddress = customerService
					.findAdditionalAddress(customer.getEntityId(),
							customer.getDefaultBilling(),
							customer.getDefaultShipping());
			model.addAttribute("defaultBilling", defaultBilling);
			model.addAttribute("defaultShipping", defaultShipping);
			model.addAttribute("listAddress", listAddress);
			//return "address";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/address/new", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showNewAddress(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			countryList = new HashMap<String, String>();
			countryList.put("VN", "Viet Nam");
			regionList = new HashMap<String, String>();
			regionList.put("1", "Ho Chi Minh");
			regionList.put("2", "Ha Noi");
			model.addAttribute("address", new AddressAccount());
			model.addAttribute("countryList", countryList);
			model.addAttribute("regionList", regionList);
			//return "update-address";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/address/new", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> addNewAddress(
			@ModelAttribute("address") @Validated AddressAccount address,
			Model model, HttpSession session, BindingResult result) {
		if (result.hasErrors()) {
			//return "update-address";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = (CustomerEntity) session
					.getAttribute("customer");
			if (customer != null) {
				address.setRegion(regionList.get(address.getRegionId()));
				address.setCountry(countryList.get(address.getCountryId()));
				customerService.saveAdress(address, customer);
				model.addAttribute("message", "The address has been saved.");
				//return "redirect:/customer/account/address";
				return new ResponseEntity<PackageAPI>(HttpStatus.OK);
			} else {
				//return "redirect:/customer/account/login";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}
		}
	}
	
	@RequestMapping(value = "/address/edit/id/{entityId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showUpdateAddress(Model model,
			@PathVariable("entityId") Integer id, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			CustomerAddressEntity cusAddress = customerService
					.getCustomerAddress(id);
			AddressAccount address;
			if (cusAddress == null) {
				//return "redirect:/customer/account/address";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			} else {
				address = new AddressAccount();
				address.setEntityId(cusAddress.getEntityId());
				address.setFirstName(cusAddress.getFirstname());
				address.setLastName(cusAddress.getLastname());
				address.setTelephone(cusAddress.getPhone());
				address.setStreet(cusAddress.getStreet());
				address.setRegionId(cusAddress.getRegionId());
				address.setCountryId(cusAddress.getCountryId());
				countryList = new HashMap<String, String>();
				countryList.put("VN", "Viet Nam");
				regionList = new HashMap<String, String>();
				regionList.put("1", "Ho Chi Minh");
				regionList.put("2", "Ha Noi");
				model.addAttribute("address", address);
				model.addAttribute("defaultBilling",
						customer.getDefaultBilling());
				model.addAttribute("defaultShipping",
						customer.getDefaultShipping());
				model.addAttribute("countryList", countryList);
				model.addAttribute("regionList", regionList);
				//return "edit-address";
				return new ResponseEntity<PackageAPI>(HttpStatus.OK);
			}
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	
	////////////////////////////////////////////
	@RequestMapping(value = "/address/edit/id/{entityId}", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> updateAddress(
			@ModelAttribute("address") @Validated AddressAccount address,
			Model model, HttpSession session, BindingResult result) {
		if (result.hasErrors()) {
			//return "update-address";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = (CustomerEntity) session
					.getAttribute("customer");
			if (customer != null) {
				address.setRegion(regionList.get(address.getRegionId()));
				address.setCountry(countryList.get(address.getCountryId()));
				customerService.updateAdress(address, customer);
				model.addAttribute("message", "The address has been update.");
				//return "redirect:/customer/account/address";
				return new ResponseEntity<PackageAPI>(HttpStatus.OK);
			} else {
				//return "redirect:/customer/account/login";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}

		}
	}

	@RequestMapping(value = "/address/delete/id/{entityId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> deleteAddress(Model model,
			@PathVariable("entityId") Integer id, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			if (id == null) {
				//return "redirect:/customer/account/address";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}
			customerService.deleteCustomerAddress(id);
			model.addAttribute("message", "The address has been deleted.");
			//return "redirect:/customer/account/address";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}

	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public String showForgotPassword() {
		return "forgot-password";
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> forgotPassword(@RequestParam("email") String customerEmail,
			HttpServletRequest request, Model model) {
		if (customerEmail == null || "".equals(customerEmail)) {
			model.addAttribute("error",
					messageSource.getMessage("common.required", null, null));
			//return "forgot-password";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else if (!pattern.matcher(customerEmail).matches()) {
			model.addAttribute("error", messageSource.getMessage(
					"common.validation.email", null, null));
			//return "forgot-password";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		} else {
			CustomerEntity customer = customerService
					.findByEmail(customerEmail);
			if (customer == null) {
				model.addAttribute("error", "Email not exits in databases.");
				//return "forgot-password";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}
			String token = UUID.randomUUID().toString();
			customerService.createVerificationTokenForUser(customer, token,
					"reset-pass");
			String appUrl = getBaseUrl(request);
			SimpleMailMessage email = constructResetTokenEmail(appUrl,
					request.getLocale(), token, customer);
			mailSender.send(email);
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		}
	}

	private SimpleMailMessage constructResetTokenEmail(String contextPath,
			Locale locale, String token, CustomerEntity customer) {
		EmailTemplate template = emailService.getEmailTemplate("forgot");
		String recipientAddress = customer.getEmail();
		String resetPassword = contextPath
				+ "/customer/account/changePassword?id="
				+ customer.getEntityId() + "&token=" + token;
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(template.getTemplateSubject());
		String text = String.format(template.getTemplateText(),
				customer.getLastname() + " " + customer.getFirstname(),
				resetPassword);
		email.setText(text);
		return email;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showChangePasswordPage(Locale locale, Model model,
			@RequestParam("id") Integer id, @RequestParam("token") String token) {
		VerificationToken verificationToken = customerService
				.getVerificationToken(token);
		CustomerEntity customer = verificationToken.getCustomerEntity();
		if (token == null || id == null || id != customer.getEntityId()) {
			model.addAttribute("message", "Not token extits in database!");
			//return "redirect:/customer/account/baduser";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime()
				.getTime()) <= 0) {
			model.addAttribute("message", "Token has expired!");
			//return "redirect:/customer/account/baduser";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
		model.addAttribute("entityId", customer.getEntityId());
		//return "update-password";
		return new ResponseEntity<PackageAPI>(HttpStatus.OK);
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> changePasswordPage(@RequestParam("password") String password,
			@RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("entityId") int entityId)
			throws NoSuchAlgorithmException {
		CustomerEntity customer = customerService.getCustomerId(entityId);
		customer.setPassword(customerService.hashPassword(password));
		customerService.update(customer);
		//return "redirect:/customer/account/login";
		return new ResponseEntity<PackageAPI>(HttpStatus.OK);
	}

	@RequestMapping(value = "/myorder", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> order(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			List<OrderModel> orders = customerService.myOrders(customer);
			model.addAttribute("orders", orders);
			//return "myorders";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/myorder/cancle/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> cancleOrder(Model model, HttpSession session,
			@PathVariable("id") int id) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			SalesOrder order = customerService.getOrder(id);
			if (order != null) {
				Calendar cal = Calendar.getInstance();
				long time =  cal.getTime().getTime() - order.getCreatedAt().getTime();
				if ((cal.getTime().getTime() - order.getCreatedAt().getTime()) <= 10800*1000){
					model.addAttribute("message", "Orders has been canceled.");
					customerService.cancelOrder(order);
					SimpleMailMessage email = constructCancleOrdersEmail(customer, id);
					mailSender.send(email);
				} else {
					model.addAttribute("message", "Your order has placed more than 3 hours should not be canceled.");
				}
			} else {
				model.addAttribute("error", "Orders not exits in database.");
			}
			//return "redirect:/customer/account/myorder";
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			//return "redirect:/customer/account/login";
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	private SimpleMailMessage constructCancleOrdersEmail(
			CustomerEntity customer, int id) {
		EmailTemplate template = emailService.getEmailTemplate("cancle");
		String recipientAddress = customer.getEmail();
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject(template.getTemplateSubject());
		String text = String.format(template.getTemplateText(),
				customer.getLastname() + " " + customer.getFirstname(), id);
		email.setText(text);
		return email;
	}
	
}
