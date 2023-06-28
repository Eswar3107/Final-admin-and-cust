package eStoreProduct.controller;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eStoreProduct.DAO.cartDAO;
import eStoreProduct.DAO.customerDAO;
import eStoreProduct.model.custCredModel;

@Controller
public class homeController {
    private static final Logger logger = LoggerFactory.getLogger(homeController.class);

    private final customerDAO cdao;
    private final cartDAO cd;

    // Dependency injection
    @Autowired
    public homeController(customerDAO customerdao, cartDAO cd) {
        cdao = customerdao;
        this.cd = cd;
    }

    // URL mapping for home page
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHomePage(Model model) {
        logger.info("Requested home page");
        // Call the view
        return "home";
    }

    // URL mapping for logged-in user to get back to home
    @RequestMapping(value = "/loggedIn", method = RequestMethod.GET)
    public String getHomeForLoggedUser(Model model) {
        logger.info("Requested home page for logged-in user");
        // Call the view
        return "home";
    }

    // URL mapping to open signup page for new customer
    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String getSignUpPage(Model model) {
        logger.info("Requested sign up page");
        // Call the view
        return "signUp";
    }

    // URL mapping to open signin page
    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String getSignInPage(Model model) {
        logger.info("Requested sign in page");
        // Call the view
        return "signIn";
    }

    // URL mapping when customer completed the signup form
    @RequestMapping(value = "/signInCreateAccount", method = RequestMethod.POST)
    public String createAccount(@Validated custCredModel ccm, Model model) {
        logger.info("Received sign-in create account request");
        // Add customer to database
        boolean b = cdao.createCustomer(ccm);
        // Set it to the model
        if (b) {
            model.addAttribute("customer", ccm);
        }
        // Call the view
        return "createdMsg";
    }

    // URL mapping to redirect to home page by changing login status
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String userLogout(Model model, HttpSession session) {
        // Get the logged-in session customer
        custCredModel cust = (custCredModel) session.getAttribute("customer");
        // Change the login status and add to model
        boolean flag = false;
        model.addAttribute("fl", flag);
        if (model.containsAttribute("customer")) {
            model.addAttribute("customer", null);
        }
        session.invalidate();
        logger.info("User logged out");
        // Call the view
        return "home";
    }
}
