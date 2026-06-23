package com.example.demo.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.partition.Partitioner;
import org.springframework.batch.infrastructure.item.ExecutionContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.domain.Reservations;

public class ReservationsDevidePartitioner implements Partitioner {

	private final JdbcOperations jdbcOperations;

	public ReservationsDevidePartitioner(DataSource dataSource) {
		this.jdbcOperations = new JdbcTemplate(dataSource);
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		String sql = "SELECT a.file_name, b.db_name, b.host, b.username, b.password FROM reservations a INNER JOIN credentials b ON a.credential_key = b.db_name ORDER BY a.id";
		List<Reservations> reservationList = this.jdbcOperations.query(sql, (rs, rowNum) -> {
			return new Reservations(rs.getString("file_name"), rs.getString("db_name"), rs.getString("host"),
					rs.getString("username"), rs.getString("password"));
		});

		Map<String, ExecutionContext> map = new HashMap<>();
		for (int i = 0; i < reservationList.size(); i++) {
			ExecutionContext executionContext = new ExecutionContext();
			executionContext.put("fileName", reservationList.get(i).fileName());
			executionContext.put("dbname", reservationList.get(i).dbname());
			executionContext.put("host", reservationList.get(i).host());
			executionContext.put("username", reservationList.get(i).username());
			executionContext.put("password", reservationList.get(i).password());

			map.put("reservation-" + i, executionContext);
		}

		return map;
	}

}
