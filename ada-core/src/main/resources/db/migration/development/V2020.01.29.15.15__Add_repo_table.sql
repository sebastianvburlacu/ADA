CREATE TABLE REPOSITORY(
   ID BIGSERIAL PRIMARY KEY,
   URL VARCHAR(256) NOT NULL,
   NAME VARCHAR(256) NOT NULL,
   OWNER VARCHAR(256) NOT NULL,
   BRANCH VARCHAR(256) NOT NULL
);