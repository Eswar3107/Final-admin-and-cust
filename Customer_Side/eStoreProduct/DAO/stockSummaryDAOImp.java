package eStoreProduct.DAO;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository; // Import the Repository annotation

import eStoreProduct.model.stockMapperMapper;
import eStoreProduct.model.stockSummaryModel;

@Repository // Add the Repository annotation
public class stockSummaryDAOImp implements stockSummaryDAO {
	@PersistenceContext
	private EntityManager entityManager;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public stockSummaryDAOImp(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<stockSummaryModel> getStocks() {
		String custSelectQuery = "SELECT slp.*, spc.prct_title, shc.sgst, shc.igst, shc.cgst, shc.gst, sps.prod_price, sps.prod_stock, sps.prod_mrp "
				+ "FROM slam_products slp " + "JOIN slam_Productstock sps ON slp.prod_id = sps.prod_id "
				+ "JOIN slam_hsn_code shc ON slp.prod_gstc_id = shc.hsn_code "
				+ "JOIN SLAM_PRODUCTCATEGORIES spc ON spc.prct_id = slp.prod_prct_id";

		try {
			System.out.print("in query1");
			List<stockSummaryModel> stocks = jdbcTemplate.query(custSelectQuery, new stockMapperMapper());
			System.out.print("in query2");
			return stocks;
		} catch (Exception e) {
			// Handle the exception appropriately (e.g., logging, throwing custom exception, etc.)
			e.printStackTrace();
			return Collections.emptyList(); // or throw an exception if required
		}
	}

	public void updateStocks(int prodid, String imageurl, int gstcid, int reorderlevel, int stock, double mrp) {
		System.out.println("In the method: " + prodid + "gst" + gstcid);

		try {
			String query1 = "UPDATE slam_products SET reorderlevel = ?, image_url = ?, prod_gstc_id = ? WHERE prod_id = ?";
			String query2 = "UPDATE slam_productstock SET prod_stock = ?, prod_mrp = ?, prod_price = ? WHERE prod_id = ?";
			String query3 = "SELECT gst FROM slam_hsn_code WHERE hsn_code = ?";

			double gstPercent = jdbcTemplate.queryForObject(query3, new Object[] { gstcid }, Double.class);
			double updated_mrp = ((mrp * gstPercent * 0.01) + mrp);

			jdbcTemplate.update(query1, reorderlevel, imageurl, gstcid, prodid);
			System.out.println("Updated slam_products table");

			jdbcTemplate.update(query2, stock, mrp, updated_mrp, prodid);
			System.out.println("Updated slam_productstock table");
		} catch (Exception e) {
			System.out.println("Error updating the database: " + e.getMessage());
			e.printStackTrace();
		}
	}

}