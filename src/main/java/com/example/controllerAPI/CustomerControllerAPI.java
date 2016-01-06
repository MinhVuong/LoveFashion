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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
import com.example.modelAPI.CustomerAPI;
import com.example.modelAPI.ListOrderAPI;
import com.example.modelAPI.Message;
import com.example.modelAPI.PackageAPI;
import com.example.modelAPI.ShowAddress;
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

	@RequestMapping(value = { "/{id}" },method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	public ResponseEntity<PackageAPI> showDashboard(@PathVariable("id") int id) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer == null) {
			return new ResponseEntity<PackageAPI>( HttpStatus.NO_CONTENT);
		} else {
			
			ShowDashBoard showDashBoard = new ShowDashBoard();
			CustomerAPI result = new CustomerAPI();
			result.setEmail(customer.getEmail());
			result.setEntityId(customer.getEntityId());
			result.setFirstname(customer.getFirstname());
			result.setGender(customer.getGender());
			result.setLastname(customer.getLastname());
			result.setPassword(customer.getPassword());
			result.setScore(customer.getScore());
			
			showDashBoard.setDefaultBilling(null);
			showDashBoard.setDefaultShipping(null);
			if (customer.getDefaultBilling() != null) {
				showDashBoard.setDefaultBilling(customerService.getCustomerAddress(new Integer(customer.getDefaultBilling()))); 
			}
			if (customer.getDefaultShipping() != null) {
				showDashBoard.setDefaultShipping(customerService.getCustomerAddress(new Integer(customer.getDefaultShipping())));
			}
			System.out.println("api show dashboard ");
			
			showDashBoard.setCustomer(result);
			return new ResponseEntity<PackageAPI>(showDashBoard, HttpStatus.OK);
		}
	}
	
	@RequestMapping(value = { "/login " },
			method = { RequestMethod.GET },
			produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<CustomerEntity> showLogin() {
		
		System.out.println("api get login ");
		
		CustomerEntity customer = new CustomerEntity();
		ResponseEntity<CustomerEntity> entity = new ResponseEntity<CustomerEntity>(customer, HttpStatus.valueOf(200));
		return entity;
	}
	@RequestMapping(value = { "/login " },
			method = { RequestMethod.POST },
			consumes = { "application/json", "application/xml" },
			produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> submitLogin(@RequestBody CustomerEntity customer_Client) throws NoSuchAlgorithmException {
	
		//System.out.println("api put login");
		String email = customer_Client.getEmail();
		String password = customer_Client.getPassword();
		Message message = new Message();
		if (email == null || "".equals(email)) {
			
			message.setTitle("Login");
			message.setContent("Email null");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(422));
		} else if (password == null || "".equals(password)) {
			
			message.setTitle("Login");
			message.setContent("Password null");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(422));
		} else if (!pattern.matcher(email).matches()) {
			
			message.setTitle("Login");
			message.setContent("Email didn't correct format: @gmail or @yahoo,...");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(422));
		} else if (password.length() < PASSWORD_LENGTH) {
			
			message.setTitle("Login");
			message.setContent("Lengh Password less 6 chars");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(422));
		} else {
			CustomerEntity customer = customerService.getCustomer(email, password);
			if (customer == null) {
				
				message.setTitle("Login");
				message.setContent("Account don't exist in database!"); 
				return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(404));
			} else {
				customer.setLogdate(new Date());
				customer.setLognum((short) (customer.getLognum() + 1));
				customerService.update(customer);
				String userID = customer.getEntityId().toString();
				message.setTitle("Login");
				message.setContent(userID); 
				return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(200));
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
	public ResponseEntity<PackageAPI> register(@RequestBody RegisterModel account){
		Message message = new Message();
		CustomerEntity customer;
		
		try {
			customer = customerService.register(account, false);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			
			message.setTitle("Register");
			message.setContent("Error Server"); 
			return new ResponseEntity<PackageAPI>(message, HttpStatus.valueOf(404));
		}
		if (customer == null) {
			message.setTitle("Register");
			message.setContent("Email had used!"); 
			return new ResponseEntity<PackageAPI>(message, HttpStatus.CONFLICT);
		} else {
			
			message.setTitle("Register");
			message.setContent("Register success");
			
			//String appUrl = this.getBaseUrl(request);
			//eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, customer, request.getLocale(), appUrl));
			
			return new ResponseEntity<PackageAPI>(message, HttpStatus.OK);
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
		Message message = new Message();
		if (verificationToken == null) {
			message.setTitle("Token");
			message.setContent("Not token exist in database!");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.NO_CONTENT);
		}
		CustomerEntity customer = verificationToken.getCustomerEntity();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			message.setTitle("Token");
			message.setContent("Token has expired!");
			return new ResponseEntity<PackageAPI>(message, HttpStatus.GATEWAY_TIMEOUT);
		}
		customer.setIsActive((short) 1);
		customerService.update(customer);
		
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
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		}
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
	
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> showUpdateAccount(@PathVariable(value = "id") Integer id,
			@RequestParam(value = "changePassword", required = false) Integer changePassword) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			UpdateAccount update = new UpdateAccount();
			if (changePassword != null)
				update.setChangePassword(true);
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		}
		return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> updateAccount(@RequestBody UpdateAccount account)
			throws NoSuchAlgorithmException {
		
		CustomerEntity customer = customerService.findByEmail(account.getEmail());
		
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
					
					return new ResponseEntity<PackageAPI>(HttpStatus.valueOf(404));
				}
			}
			customerService.update(customer);
			String id = customer.getEntityId().toString();
			Message message = new Message();
			message.setTitle("Update Account");
			message.setContent(id);
			
			return new ResponseEntity<PackageAPI>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@RequestMapping(value = "/address/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<ShowAddress> showAddress(@PathVariable("id") int id) {
		CustomerEntity customer = customerService.getCustomerId(id);
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
			ShowAddress showAddress = new ShowAddress();
			showAddress.setDefaultBilling(defaultBilling);
			showAddress.setDefaultShipping(defaultShipping);
			showAddress.setListAddress(listAddress);
			return new ResponseEntity<ShowAddress>(showAddress, HttpStatus.OK);
		} else {
			
			return new ResponseEntity<ShowAddress>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/address/new", method = RequestMethod.GET)	// Xem lai sau
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
	
	@RequestMapping(value = "/address/new/{id}", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> addNewAddress(@PathVariable(value = "id") Integer id,
			@RequestBody AddressAccount address) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			countryList = new HashMap<String, String>();
			countryList.put("VN", "Viet Nam");
			regionList = new HashMap<String, String>();
			regionList.put("1", "Ho Chi Minh");
			regionList.put("2", "Ha Noi");
			
			address.setRegion(regionList.get(address.getRegionId()));
			address.setCountry(countryList.get(address.getCountryId()));
			customerService.saveAdress(address, customer);
			
			
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
		
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}
	// Khong can chuyen
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
	@RequestMapping(value = "/address/edit/{id}", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> updateAddress(@PathVariable(value = "id") Integer id,
			@RequestBody AddressAccount address) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			address.setRegion(regionList.get(address.getRegionId()));
			address.setCountry(countryList.get(address.getCountryId()));
			customerService.updateAdress(address, customer);
			
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/address/delete/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> deleteAddress(@PathVariable(value = "id") Integer id) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			if (id == null) {
				//return "redirect:/customer/account/address";
				return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
			}
			customerService.deleteCustomerAddress(id);
			return new ResponseEntity<PackageAPI>(HttpStatus.OK);
		} else {
			return new ResponseEntity<PackageAPI>(HttpStatus.BAD_REQUEST);
		}

	}

	

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST, consumes = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> forgotPassword(@RequestBody CustomerEntity customer_Client, HttpServletRequest request) {
		String customerEmail = customer_Client.getEmail();
		System.out.println(customerEmail);
		System.out.println("api forgot password");
		if (customerEmail == null || "".equals(customerEmail)) {
			return new ResponseEntity<PackageAPI>(HttpStatus.NO_CONTENT);
		} else if (!pattern.matcher(customerEmail).matches()) {
			
			return new ResponseEntity<PackageAPI>(HttpStatus.valueOf(422));		// khong dung dinh dang
		} else {
			
			CustomerEntity customer = customerService.findByEmail(customerEmail);
			if (customer == null) {
				return new ResponseEntity<PackageAPI>(HttpStatus.CONFLICT);
			}
			String token = UUID.randomUUID().toString();
			customerService.createVerificationTokenForUser(customer, token,"reset-pass");
			String appUrl = getBaseUrl(request);
			SimpleMailMessage email = constructResetTokenEmail(appUrl,request.getLocale(), token, customer);
			mailSender.send(email);
			
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
			@RequestParam("id") Integer id, @PathVariable("token") String token) {
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
	public ResponseEntity<PackageAPI> changePasswordPage(@PathVariable("password") String password,
			@RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("entityId") int entityId)
			throws NoSuchAlgorithmException {
		CustomerEntity customer = customerService.getCustomerId(entityId);
		customer.setPassword(customerService.hashPassword(password));
		customerService.update(customer);
		
		return new ResponseEntity<PackageAPI>(HttpStatus.OK);
	}

	@RequestMapping(value = "/myorder/{id}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<ListOrderAPI> order(@PathVariable(value = "id") Integer id) {
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			ListOrderAPI list = new ListOrderAPI();
			list.setListOder(customerService.myOrders(customer));
			
			return new ResponseEntity<ListOrderAPI>(list, HttpStatus.OK);
		} else {
			return new ResponseEntity<ListOrderAPI>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/myorder/cancle/{id},{di}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
	@ResponseBody
	public ResponseEntity<PackageAPI> cancleOrder(@PathVariable(value = "id") Integer id, @PathVariable(value = "di") Integer di) {
		System.out.println(id);
		System.out.println(di);
		CustomerEntity customer = customerService.getCustomerId(id);
		if (customer != null) {
			SalesOrder order = customerService.getOrder(di);
			if (order != null) {
				Calendar cal = Calendar.getInstance();
				long time =  cal.getTime().getTime() - order.getCreatedAt().getTime();
				if ((cal.getTime().getTime() - order.getCreatedAt().getTime()) <= 10800*1000){
					//model.addAttribute("message", "Orders has been canceled.");
					customerService.cancelOrder(order);
					//SimpleMailMessage email = constructCancleOrdersEmail(customer, id);
					//mailSender.send(email);
					return new ResponseEntity<PackageAPI>(HttpStatus.OK);
				} else {
					//model.addAttribute("message", "Your order has placed more than 3 hours should not be canceled.");
					return new ResponseEntity<PackageAPI>(HttpStatus.GATEWAY_TIMEOUT);
				}
			} else {
				//model.addAttribute("error", "Orders not exits in database.");
				return new ResponseEntity<PackageAPI>(HttpStatus.NO_CONTENT);
			}
			
		} else {
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
