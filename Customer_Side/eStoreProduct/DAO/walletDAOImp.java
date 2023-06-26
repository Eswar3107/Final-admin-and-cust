package eStoreProduct.DAO;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import eStoreProduct.model.wallet;

@Component
public class walletDAOImp implements walletDAO {
	JdbcTemplate jdbcTemplate;

	String get_wallet_amt = "select * from slam_wallet where cust_id=?";
	String update_wallet = "update slam_wallet set amount=? where cust_id=?";

	@Autowired
	public walletDAOImp(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public wallet getWalletAmount(int custid) {

		return jdbcTemplate.queryForObject(get_wallet_amt, new Object[] { custid }, (rs, rowNum) -> {
			wallet w = new wallet();
			w.setCustid(rs.getInt("cust_id"));
			w.setAmount(rs.getDouble("amount"));
			return w;
		});

	}

	@Override
	public void updatewallet(double amt, int custid) {

		jdbcTemplate.update(update_wallet, amt, custid);

	}

}
