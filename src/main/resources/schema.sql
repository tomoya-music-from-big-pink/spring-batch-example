CREATE TABLE reservations (
	id INTEGER NOT NULL PRIMARY KEY,
	file_name VARCHAR(20) NOT NULL,
	credential_key VARCHAR(10) NOT NULL
);

INSERT INTO reservations VALUES(1, 'bon_jov.csv', 'test01');
INSERT INTO reservations VALUES(2, 'metallica.csv', 'test02');
INSERT INTO reservations VALUES(3, 'iron_maiden.csv', 'test03');

CREATE TABLE credentials (
	id INTEGER NOT NULL PRIMARY KEY,
	db_name VARCHAR(10) NOT NULL,
	host VARCHAR(10) NOT NULL,
	username VARCHAR(10) NOT NULL,
	password VARCHAR(10) NOT NULL
);

INSERT INTO credentials VALUES(1, 'test01', 'localhost', 'testuser01', 'testpass01');
INSERT INTO credentials VALUES(3, 'test02', 'localhost', 'testuser02', 'testpass02');
INSERT INTO credentials VALUES(4, 'test03', 'localhost', 'testuser03', 'testpass03');
