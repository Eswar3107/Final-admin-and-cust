package eStoreProduct.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

//import eStoreProduct.ProductsService.RazorpyService;

@WebServlet("/OrderCreation")
//@Component
public class OrderCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//@Autowired
	//private RazorpyService razorpayService;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			RazorpayClient razorpayClient = new RazorpayClient("rzp_test_Eu94k5nuplVQzA", "iC6DFpPEkTIq0UGGQalJir6s");
			JSONObject obj = new JSONObject();
			obj.put("amount", 100*1000); // Replace 1000 with the
									// desired amount value
			obj.put("currency", "INR");
			obj.put("receipt", "hello123");
			obj.put("payment_capture", true);
			Order order = razorpayClient.orders.create(obj);
			String orderId = order.get("id");
			System.out.println(order.toString());
			request.setAttribute("orderId", orderId); // Pass the orderId to the JSP page
			request.setAttribute("username", "lucky");
			request.setAttribute("email", "lucky@gmail.com");
			request.setAttribute("phone", "9133640746");

			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/payment-options.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			System.out.println(e);
			response.getWriter().append("Error occurred while creating order");
		}
	}
}
