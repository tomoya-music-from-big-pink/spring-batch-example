#!/bin/bash

psql -c "CREATE DATABASE test01;"
psql -c "CREATE USER testuser01 WITH LOGIN CREATEDB PASSWORD 'testpass01';"
psql test01 -c "CREATE TABLE member (id INTEGER NOT NULL PRIMARY KEY, first_name VARCHAR(20) NOT NULL, last_name VARCHAR(20) NOT NULL, full_name VARCHAR(20) NOT NULL);"
psql test01 -c "ALTER TABLE member OWNER TO testuser01;"