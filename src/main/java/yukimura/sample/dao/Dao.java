package yukimura.sample.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.ToString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import yukimura.sample.dao.entity.SQLHistoryEntity;

@Repository
@ToString
public class Dao implements AutoCloseable {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * sql_nameテーブルへの登録を行う。
     * @param sqlName
     * @return
     * @throws SQLException
     */
    public Integer insertSQLName(final String sqlName) throws SQLException {
        final String insertSQLName  = "INSERT INTO sql_name value(:sqlId,:sqlName)";
        final String selectMaxSqlId = "SELECT max(sql_id) AS max_sql_id FROM sql_name";
        Integer maxSqlId = namedParameterJdbcTemplate.queryForObject(selectMaxSqlId, new HashMap<String, Object>(), Integer.class);
        Integer nextSqlId = maxSqlId+1;
        Map<String, Object> sqlParamMap = new HashMap<>();
        sqlParamMap.put("sqlId", nextSqlId);
        sqlParamMap.put("sqlName", sqlName);
        namedParameterJdbcTemplate.update(insertSQLName, sqlParamMap);
        return nextSqlId;

    }

    /**
     * sql_historyにデータを登録する。
     * @param insertData
     * @return
     * @throws SQLException
     */
    public boolean insertSQLHistory(final SQLHistoryEntity insertData) throws SQLException {
        final String insertSQLName = "INSERT INTO sql_history value(?,?,?,?,?)";
        try(PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertSQLName)){
            pstmt.setInt(1, insertData.getSqlId());
            pstmt.setInt(2, this.selectMaxSeq(insertData.getSqlId())+1);
            pstmt.setString(3, insertData.getSqlSentence());
            pstmt.setString(4, insertData.getComment());
            pstmt.setDate(5, new Date(Calendar.getInstance().getTimeInMillis()));
            return pstmt.execute();
        }
    }

    private Integer selectMaxSeq(final Integer targetSqlId) throws SQLException {
        System.out.println("called selectMaxSeq " + targetSqlId);
        final String sqlSelectMaxSeq = "SELECT max(seq) max_seq FROM sql_history where sql_id = :targetSqlId";
        Map<String, Object> sqlParamMap = new HashMap<>();
        sqlParamMap.put("targetSqlId", targetSqlId);
        Integer maxSeq =  namedParameterJdbcTemplate.queryForObject(sqlSelectMaxSeq, sqlParamMap, Integer.class);
        return maxSeq == null ? 0 : maxSeq;
    }

    /**
     * 渡されたsqlを実行する。
     * @param sql
     * @return
     */
    public List<Map<String, Object>> select2MapList(final String sql) {
        return this.select2MapList(sql,null);
    }

    /**
     * 渡されたsqlを実行する。
     * @param sql
     * @param sqlParamMap
     * @return
     */
    public List<Map<String, Object>>select2MapList(final String sql, final Map<String, Object> sqlParamMap) {
        System.out.println("called select2MapList :" + sql + ":" + sqlParamMap);
        return namedParameterJdbcTemplate.queryForList(sql, sqlParamMap);
    }

    @Override
    public void close() throws Exception {
        System.out.println("SQLHIstory Datasource close()");
        try {
            Connection conn = dataSource.getConnection();
            if( conn != null && !conn.isClosed() ) {
                conn.close();
            }
        } catch (SQLException sqle) {
            System.err.println("close failed.");
            sqle.printStackTrace();
        }

    }

}
