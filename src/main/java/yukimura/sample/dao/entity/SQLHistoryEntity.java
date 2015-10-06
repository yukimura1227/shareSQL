package yukimura.sample.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SQLHistoryEntity {
	private Integer sqlId;
	private Integer seq;
	private String  sqlSentence;
	private String  comment;

}
