package eStoreProduct.controller;

import java.util.*;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import eStoreProduct.DAO.OrderDAOView;
import eStoreProduct.DAO.ProductDAO;
import eStoreProduct.model.OrdersViewModel;
import eStoreProduct.model.Product;
import eStoreProduct.model.custCredModel;
import eStoreProduct.utility.ProductStockPrice;
@Controller
public class customerOrderController {
	//@Autowired
	//private  ProductDAO pdaoimp;
	
	@Autowired
	 private OrderDAOView orderdaov;
	
	  @RequestMapping("/CustomerOrdersProfile")
	  public String showOrders(Model model, HttpSession session) {
		  System.out.println("inshow");
			custCredModel cust = (custCredModel) session.getAttribute("customer");

	    List<OrdersViewModel> orderProducts = orderdaov. getorderProds(cust.getCustId());
	    for(OrdersViewModel o:orderProducts)
	    {
			  System.out.println("hi order"+o.getName());
	
	    }
	    
	    model.addAttribute("orderProducts", orderProducts);
	    return "orders";
	  }
	  @GetMapping("/productDetails")
	    public String getProductDetails(@RequestParam("id") int productId, Model model, HttpSession session) {
			custCredModel cust = (custCredModel) session.getAttribute("customer");
		  OrdersViewModel product = orderdaov.OrdProductById(cust.getCustId(), productId);
	        model.addAttribute("product", product);
	        return "OrdProDetails";
	    }
	  
	  @PostMapping("/cancelOrder")
	  @ResponseBody
	  public String cancelOrder(@RequestParam("orderproId") Integer productId,@RequestParam("orderId") int orderId) {
		  orderdaov.cancelorderbyId(productId, orderId);

System.out.println("cancel order");
boolean allProductsCancelled = orderdaov.areAllProductsCancelled(orderId);
		    if (allProductsCancelled) {
		        // Update the shipment status of the order in slam_Orders table
		        orderdaov.updateOrderShipmentStatus(orderId, "cancelled");
		    }

		    return "Order with ID " + productId + orderId + " has been cancelled.";
	  }
	  
	  
	  @RequestMapping(value = "/trackOrder", method = RequestMethod.GET)
	  @ResponseBody
	  public String trackOrder(@RequestParam("orderproId") int productId,@RequestParam("orderId") int orderId) {
	      // Retrieve the shipment status for the given order ID
	      String shipmentStatus = orderdaov.getShipmentStatus(productId,orderId);
	      
	      return shipmentStatus;
	  }
	  
	  @RequestMapping(value = "/sortorders", method = RequestMethod.POST)
		public String sortProducts(@RequestParam("sortOrder") String sortOrder, Model model, HttpSession session) {
			// Sort the products based on the selected sorting option
			custCredModel cust = (custCredModel) session.getAttribute("customer");

			List<OrdersViewModel> ordersList = orderdaov.getorderProds(cust.getCustId());

			if (sortOrder.equals("lowToHigh") || sortOrder.equals("highToLow")) {
				ordersList  = orderdaov.sortProductsByPrice(ordersList , sortOrder);
				model.addAttribute("orderProducts", ordersList );
			}
			// Return the view
			return "orders";
		}
	  
	  

		@RequestMapping(value = "/filterorderProducts", method = RequestMethod.POST)
		public String getFilteredProducts(@RequestParam("priceRange") String priceRange, Model model, HttpSession session) {
			double minPrice;
			double maxPrice;
			custCredModel cust = (custCredModel) session.getAttribute("customer");

			List<OrdersViewModel> productList = orderdaov.getorderProds(cust.getCustId());

			// Parse the selected price range
			if (priceRange.equals("0-500")) {
				minPrice = 0.0;
				maxPrice = 500.0;
			} else if (priceRange.equals("500-1000")) {
				minPrice = 500.0;
				maxPrice = 1000.0;
			} else if (priceRange.equals("1000-2000")) {
				minPrice = 1000.0;
				maxPrice = 2000.0;
			} else if (priceRange.equals("2000-4000")) {
				minPrice = 2000.0;
				maxPrice = 4000.0;
			} else {
				// Default range or invalid option selected
				model.addAttribute("orderProducts", productList);
				return "orders";
			}
			System.out.println("min price  " + minPrice + "    maxprice  " + maxPrice);
			// Call the filterProductsByPriceRange() method from the DAO
			List<OrdersViewModel> filteredList = orderdaov.filterProductsByPriceRange(productList, minPrice, maxPrice);
			model.addAttribute("orderProducts",filteredList );
			System.out.println(filteredList + "in filterProducts");
			return "orders";
		}

	 
//			if (sortby ) {
//				products = pdaoimp.getProductsByCategory(categoryId);
//				System.out.println("hiiiiiiiiiii");
//			} else {
//				products = pdaoimp.getAllProducts();
//				System.out.println("hiiiiiiiiiiiiiiiiiii")
//			}
//			model.addAttribute("products", products);
//			return "productCatalog";
		


}
