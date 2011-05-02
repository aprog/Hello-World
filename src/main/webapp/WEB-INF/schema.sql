DROP TABLE users;
DROP TABLE roles;
DROP TABLE personalmessages;
DROP TABLE pm2users;
DROP TABLE pmrecipients;
DROP TABLE pm2recipients;

CREATE TABLE users (
	id		SERIAL PRIMARY KEY,
	name		VARCHAR(255) UNIQUE NOT NULL,
	password	VARCHAR(255),
	email		VARCHAR(255),
	roleId		INTEGER
);

CREATE TABLE roles (
	id		SERIAL PRIMARY KEY,
	name		VARCHAR(255) UNIQUE,
	canClaim	BOOLEAN
);

CREATE TABLE personalmessages (
	id		SERIAL PRIMARY KEY,
	subject		VARCHAR(255) NOT NULL,
	body		TEXT,
	creationTime	TIMESTAMP,
	deleted		BOOLEAN
);

CREATE TABLE pm2users (
	id		SERIAL PRIMARY KEY,
	userid		INTEGER,
	pmid		INTEGER
);

CREATE TABLE pmrecipients (
	id		SERIAL PRIMARY KEY,
	userid		INTEGER,
	isRead		BOOLEAN,
	deleted		BOOLEAN
);

CREATE TABLE pm2recipients (
	id		SERIAL PRIMARY KEY,
	pmid		INTEGER,
	pmrecipientid	INTEGER
);
