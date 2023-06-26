package eStoreProduct.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eStoreProduct.BLL.BLL;
import eStoreProduct.BLL.BLLClass2;
import eStoreProduct.DAO.OrderDAO;
import eStoreProduct.DAO.ProductDAO;
import eStoreProduct.DAO.StockUpdaterDAO;
import eStoreProduct.DAO.cartDAO;
import eStoreProduct.DAO.customerDAO;
import eStoreProduct.DAO.walletDAO;
import eStoreProduct.model.custCredModel;
import eStoreProduct.model.orderModel;
import eStoreProduct.model.wallet;
import eStoreProduct.utility.ProductStockPrice;

@Controller
public class CustomerController {
	customerDAO cdao;
	// BLLClass obj;
	cartDAO cartimp;

	BLL BLL;
	BLLClass2 bl2;
	String buytype = null;
	ProductDAO pdaoimp;
	OrderDAO odao;
	walletDAO wdao;
	StockUpdaterDAO stckdao;
	List<ProductStockPrice> products = null;
	List<ProductStockPrice> product2 = new ArrayList<ProductStockPrice>();
	orderModel om;

	@Autowired
	public CustomerController(cartDAO cartdao, customerDAO customerdao, StockUpdaterDAO stckdao, orderModel om,
			BLLClass2 bl2, BLL bl1, ProductDAO productdao, OrderDAO odao, walletDAO w) {
		cdao = customerdao;
		cartimp = cartdao;
		this.bl2 = bl2;
		this.BLL = bl1;
		pdaoimp = productdao;
		this.odao = odao;
		wdao = w;
		this.om = om;
		this.stckdao = stckdao;
	}

	@RequestMapping(value = "/profilePage")
	public String sendProfilePage(Model model, HttpSession session) {
		custCredModel cust = (custCredModel) session.getAttribute("customer");
		// System.out.println(cust.getCustId());
		model.addAttribute("cust", cust);
		return "profile";
	}

	// on clicking update Profile in profile page
	@RequestMapping(value = "/updateProfile", method = RequestMethod.POST)
	public String userupdate(@ModelAttribute("Customer") custCredModel cust, Model model, HttpSession session) {
		cdao.updatecustomer(cust);
		custCredModel custt = cdao.getCustomerById(cust.getCustId());
		if (custt != null) {
			model.addAttribute("cust", custt);
		}
		return "profile";
	}

	/*
	 * @RequestMapping(value = "/payment", method = RequestMethod.POST) public String showPaymentOptions(Model model,
	 * HttpSession session) { return "payment"; }
	 */

	// @GetMapping("/buycartitems")
	// public String confirmbuycart(Model model, HttpSession session) {
	// custCredModel cust = (custCredModel) session.getAttribute("customer");
	// System.out.println("buycartitems");
	// if (cust == null) {
	// return "signIn";
	// }
	// // model.addAttribute("cust", cust);
	// List<ProductStockPrice> products = cartimp.getCartProds(cust.getCustId());
	// model.addAttribute("products", products);
	// System.out.println("in buycartitems");
	// double cartcost = BLL.getCartCost(cust.getCustId());
	// model.addAttribute("cartcost", cartcost);
	// return "paymentcart";
	//
	// }

	@GetMapping("/buycartitems")
	public String confirmbuycart(Model model, HttpSession session) {

		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			System.out.println("Entered into buycartitems");
			BLL.calculateTotalfair(cust1);
			System.out.println("Entered into buycartitems1");

			products = BLL.GetQtyItems2();
			System.out.println("Entered into buycartitems2          " + ProductStockPrice.getTotal());
			model.addAttribute("products", products);
			buytype = "cartproducts";

			wallet Wallet = wdao.getWalletAmount(cust1.getCustId());
			model.addAttribute("Wallet", Wallet);

			return "paymentpreview";
		} else {
			return "signIn";
		}

	}

	@GetMapping("/getOrderId")
	@ResponseBody
	public String getOrderId(@RequestParam(value = "amt") double amt) {
		double amountInPaisa = amt; // Convert amount to paisa
		String orderId = bl2.createRazorpayOrder(amountInPaisa);
		return orderId;
	}

	@GetMapping("/done")
	public String orderCreation(Model model) {
		return "redirect:/OrderCreation";
	}

	@PostMapping("/confirmShipmentAddress")
	@ResponseBody
	public String confirm(@RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "custsaddress") String custsaddress,
			@RequestParam(value = "spincode") String spincode, Model model, HttpSession session) {
		// System.out.println("confirm shipmentaddress");
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		custCredModel cust2 = new custCredModel();
		cust2.setCustMobile(mobile);
		cust2.setCustSAddress(custsaddress);
		cust2.setCustPincode(spincode);
		session.setAttribute("cust2", cust2);
		// cdao.updateshipmentaddress(cust1.getCustId(), shaddr);
		return "OK";

	}

	// @GetMapping("/paymentoptions")
	// public String orderCreate(Model model, HttpSession session) {
	// // String orderId = bl2.createRazorpayOrder(Double.parseDouble((String) session.getAttribute("qtycost")));
	// custCredModel cust = (custCredModel) session.getAttribute("customer");
	// double cartcost = BLL.getCartCost(cust.getCustId());
	// // model.addAttribute("cartcost", cartcost);
	// System.out.println(cartcost + ":tcost");
	// System.out.println("amount in controller before razor pay " + cartcost);
	// String orderId = bl2.createRazorpayOrder(cartcost);
	// model.addAttribute("orderId", orderId);
	// model.addAttribute("amt", (int) cartcost * 100);
	// return "payment-options";
	// }

	@PostMapping("/updateshipment")
	@ResponseBody
	public String handleFormSubmission(@RequestParam(value = "name") String name,
			@RequestParam(value = "custSAddress") String caddress, @RequestParam(value = "custSpincode") String pincode,
			HttpSession session) {
		custCredModel cust = (custCredModel) session.getAttribute("customer");
		boolean isValid = pdaoimp.isPincodeValid(Integer.parseInt(pincode));

		if (isValid) {
			cust.setCustName(name);
			cust.setCustSAddress(caddress);
			cust.setCustSpincode(pincode);
			String update_status = cdao.updateShpimentDetails(cust);
			if (update_status.equals("Updated"))
				return "Valid";
			else
				return "Not Valid";
		}
		return "Not Valid";
	}

	// @GetMapping("/paymentoptions")
	// public String orderCreation(Model model, HttpSession session, @RequestParam("total") double totalAmount) {
	// custCredModel cust1 = (custCredModel) session.getAttribute("customer");
	// String orderId = bl2.createRazorpayOrder(Integer.parseInt(String.valueOf(totalAmount)));
	// long amountInPaisa = (long) (totalAmount * 100); // Convert amount to paisa
	//
	// model.addAttribute("orderId", orderId); // Pass the orderId to the view
	// model.addAttribute("amt", amountInPaisa); // Pass the amount in paisa to the view
	// return "payment-options";
	// }

	@PostMapping(value = "/invoice")
	public String invoice(@RequestParam("paymentReference") String id, @RequestParam("total") String total, Model model,
			HttpSession session, @Validated orderModel om) {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		wallet Wallet = wdao.getWalletAmount(cust1.getCustId());
		double payamount = Double.parseDouble(total);
		double totalamount = ProductStockPrice.getTotal();
		double walletusedamount = totalamount - payamount;
		if (walletusedamount > 0) {
			double x = Wallet.getAmount() - walletusedamount;
			wdao.updatewallet(x, cust1.getCustId());
		}

		if (buytype.equals("cartproducts")) {
			products = BLL.GetQtyItems2();
		} else {
			products = product2;
		}
		om.setGst(BLL.getOrderGST(products));
		model.addAttribute("payment_id", om.getPaymentReference());
		System.out.println("order shipping pincode is: " + om.getShippingPincode());
		odao.insertIntoOrders(om, products);
		for (ProductStockPrice p : products) {
			stckdao.updateStocks(p.getProd_id(), p.getQuantity());
			System.out.println("stock updated");
		}
		System.out.println(id);
		model.addAttribute("customer", cust1);
		model.addAttribute("payid", id);
		model.addAttribute("products", products);
		return "invoice";
	}

	@GetMapping("/buythisproduct")
	public String buythisproduct(@RequestParam(value = "productId", required = true) int productId,
			@RequestParam(value = "qty", required = true) int qty, Model model, HttpSession session)
			throws NumberFormatException, SQLException {
		product2.clear();
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		ProductStockPrice product = BLL.individualTotalfair(cust1, productId, qty);
		product2.add(product);

		buytype = "individual";
		wallet Wallet = wdao.getWalletAmount(cust1.getCustId());
		model.addAttribute("Wallet", Wallet);

		model.addAttribute("products", product2);

		return "paymentpreview";

	}

	@GetMapping("/checkloginornot")
	@ResponseBody
	public String buyproduct(Model model, HttpSession session) throws NumberFormatException, SQLException {
		custCredModel cust1 = (custCredModel) session.getAttribute("customer");
		if (cust1 != null) {
			return "true";
		} else {
			return "false";
		}

	}

}
