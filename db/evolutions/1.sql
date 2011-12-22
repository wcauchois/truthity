
# --- !Ups

CREATE TABLE Fact(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  noun varchar(255) NOT NULL,
  verb varchar(255) NOT NULL,
  object varchar(255) NOT NULL,
  addedAt date NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE Vote(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  factId bigint(20) NOT NULL,
  value smallint NOT NULL,
  votedAt date NOT NULL,
  FOREIGN KEY(factId) REFERENCES Fact(id),
  PRIMARY KEY(id)
);

# --- !Downs

DROP TABLE Fact;
DROP TABLE Vote;

