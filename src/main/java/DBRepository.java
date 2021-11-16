import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBRepository {
    public DBRepository(String name) throws Exception {
        // Для SQLite регистрация выглядит следующим образом
        Class.forName("org.sqlite.JDBC");
        // Для H2 Database - org.h2.Driver
        // Для MySQL - com.mysql.jdbc.Driver
        //protocol: jdbc
        //subprotocol: sqlite
        //name: test.db
        Connection conn;
        conn = DriverManager.getConnection(name);

        /*
        * CREATE TABLE IF NOT EXISTS Weather
            (
                   String city
                   String date
                   String weatherText
                   Double temperature

            );
            3. Организовать запись данных в базу при каждом успешном API запросе.
            * Формат - String city, String localDate, String weatherText, Double temperature.
        * */
        /*String statement = "CREATE TABLE IF NOT EXISTS Weather (" +
                "city TEXT PRIMARY KEY NOT NULL," +
                "date TEXT NOT NULL," +
                "weatherText TEXT NOT NULL," +
                "temperature DOUBLE NOT NULL" +
                ");";*/
        String sql = "CREATE TABLE IF NOT EXISTS Weather (\n"
                + "	city text NOT NULL,\n"
                + "	date text NOT NULL,\n"
                + "	weathertext text NOT NULL,\n"
                + "	temperature real\n"
                + ");";
        try (Statement prepCreate = conn.createStatement()) {
            prepCreate.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        conn.close();
        _dbName = name;
    }

    public void addDate(String city, String date, String wheatherText, Double temperature) throws Exception {
        Connection conn;
        conn = DriverManager.getConnection(_dbName);

        String sql = "INSERT INTO Weather (city, date, weathertext, temperature) VALUES (\n"
                + "\"" + city +"\",\"" + date +"\",\"" + wheatherText +"\",\"" + temperature + "\"\n"
                + ");";

        try (Statement prepCreate = conn.createStatement()) {
            prepCreate.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        conn.close();
    }

    public class DateDb{
        public String city;
        public String date;
        public String text;
        public Double temp;

        @Override
        public String toString() {
            return "" +city + ", " + date + ", " + text + ", " + temp;
        }
    }
    public List<DateDb> readDates()  throws Exception {
        List<DateDb> result = new ArrayList<>();

        Connection conn;
        conn = DriverManager.getConnection(_dbName);

        String sql = "SELECT * FROM Weather";

        try (Statement prepCreate = conn.createStatement()) {
            prepCreate.execute(sql);
            ResultSet rs = prepCreate.getResultSet();
            while (rs.next()) {
                DateDb newval = new DateDb();

                newval.city = rs.getString(1);
                newval.date = rs.getString(2);
                newval.text = rs.getString(3);
                newval.temp = rs.getDouble(4);
                result.add(newval);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        conn.close();

        return result;
    }
    private String _dbName;
}
