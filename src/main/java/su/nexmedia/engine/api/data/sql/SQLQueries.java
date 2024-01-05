package su.nexmedia.engine.api.data.sql;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.connection.AbstractDataConnector;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class SQLQueries {

    public static boolean hasTable(@NotNull AbstractDataConnector connector, @NotNull String table) {
        try (Connection connection = connector.getConnection()) {

            boolean has;
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, table, null);
            has = tables.next();
            tables.close();
            return has;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasColumn(@NotNull AbstractDataConnector connector, @NotNull String table, @NotNull SQLColumn column) {
        String sql = "SELECT * FROM " + table;
        String columnName = column.getName();
        try (Connection connection = connector.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            for (int index = 1; index <= columns; index++) {
                if (columnName.equals(metaData.getColumnName(index))) {
                    return true;
                }
            }
            return false;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void executeStatement(@NotNull AbstractDataConnector connector, @NotNull String sql) {
        executeStatement(connector, sql, Collections.emptySet());
    }

    public static void executeStatement(@NotNull AbstractDataConnector connector, @NotNull String sql,
                                           @NotNull Collection<String> values1) {
        executeStatement(connector, sql, values1, Collections.emptySet());
    }

    public static void executeStatement(@NotNull AbstractDataConnector connector, @NotNull String sql,
                                           @NotNull Collection<String> values1, @NotNull Collection<String> values2) {

        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int count = 1;
            for (String columnName : values1) {
                statement.setString(count++, columnName);
            }
            for (String columnValue : values2) {
                statement.setString(count++, columnValue);
            }

            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public static <T> List<@NotNull T> executeQuery(@NotNull AbstractDataConnector connector, @NotNull String sql,
                                                    @NotNull Collection<String> values1,
                                                    @NotNull Function<ResultSet, T> dataFunction,
                                                    int amount) {

        List<T> list = new ArrayList<>();
        try (Connection connection = connector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            int count = 1;
            for (String wValue : values1) {
                statement.setString(count++, wValue);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next() && (amount < 0 || list.size() < amount)) {
                list.add(dataFunction.apply(resultSet));
            }
            resultSet.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        list.removeIf(Objects::isNull);

        return list;
    }
}
