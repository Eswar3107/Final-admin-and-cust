package eStoreProduct.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import eStoreProduct.DAO.admin.CategoryReportDAO;
import eStoreProduct.DAO.admin.OrderReportDAO;
import eStoreProduct.model.admin.output.*;
import eStoreProduct.model.admin.entities.*;

@Controller

public class ReportController {
    
 
    private OrderReportDAO odao;
    private CategoryReportDAO crd;
    
    @Autowired
	public ReportController( OrderReportDAO odao,CategoryReportDAO crd) {
	this.odao = odao;
	this.crd=crd;
	}
        //method to generate the gst
	@GetMapping("/generateGSTReport")
	public String generateGSTReport(Model model) {
		//get all the ordered products
	    List<SlamOrderModel> om=odao.getAllOrders();
	    model.addAttribute("orderReport", om);
		//call view
	    return "Reports";
	}
	//method to generate the report based on categories
	@GetMapping("/categoryReport")
	public String categoryReport(Model model) {
	    //List<orderModel> om=odao.getAllOrders();
	    List<CategoryReportViewModel> cl=crd.getCatRep();
	    model.addAttribute("categoryReport", cl);
	    return "catReports";
	}
	
}
