package yukimura.sample.rest.jersey2.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import yukimura.sample.dao.Dao;
import yukimura.sample.dao.entity.SQLHistoryEntity;
import yukimura.sample.rest.jersey2.spec.ShareSQLService;

public class ShareSQLServiceImpl implements ShareSQLService {
    @Autowired
    private Dao sqlHistoryDao;
    @Override
    public List<Map<String, Object>> getSqlIds() throws SQLException {
        //  sql_nameテーブルからsql_idとsql_nameの情報を全て抽出する。
        final String sqlSelectSqlIds 
          = "SELECT sql_id as sqlId ,sql_name as sqlName FROM sql_name";
        List<Map<String, Object>> select2MapList = sqlHistoryDao.select2MapList(sqlSelectSqlIds);
        return select2MapList;

    }

    @Override
    public List<Map<String, Object>> getSqlHistoryKeys(Integer targetSqlId) throws SQLException {
        if( targetSqlId == null ) return null;
        // 対象のsql_idに紐づく情報をsql_historyとsql_nameテーブルから、取得する
        final String sqlSelectSqlHistoryKeys 
          = "SELECT sql_id as sqlId, seq, sql_sentence as sqlSentence,comment FROM sql_history "
          + "WHERE sql_id = :targetSqlId "
          + "order by seq desc";
        Map<String, Object> sqlParamMap = new HashMap<>();
        sqlParamMap.put("targetSqlId", targetSqlId);
        List<Map<String, Object>> select2MapList = sqlHistoryDao.select2MapList( sqlSelectSqlHistoryKeys, sqlParamMap);

        return select2MapList;
//        return sqlHistoryDao.selectSqlHistoryInfos(targetSqlId);

    }

    @Override
    public boolean createSQL(String sqlName, String sqlSentence, String sqlComment) throws SQLException {
        Integer sqlId = sqlHistoryDao.insertSQLName(sqlName);
        SQLHistoryEntity sqlHistoryEntity = new SQLHistoryEntity(sqlId, null, sqlSentence, sqlComment);
        int result = sqlHistoryDao.insertSQLHistory(sqlHistoryEntity);

        return result == 1 ? true : false;

    }

    @Override
    public boolean updateSQL(Integer sqlId, String sqlName, String sqlSentence, String sqlComment) throws SQLException {
        System.out.println("called updateSQL sqlId:" + sqlId);
        sqlHistoryDao.updateSQLName(sqlId, sqlName);
        SQLHistoryEntity sqlHistoryEntity = new SQLHistoryEntity(sqlId, null, sqlSentence, sqlComment);
        int result = sqlHistoryDao.insertSQLHistory(sqlHistoryEntity);

        return result == 1 ? true : false;

    }
    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> select2MapList = sqlHistoryDao.select2MapList(sql);
        System.out.println(sql);
        System.out.println(select2MapList);
        return select2MapList;

    }

}
