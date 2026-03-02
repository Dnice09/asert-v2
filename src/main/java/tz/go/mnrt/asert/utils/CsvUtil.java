package tz.go.mnrt.asert.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class CsvUtil {

    private final JdbcTemplate jdbcTemplate;

    public CsvUtil(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void exportTableToCSV(String tableName, String filePath) {
        String sql = "COPY " + tableName + " TO STDOUT WITH (FORMAT CSV, HEADER)";
        try (FileWriter fileWriter = new FileWriter(filePath);
                Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                fileWriter.append(resultSet.getString(1)).append("\n");
            }
            System.out.println("Data exported to " + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
