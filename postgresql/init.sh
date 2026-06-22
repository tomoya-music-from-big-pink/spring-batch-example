#!/bin/bash

psql -c "CREATE DATABASE test01;"
psql -c "CREATE DATABASE test02;"
psql -c "CREATE DATABASE test03;"
psql -c "CREATE USER testuser01 WITH LOGIN CREATEDB PASSWORD 'testpass01';"
psql -c "CREATE USER testuser02 WITH LOGIN CREATEDB PASSWORD 'testpass02';"
psql -c "CREATE USER testuser03 WITH LOGIN CREATEDB PASSWORD 'testpass03';"
psql test01 -c "CREATE TABLE member (id INTEGER NOT NULL PRIMARY KEY, first_name VARCHAR(20) NOT NULL, last_name VARCHAR(20) NOT NULL, full_name VARCHAR(20) NOT NULL);"
psql test01 -c "ALTER TABLE member OWNER TO testuser01;"
psql test02 -c "CREATE TABLE member (id INTEGER NOT NULL PRIMARY KEY, first_name VARCHAR(20) NOT NULL, last_name VARCHAR(20) NOT NULL, full_name VARCHAR(20) NOT NULL);"
psql test02 -c "ALTER TABLE member OWNER TO testuser02;"
psql test03 -c "CREATE TABLE member (id INTEGER NOT NULL PRIMARY KEY, first_name VARCHAR(20) NOT NULL, last_name VARCHAR(20) NOT NULL, full_name VARCHAR(20) NOT NULL);"
psql test03 -c "ALTER TABLE member OWNER TO testuser03;"