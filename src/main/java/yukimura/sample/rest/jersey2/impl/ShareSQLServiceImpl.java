package yukimura.sample.rest.jersey2.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import yukimura.sample.dao.Dao;
import yukimura.sample.dao.entity.SQLHistoryEntity;
import yukimura.sample.dao.entity.SQLNameEntity;
import yukimura.sample.rest.jersey2.spec.ShareSQLService;

public class ShareSQLServiceImpl implements ShareSQLService {
    @Autowired
    private Dao sqlHistoryDao;

	@Override
	public List<SQLNameEntity> getSqlIds() throws SQLException {
		return sqlHistoryDao.selectSqlIds();
	}

	@Override
	public List<SQLHistoryEntity> getSqlHistoryKeys(Integer targetSqlId) throws SQLException {
		if( targetSqlId == null ) return null;

		return sqlHistoryDao.selectSqlHistoryInfos(targetSqlId);

	}

	@Override
	public boolean createSQL(String sqlName, String sqlSentence, String sqlComment) throws SQLException {
		Integer sqlId = sqlHistoryDao.insertSQLName(sqlName);
		SQLHistoryEntity sqlHistoryEntity = new SQLHistoryEntity(sqlId, null, sqlSentence, sqlComment);
		boolean result = sqlHistoryDao.insertSQLHistory(sqlHistoryEntity);
	
		return result;

	}

	@Override
	public List<Map<String, Object>> executeQuery(String sql) {
		List<Map<String, Object>> select2MapList = sqlHistoryDao.select2MapList(sql);
		System.out.println(sql);
		System.out.println(select2MapList);
		return select2MapList;
	}

}
