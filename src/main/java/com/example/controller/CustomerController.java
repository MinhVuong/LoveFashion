package com.example.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.text.View;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
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
import com.example.service.ICustomerService;
import com.example.service.IEmailTemplateService;
import com.example.service.IProductSevice;
import com.example.validator.RegisterFormValidator;

@Controller
@RequestMapping(value = "/customer/account")
@SessionAttributes("customer")
public class CustomerController {

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

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showDashboard(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer == null) {
			return "redirect:/customer/account/login";
		} else {
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
			model.addAttribute("defaultBilling", defaultBilling);
			model.addAttribute("defaultShipping", defaultShipping);
			return "dashboard";
		}

	}

	@RequestMapping(value = "/loginFB", method = RequestMethod.GET)
	public String pressbuttonLoginFB(@RequestParam("code") String code, Model model) throws IOException, NoSuchAlgorithmException {
		// cái này m sẽ zử lý nè.
		// lam sao lay dc gia tri cua GET ?
		//vi du /loginFB?code=asakdjs
		
		// roi ok, chay di
		//dc chua
        
        System.out.println(code);
        
        String url = "https://accounts.google.com/o/oauth2/token";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "client_id=1052950712954-a2nrjpahmp79lb92ahmah2vm9764fmtl.apps.googleusercontent.com&client_secret=yNRv1XfhXKd9jDB856ebgnVN&code="+code+"&grant_type=authorization_code&redirect_uri=http://localhost:8080/customer/account/loginFB";
		
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'POST' request to URL : " + url);
		//System.out.println("Post parameters : " + urlParameters);
		//System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		//System.out.println(response.toString());
		 //response 
		//parse lay ra access _yoken dum t
		JSONParser jsonParser = new JSONParser();

		JSONObject jsonObject;
		String access = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(response.toString());
			access = (String) jsonObject.get("access_token");
			
            
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    
		 //Get email 
		
		
		url = "https://www.googleapis.com/oauth2/v1/userinfo?access_token="+access;
		
		obj = new URL(url);
		con = (HttpsURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
        
		//add request header
		
		con.setRequestProperty("Accept-Charset", "UTF-8");
		responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		//System.out.println("Response Code : " + responseCode);
		Charset charset = Charset.forName("UTF8");
		in = new BufferedReader(
		        new InputStreamReader(con.getInputStream(),charset));
		
		 response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());
		jsonParser = new JSONParser();

		
		String email = null;
		String fname = null;
		String lname = null;
		String password = "fuck";
		String pass = null;
		try {
			pass = customerService.hashPassword(password);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			jsonObject = (JSONObject) jsonParser.parse(response.toString());
			email = (String) jsonObject.get("email");
			fname = (String) jsonObject.get("family_name");
			lname = (String) jsonObject.get("given_name");
			
			
            
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(email.toString());
		            // get a String from the JSON object
		//xong
		//gio setup dum tao cai get use by email di
		
		CustomerEntity customer = customerService.findByEmail(email);
		if (customer == null) {
			RegisterModel register = new RegisterModel();
			register.setEmail(email);
			register.setPassword(pass);
			register.setFirstName(fname);
			register.setLastName(lname);
			customer = customerService.register(register, true);
			model.addAttribute("customer", customer);
			
			return "redirect:/customer/account/";
		} else {
			customer.setLogdate(new Date());
			customer.setLognum((short) (customer.getLognum() + 1));
			customerService.update(customer);
			model.addAttribute("customer", customer);
			//
			
			//doan nao set cookie dau?
			return "redirect:/customer/account/";
		}
		            

        
		
		//lay code sao
		//String email = "";// gia su day là email lay tu fb.
		//CustomerEntity customer = customerService.getCustomer(email, password);	// cai nay can pass. M lamf ham khac k can pass, chi can email thoi, vi fb xac thuc roi.
		//customer.setLogdate(new Date());
		//customer.setLognum((short) (customer.getLognum() + 1));
		//customerService.update(customer);
		//model.addAttribute("customer", customer);
	
		//doan nao set cookie dau?
		
		
	}
	
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String showLogin(HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null)
			return "redirect:/customer/account/";
		return "login";
	}
	///gio 72n tao euc hạy cái kia thử m xem nha
	// check login nè.
	//giờ giả sử tao bấm vào nút login facebook. nó sẽ trả vể kết quả ở return_url . làm sao để set là với cái email của facebook đó được phép đăng nhập ?
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam("login[username]") String email,
			@RequestParam("login[password]") String password, Model model,
			@RequestParam(value = "returnUrl", required = false) String url)
			throws NoSuchAlgorithmException {
		if (email == null || "".equals(email)) {
			model.addAttribute("email",
					messageSource.getMessage("common.required", null, null));
			return "login";
		} else if (password == null || "".equals(password)) {
			model.addAttribute("password",
					messageSource.getMessage("common.required", null, null));
			return "login";
		} else if (!pattern.matcher(email).matches()) {
			model.addAttribute("email", messageSource.getMessage(
					"common.validation.email", null, null));
			return "login";
		} else if (password.length() < PASSWORD_LENGTH) {
			model.addAttribute("password", messageSource.getMessage(
					"common.validation.password", null, null));
			return "login";
		} else {
			CustomerEntity customer = customerService.getCustomer(email,
					password);
			if (customer == null) {
				model.addAttribute("error_login", messageSource.getMessage(
						"customer.account.login.error", null, null));
				return "login";
			} else {
				customer.setLogdate(new Date());
				customer.setLognum((short) (customer.getLognum() + 1));
				customerService.update(customer);
				model.addAttribute("customer", customer);
				if (url != null && !"".equals(url)) {
					return "redirect:" + url;
				}
				//doan nao set cookie dau?
				return "redirect:/customer/account/";
			}
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(WebRequest request, SessionStatus status) {
		status.setComplete();
		request.removeAttribute("customer", WebRequest.SCOPE_SESSION);
		return "redirect:/customer/account/login";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String showRegister(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null)
			return "redirect:/customer/account/";
		model.addAttribute("account", new RegisterModel());
		return "register";
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(
			@ModelAttribute("account") @Validated RegisterModel account,
			BindingResult result, Model model, HttpServletRequest request)
			throws NoSuchAlgorithmException {
		if (result.hasErrors()) {
			return "register";
		} else {
			CustomerEntity customer = customerService.register(account,false);
			if (customer == null) {
				model.addAttribute("error_register", messageSource.getMessage(
						"customer.account.register.error.E1", null, null));
				return "register";
			} else {
				String appUrl = this.getBaseUrl(request);
				eventPublisher.publishEvent(new OnRegistrationCompleteEvent(
						this, customer, request.getLocale(), appUrl));
				return "redirect:/customer/account/login";
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

	@RequestMapping(value = "/baduser", method = RequestMethod.GET)
	public String showBadUser() {
		return "bad-user";
	}

	@RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
	public String confirmRegistration(WebRequest request, Model model,
			@RequestParam("token") String token) {
		VerificationToken verificationToken = customerService
				.getVerificationToken(token);
		if (verificationToken == null) {
			model.addAttribute("message", "Not token extits in database!");
			return "redirect:/customer/account/baduser";
		}
		CustomerEntity customer = verificationToken.getCustomerEntity();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime()
				.getTime()) <= 0) {
			model.addAttribute("message", "Token has expired!");
			return "redirect:/customer/account/baduser";
		}
		customer.setIsActive((short) 1);
		customerService.update(customer);
		return "redirect:/customer/account/login";
	}

	@RequestMapping(value = "/resendEmail", method = RequestMethod.GET)
	public String resendRegistrationToken(HttpServletRequest request,
			@RequestParam("token") String existingToken, Model model) {
		VerificationToken newToken = customerService
				.generateNewVerificationToken(existingToken);
		if (newToken != null) {
			CustomerEntity customer = newToken.getCustomerEntity();
			String appUrl = this.getBaseUrl(request);
			SimpleMailMessage email = constructResendVerificationTokenEmail(
					appUrl, request.getLocale(), newToken, customer);
			mailSender.send(email);
			return "redirect:/customer/account/login";
		}
		model.addAttribute("message", "Not token extits in database!");
		return "redirect:/customer/account/baduser";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String showUpdateAccount(
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
			return "update-account";
		}
		return "redirect:/customer/account/login";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String updateAccount(
			@ModelAttribute("update") @Validated UpdateAccount account,
			Model model, BindingResult result, HttpSession session)
			throws NoSuchAlgorithmException {
		if (result.hasErrors()) {
			return "update-account";
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
						return "update-account";
					}
				}
				customerService.update(customer);
				model.addAttribute("message_update",
						"The account information has been saved.");
				return "update-account";
			} else {
				return "redirect:/customer/account/login";
			}
		}
	}

	@RequestMapping(value = "/address", method = RequestMethod.GET)
	public String showAddress(Model model, HttpSession session) {
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
			return "address";
		} else {
			return "redirect:/customer/account/login";
		}

	}

	@RequestMapping(value = "/address/new", method = RequestMethod.GET)
	public String showNewAddress(Model model, HttpSession session) {
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
			return "update-address";
		} else {
			return "redirect:/customer/account/login";
		}
	}

	@RequestMapping(value = "/address/new", method = RequestMethod.POST)
	public String addNewAddress(
			@ModelAttribute("address") @Validated AddressAccount address,
			Model model, HttpSession session, BindingResult result) {
		if (result.hasErrors()) {
			return "update-address";
		} else {
			CustomerEntity customer = (CustomerEntity) session
					.getAttribute("customer");
			if (customer != null) {
				address.setRegion(regionList.get(address.getRegionId()));
				address.setCountry(countryList.get(address.getCountryId()));
				customerService.saveAdress(address, customer);
				model.addAttribute("message", "The address has been saved.");
				return "redirect:/customer/account/address";
			} else {
				return "redirect:/customer/account/login";
			}
		}
	}

	@RequestMapping(value = "/address/edit/id/{entityId}", method = RequestMethod.GET)
	public String showUpdateAddress(Model model,
			@PathVariable("entityId") Integer id, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			CustomerAddressEntity cusAddress = customerService
					.getCustomerAddress(id);
			AddressAccount address;
			if (cusAddress == null) {
				return "redirect:/customer/account/address";
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
				return "edit-address";
			}
		} else {
			return "redirect:/customer/account/login";
		}
	}

	@RequestMapping(value = "/address/edit/id/{entityId}", method = RequestMethod.POST)
	public String updateAddress(
			@ModelAttribute("address") @Validated AddressAccount address,
			Model model, HttpSession session, BindingResult result) {
		if (result.hasErrors()) {
			return "update-address";
		} else {
			CustomerEntity customer = (CustomerEntity) session
					.getAttribute("customer");
			if (customer != null) {
				address.setRegion(regionList.get(address.getRegionId()));
				address.setCountry(countryList.get(address.getCountryId()));
				customerService.updateAdress(address, customer);
				model.addAttribute("message", "The address has been update.");
				return "redirect:/customer/account/address";
			} else {
				return "redirect:/customer/account/login";
			}

		}
	}

	@RequestMapping(value = "/address/delete/id/{entityId}", method = RequestMethod.GET)
	public String deleteAddress(Model model,
			@PathVariable("entityId") Integer id, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			if (id == null) {
				return "redirect:/customer/account/address";
			}
			customerService.deleteCustomerAddress(id);
			model.addAttribute("message", "The address has been deleted.");
			return "redirect:/customer/account/address";
		} else {
			return "redirect:/customer/account/login";
		}

	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
	public String showForgotPassword() {
		return "forgot-password";
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public String forgotPassword(@RequestParam("email") String customerEmail,
			HttpServletRequest request, Model model) {
		if (customerEmail == null || "".equals(customerEmail)) {
			model.addAttribute("error",
					messageSource.getMessage("common.required", null, null));
			return "forgot-password";
		} else if (!pattern.matcher(customerEmail).matches()) {
			model.addAttribute("error", messageSource.getMessage(
					"common.validation.email", null, null));
			return "forgot-password";
		} else {
			CustomerEntity customer = customerService
					.findByEmail(customerEmail);
			if (customer == null) {
				model.addAttribute("error", "Email not exits in databases.");
				return "forgot-password";
			}
			String token = UUID.randomUUID().toString();
			customerService.createVerificationTokenForUser(customer, token,
					"reset-pass");
			String appUrl = getBaseUrl(request);
			SimpleMailMessage email = constructResetTokenEmail(appUrl,
					request.getLocale(), token, customer);
			mailSender.send(email);
			return "redirect:/customer/account/login";
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

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String showChangePasswordPage(Locale locale, Model model,
			@RequestParam("id") Integer id, @RequestParam("token") String token) {
		VerificationToken verificationToken = customerService
				.getVerificationToken(token);
		CustomerEntity customer = verificationToken.getCustomerEntity();
		if (token == null || id == null || id != customer.getEntityId()) {
			model.addAttribute("message", "Not token extits in database!");
			return "redirect:/customer/account/baduser";
		}
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime()
				.getTime()) <= 0) {
			model.addAttribute("message", "Token has expired!");
			return "redirect:/customer/account/baduser";
		}
		model.addAttribute("entityId", customer.getEntityId());
		return "update-password";
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePasswordPage(@RequestParam("password") String password,
			@RequestParam("confirmPassword") String confirmPassword,
			@RequestParam("entityId") int entityId)
			throws NoSuchAlgorithmException {
		CustomerEntity customer = customerService.getCustomerId(entityId);
		customer.setPassword(customerService.hashPassword(password));
		customerService.update(customer);
		return "redirect:/customer/account/login";
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

	@RequestMapping(value = "/myorder", method = RequestMethod.GET)
	public String order(Model model, HttpSession session) {
		CustomerEntity customer = (CustomerEntity) session
				.getAttribute("customer");
		if (customer != null) {
			List<OrderModel> orders = customerService.myOrders(customer);
			model.addAttribute("orders", orders);
			return "myorders";
		} else {
			return "redirect:/customer/account/login";
		}
	}

	@RequestMapping(value = "/myorder/cancle/{id}", method = RequestMethod.GET)
	public String cancleOrder(Model model, HttpSession session,
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
			return "redirect:/customer/account/myorder";
		} else {
			return "redirect:/customer/account/login";
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
