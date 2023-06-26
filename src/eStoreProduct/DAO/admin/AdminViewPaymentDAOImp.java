package eStoreProduct.DAO.admin;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import eStoreProduct.model.AdminViewPayments;
import eStoreProduct.model.AdminViewPaymentsMapper;

@Component
public class AdminViewPaymentDAOImp implements AdminViewPaymentDAO{

	JdbcTemplate jdbcTemplate;
	@Autowired
	public AdminViewPaymentDAOImp(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private String get_payments="select ordr_id,ordr_billno,ordr_odate,ordr_total,ordr_payreference from slam_orders";
	private String get_Between_dates="select ordr_id,ordr_billno,ordr_odate,ordr_total,ordr_payreference from slam_ordersdup where ordr_odate between ? and ?";
	private String get_filter_payments="select ordr_id,ordr_billno,ordr_odate,ordr_total,ordr_payreference from slam_ordersdup where ordr_total between ? and ?";
	private String get_max_payments="select ordr_id,ordr_billno,ordr_odate,ordr_total,ordr_payreference from slam_ordersdup where ordr_total>?";
	@Override
	public List<AdminViewPayments> getPayments() {
		return jdbcTemplate.query(get_payments, new AdminViewPaymentsMapper());
		
	}
	public List<AdminViewPayments> getPaymentsBetweenDates(Timestamp date1,Timestamp date2) {
		return jdbcTemplate.query(get_Between_dates, new AdminViewPaymentsMapper(),date1,date2);
		
		//return null;
	}
	
	public List<AdminViewPayments> getPaymentsInThePriceRange(double p1,double p2)
	{
		return jdbcTemplate.query(get_filter_payments, new AdminViewPaymentsMapper(),p1,p2);
	}
	
	public List<AdminViewPayments> getMaxPricePayment(double p1)
	{
		return jdbcTemplate.query(get_max_payments, new AdminViewPaymentsMapper(),p1);
	}

}
