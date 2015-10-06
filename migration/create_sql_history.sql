CREATE TABLE sql_history(
  sql_id integer,
  seq integer,
  sql_sentence text not null,
  comment text ,
  regist_date datetime,
  PRIMARY KEY(sql_id,seq)
);
CREATE TABLE sql_name(
  sql_id integer PRIMARY KEY,
  sql_name text
);