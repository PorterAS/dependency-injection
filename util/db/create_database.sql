CREATE USER :"app_user" WITH PASSWORD :'app_pw' CREATEDB;
CREATE DATABASE :"app_db";
GRANT ALL PRIVILEGES ON DATABASE :"app_db" TO :"app_user";
\c :"app_db"
CREATE SCHEMA :"app_schema" AUTHORIZATION :"app_user";
ALTER USER :"app_user" SET search_path to :"app_schema";
CREATE EXTENSION pgcrypto SCHEMA :"app_schema";
