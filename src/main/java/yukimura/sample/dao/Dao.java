package yukimura.sample.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.ToString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import yukimura.sample.dao.entity.SQLHistoryEntity;

@Repository
@ToString
public class Dao implements AutoCloseable {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    /**
     * sql_nameテーブルへの登録を行う。
     * @param sqlName
     * @return
     * @throws SQLException
     */
    public Integer insertSQLName(final String sqlName) throws SQLException {
        final String insertSQLName  = "INSERT INTO sql_name value(?,?)";
        final String selectMaxSqlId = "SELECT max(sql_id) AS max_sql_id FROM sql_name";
        try(PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertSQLName)){
            Integer maxSqlId = (Integer)jdbcTemplate.queryForList(selectMaxSqlId).get(0).get("max_sql_id");
            Integer nextSqlId = maxSqlId+1;
            pstmt.setInt(1, nextSqlId);
            pstmt.setString(2, sqlName);
            pstmt.execute();
            return nextSqlId;
        }

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
        final String sqlSelectMaxSeq = "SELECT max(seq) max_seq FROM sql_history where sql_id = ?";
        try(PreparedStatement pstmt = dataSource.getConnection().prepareStatement(sqlSelectMaxSeq)) {
            pstmt.setInt(1, targetSqlId);
            try(ResultSet rs = pstmt.executeQuery()){
                rs.next();
                return rs.getInt("max_seq");
                    
            }
        }

    }

    /**
     * 特定テーブルから、第一引数で指定したカラムを取得する。
     * （queryForListのSampleその１）
     * @param targetColumnArray
     * @param tableName
     * @return
     */
    public List<Map<String, Object>> select2MapList(final String sql) {
        return jdbcTemplate.queryForList(sql);
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
