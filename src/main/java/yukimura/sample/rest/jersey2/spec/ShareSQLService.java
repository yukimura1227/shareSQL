package yukimura.sample.rest.jersey2.spec;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


@Path("/sample")
public interface ShareSQLService {    
    // Ex) http://localhost:8080/shareSQL/rest/sample/getSqlIds
    @GET
    @Path("/getSqlIds")
    @Produces("application/json")
    public List<Map<String, Object>> getSqlIds() throws SQLException;

    // Ex) http://localhost:8080/shareSQL/rest/sample/getSqlHistoryKeys/1
    @GET
    @Path("/getSqlHistoryKeys/{sqlId}")
    @Produces("application/json")
    public List<Map<String, Object>> getSqlHistoryKeys(@PathParam("sqlId") Integer targetSqlId) throws SQLException;

    @POST
    @Path("/createSQL")
    @Produces("application/json")
    public boolean createSQL(
      @FormParam("sqlName")    String sqlName,
      @FormParam("sqlSentence")String sqlSentence,
      @FormParam("sqlComment") String sqlComment
    ) throws SQLException;
    
    @POST
    @Path("/execQuery")
    @Produces("application/json")
    public List<Map<String, Object>> executeQuery(@FormParam("sql")String sql);

}
