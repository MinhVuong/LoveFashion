package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

import com.example.dto.AddressAccount;
import com.example.dto.CartProductModel;
import com.example.dto.CartViewModel;
import com.example.dto.ListProductModel;
import com.example.dto.ProductModel;
import com.example.entity.CatalogProductEntity;
import com.example.entity.CustomerAddressEntity;
import com.example.entity.CustomerEntity;
import com.example.entity.EmailTemplate;
import com.example.entity.SalesOrderAddress;
import com.example.service.ICustomerService;
import com.example.service.IProductSevice;

@Controller
@SessionAttributes("cart")
public class ProductController {

	@Autowired
	private IProductSevice proService;
	@Autowired
	private ICustomerService cutomerService;
	@Autowired
	private JavaMailSender mailSender;
	
	private final int MAX_LIMIT = 12;

	private Map<String, String> countryList;

	private Map<String, String> regionList;

	@RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
	public String showListProductCategory(
			HttpServletRequest request,
			Model model,
			@PathVariable("id") Integer id,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "price", required = false) String price) {
		if (page == 0) {
			page = 1;
		}
		String url = request.getRequestURL().append('?')
				.append(request.getQueryString()).toString();
		if(url.indexOf("?null") != -1){
			url = url.replace("null", "");
		}
		List<ListProductModel> lists = proService.getListProductByCategoy(id,
				color, price, size);
		if (lists.size() == 0) {
			model.addAttribute("message", "There are no products matching the selection.");
		} else {
			int noOfPage = lists.size() / MAX_LIMIT;
			if (lists.size() % MAX_LIMIT != 0)
				noOfPage++;
			if(page > noOfPage){
				model.addAttribute("message", "There are no products matching the selection.");
			} else{
				model.addAttribute("lists", lists);
				model.addAttribute("page", noOfPage);
				model.addAttribute("currentPage", page);
				model.addAttribute("category_id", id);
				model.addAttribute("url", replaceUrl(url));
			}
		}
		model.addAttribute("listColor", convert(color));
		model.addAttribute("listSize", convert(size));
		if (price != null && !"".equals(price)) {
			String[] splits = price.split("-");
			model.addAttribute("min", splits[0]);
			model.addAttribute("max", splits[1]);
		}
		return "list";
	}

	private Map<String, String> convert(String str) {
		Map<String, String> map = new HashMap<String, String>();
		if (str != null && !"".equals(str)) {
			String[] splits = str.split(",");
			for (String item : splits)
				map.put(item, item);
		}
		return map;
	}

	private String replaceUrl(String url) {
		int start = url.indexOf("page");
		if (start != -1) {
			String temp = url.substring(start);
			int end = temp.indexOf("&") + start;
			String result = url.substring(0, start - 1);
			if (end != (start - 1))
				result = result + url.substring(end, url.length());
			return result;
		}
		return url;

	}

	@RequestMapping(value = "/detail/{id}")
	public String showDetail(@PathVariable("id") int id, Model model) {
		ProductModel product = proService.getProduct(id);
		model.addAttribute("product", product);
		return "detail";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(
			Model model,
			@RequestParam("q") String key,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "price", required = false) String price,
			HttpServletRequest request) {
		if (page == 0) {
			page = 1;
		}
		String url = request.getRequestURL().append('?')
				.append(request.getQueryString()).toString();
		if(url.indexOf("?null") != -1){
			url = url.replace("null", "");
		}
		List<ListProductModel> lists = proService.search(key, color, size,
				price);
		if (lists.size() == 0) {
			model.addAttribute("message",
					"There are no products matching the search.");
		} else {
			int noOfPage = lists.size() / MAX_LIMIT;
			if (lists.size() % MAX_LIMIT != 0)
				noOfPage++;
			if(page > noOfPage){
				model.addAttribute("message", "There are no products matching the selection.");
			}
			else{
				model.addAttribute("lists", lists);
				model.addAttribute("page", noOfPage);
				model.addAttribute("currentPage", page);
				model.addAttribute("url", replaceUrl(url));
			}
		}
		model.addAttribute("listColor", convert(color));
		model.addAttribute("listSize", convert(size));
		if (price != null && !"".equals(price)) {
			String[] splits = price.split("-");
			model.addAttribute("min", splits[0]);
			model.addAttribute("max", splits[1]);
		}
		return "search";
	}

	@RequestMapping(value = "/cartpro/index/add", method = RequestMethod.POST)
	@ResponseBody
	public CartProductModel addCart(@RequestBody CartProductModel cartModel,
			HttpSession session, Model model) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart == null) {
			cart = new CartViewModel();
			model.addAttribute("cart", cart);
		}
		CatalogProductEntity result;
		boolean isFlat = false;
		if (cartModel.getColor() == null || "".equals(cartModel.getColor())
				|| cartModel.getSize() == null
				|| "".equals(cartModel.getSize())) {
			result = proService.getSimpleProduct(cartModel.getSku());
			if (result != null) {
				cartModel.setColor(result.getColorValue());
				cartModel.setSize(result.getSizeValue());
				cartModel.setName(result.getName());
				cartModel.setPrice(result.getPrice());
				cartModel.setImage(result.getImage());
				cartModel.setQty(1);
				isFlat = true;
			}
		} else {
			result = proService.getSimpleProduct(cartModel.getSku(),
					cartModel.getColor(), cartModel.getSize());
			if (result != null) {
				cartModel.setName(result.getName());
				cartModel.setPrice(result.getPrice());
				cartModel.setImage(result.getImage());
			}
		}
		cartModel.setListColors(proService.getColorSimpleProduct(cartModel
				.getSku()));
		cartModel.setListSizes(proService.getSizeSimpleProduct(cartModel
				.getSku()));
		cartModel.setSku(result.getSku());
		cartModel.setTax(result.getTaxClassId());
		int index = findIndexProductCartView(cart.getList(), cartModel.getSku());
		if (index != -1) {
			int qty;
			if (isFlat)
				qty = cart.getList().get(index).getQty() + 1;
			else
				qty = cart.getList().get(index).getQty() + cartModel.getQty();
			cart.getList().get(index).setQty(qty);
			cartModel.setQty(cart.getList().get(index).getQty());
		} else {
			cart.getList().add(cartModel);
		}
		cartModel.setIndex(index);
		cart.setSubtotal(sumPrice(cart.getList()));
		cart.setItem(cart.getList().size());
		cartModel.setItem(cart.getItem());
		cartModel.setTotal(cart.getSubtotal());
		return cartModel;
	}

	private int findIndexProductCartView(List<CartProductModel> cart, String sku) {
		for (int i = 0; i < cart.size(); i++) {
			if (sku.equals(cart.get(i).getSku())) {
				return i;
			}
		}
		return -1;
	}

	private double sumPrice(List<CartProductModel> cart) {
		double sum = 0f;
		for (int i = 0; i < cart.size(); i++) {
			sum = sum + cart.get(i).getQty() * cart.get(i).getPrice();
		}
		return sum;
	}

	@RequestMapping(value = "/cartpro/index/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCart(@PathVariable("id") int id, Model model,
			HttpSession session) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart == null) {
			cart = new CartViewModel();
			model.addAttribute("cart", cart);
		}
		if (cart.getList().size() > 0 && id < cart.getList().size()) {
			List<CartProductModel> list = cart.getList();
			list.remove(id);
			cart.setList(list);
		}
		cart.setSubtotal(sumPrice(cart.getList()));
		cart.setItem(cart.getList().size());
		return cart.getSubtotal() + "-" + cart.getItem();
	}

	@RequestMapping(value = "/cartpro/index", method = RequestMethod.GET)
	public String showCartView(Model model, HttpSession session) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart == null || cart.getList().size() == 0) {
			model.addAttribute("message", "The cart view empty.");
		} else {
			cart.setPriceTax(proService.clalTax(cart.getList()));
			cart.setGrandtotal(cart.getSubtotal() + cart.getPriceTax());
		}
		return "cart";
	}

	@RequestMapping(value = "/cartpro/index/edit/{id}", method = RequestMethod.POST)
	@ResponseBody
	public CartProductModel showCartView(
			@RequestBody CartProductModel cartModel, HttpSession session,
			Model model, @PathVariable("id") int index) {
		
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart != null) {
			String sku = cartModel.getSku().substring(0,
					cartModel.getSku().indexOf("-"));
			CatalogProductEntity result = proService.getSimpleProduct(sku,
					cartModel.getColor(), cartModel.getSize());
			if (result != null) {
				cart.getList().get(index).setColor(cartModel.getColor());
				cart.getList().get(index).setImage(result.getImage());
				cart.getList().get(index).setSize(cartModel.getSize());
				cart.getList().get(index).setSku(result.getSku());
				cart.getList().get(index).setPrice(result.getPrice());
				cart.getList().get(index).setQty(cartModel.getQty());
				cart.getList().get(index).setTotal(sumPrice(cart.getList()));
				cart.setSubtotal(sumPrice(cart.getList()));
				cart.setGrandtotal(cart.getSubtotal() + cart.getPriceShipping() + cart.getPriceTax());
			}
			return cart.getList().get(index);
		}
		return new CartProductModel();
	}

	@RequestMapping(value = "/checkout/onepage", method = RequestMethod.GET)
	public String showCheckOut(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "step", required = false, defaultValue = "0") int step) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if(cart != null && cart.getList().size() > 0){
			CustomerEntity customer = (CustomerEntity) session.getAttribute("customer");
			CustomerAddressEntity defaultBilling = new CustomerAddressEntity();
			CustomerAddressEntity defaultShipping = new CustomerAddressEntity();
			if(customer != null){
				if (customer.getDefaultBilling() != null) {
					defaultBilling = cutomerService.getCustomerAddress(new Integer(customer
									.getDefaultBilling()));
				}
				if (customer.getDefaultShipping() != null) {
					defaultShipping = cutomerService.getCustomerAddress(new Integer(customer
									.getDefaultShipping()));
				}
				if(step == 0)
					session.setAttribute("step", 1);
			}
			model.addAttribute("url", getBaseUrl(request));
			model.addAttribute("shipping", new AddressAccount());
			model.addAttribute("billing", new AddressAccount());
			model.addAttribute("defaultBilling", defaultBilling);
			model.addAttribute("defaultShipping", defaultShipping);
			countryList = new HashMap<String, String>();
			countryList.put("VN", "Viet Nam");
			regionList = new HashMap<String, String>();
			regionList.put("1", "Ho Chi Minh");
			regionList.put("2", "Ha Noi");
			model.addAttribute("countryList", countryList);
			model.addAttribute("regionList", regionList);
			return "check-out";
		}
		return "redirect:/cartpro/index";
	}
	
	private String getBaseUrl(HttpServletRequest request) {
		if ((request.getServerPort() == 80) || (request.getServerPort() == 443))
			return request.getScheme() + "://" + request.getServerName()
					+ request.getContextPath();
		else
			return request.getScheme() + "://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getContextPath();
	}

	@RequestMapping(value = "/checkout/onepage/method-shipping", method = RequestMethod.POST)
	public String calculorShipping(Model model, HttpSession session,
			@RequestParam("methodShipping") String method, 
			@RequestParam(value = "returnUrl", required = false, defaultValue = "") String url) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart != null && cart.getList().size() > 0) {
			if ("Free Shipping".equals(method)) {
				cart.setMethodShipping(method);
				cart.setPriceShipping(0f);
			} else {
				cart.setMethodPayement(method);
				cart.setPriceShipping(countItemInCart(cart.getList()) * 1);
			}
			cart.setGrandtotal(cart.getSubtotal() + cart.getPriceShipping());
			if("1".equals(url))
				return "redirect:/cartpro/index";
			else{
				session.setAttribute("step", 4);
				return "redirect:/checkout/onepage?step=4";
			}
		}
		return "redirect:/cartpro/index";
	}

	public int countItemInCart(List<CartProductModel> list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++)
			count = count + list.get(i).getQty();
		return count;
	}

	@RequestMapping(value = "/checkout/onepage/billing", method = RequestMethod.POST)
	public String billingAddress(Model model,
			@ModelAttribute("billing") AddressAccount address,
			HttpSession session) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart != null) {
			SalesOrderAddress orderAddress = convertAddress(address);
			orderAddress.setAddressType("billing");
			cart.setBilling(orderAddress);
			session.setAttribute("step", 2);
		}
		return "redirect:/checkout/onepage?step=2";

	}

	@RequestMapping(value = "/checkout/onepage/shipping", method = RequestMethod.POST)
	public String shippingAddress(Model model,
			@ModelAttribute("shipping") AddressAccount address,
			HttpSession session) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart != null) {
			SalesOrderAddress orderAddress = convertAddress(address);
			orderAddress.setAddressType("shipping");
			cart.setShipping(orderAddress);
			session.setAttribute("step", 3);
		}
		return "redirect:/checkout/onepage?step=3";

	}

	@RequestMapping(value = "/checkout/onepage/payment", method = RequestMethod.POST)
	public String payment(Model model, @RequestParam("payment") String payment,
			HttpSession session) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		if (cart != null) {
			cart.setMethodPayement(payment);
			session.removeAttribute("step");
		}
		return "redirect:/checkout/onepage/complele";
	}

	@RequestMapping(value = "/checkout/onepage/complele", method = RequestMethod.GET)
	public String complateCheckout(Model model, HttpSession session, 
			WebRequest request, SessionStatus status) {
		CartViewModel cart = (CartViewModel) session.getAttribute("cart");
		CustomerEntity customer = (CustomerEntity) session.getAttribute("customer");
		if (cart != null && customer != null) {
			proService.complate(cart, customer);
			mailSender.send(constructSuccessOrdersEmail(customer, cart));
			status.setComplete();
			request.removeAttribute("cart", WebRequest.SCOPE_SESSION);
			model.addAttribute("message", "You have successfully ordered! Thank you!");
			return "checkout-complete";
		}
		return "redirect:/cartpro/index";
		
	}
	
	@RequestMapping(value = "/checkout/onepage/fail", method = RequestMethod.GET)
	public String complateCheckout() {
		return "checkout-fail";
		
	}

	private SalesOrderAddress convertAddress(AddressAccount address) {
		SalesOrderAddress orderAddress = new SalesOrderAddress();
		orderAddress.setFirstname(address.getFirstName());
		orderAddress.setLastname(address.getLastName());
		orderAddress.setStreet(address.getStreet());
		orderAddress.setCountryId(address.getCountryId());
		orderAddress.setRegion(address.getRegionId());
		orderAddress.setTelephone(address.getTelephone());
		return orderAddress;
	}
	
	private SimpleMailMessage constructSuccessOrdersEmail(CustomerEntity customer, CartViewModel cart) {
		String recipientAddress = customer.getEmail();
		SimpleMailMessage email = new SimpleMailMessage();
		email.setTo(recipientAddress);
		email.setSubject("Success order");
		String text = "You have successfully ordered. We will ship your order in the fastest time. "
				+ "Thank you for ordering at our store."
				+ "\n\nDetail: "
				+ "\n\nName: " + customer.getFirstname() + " " + customer.getLastname()
				+ "\n\nAddress: " + cart.getShipping().getStreet()
				+ "\n\nTotal: " + cart.getGrandtotal() + "$"
				+ "\n\nPhone: " + cart.getShipping().getTelephone()
				+ "\n\nThank you!";
		email.setText(text);
		return email;
	}
}
