#!/bin/bash -eu

scriptdir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
basedir="$(cd ${scriptdir} && cd .. && pwd)"

function runSqlScript() {
	local db_user="$1"
	local db_password="$2"
	local db_name="$3"
	local script_name="$4"
	local parameters="${5:-}"

	PGPASSWORD=${db_password} psql -h ${db_host} -U ${db_user} -d ${db_name} -a -f ${script_name} ${parameters}
}

function sectionMsg() {
	local message="$1"
	echo " --- $message"
}

db_user="dbuser"
db_password="dbpass"
db_admin_user="myapp"
db_admin_password="myapp"
db_host="localhost"
db_port="5432"
db_name="testdb"
db_schema_name="testschema"
db_jdbc_url="jdbc:postgresql://$db_host:$db_port/$db_name"

sectionMsg "Docker container starting:"
docker run -d --name ${db_name}-postgres -p ${db_port}:${db_port} -e POSTGRES_PASSWORD=$db_admin_password -e POSTGRES_USER=$db_admin_user -v "$scriptdir/my-postgres.conf":/etc/postgresql/postgresql.conf postgres:11.6 -c 'config_file=/etc/postgresql/postgresql.conf'

echo "Waiting for Postgres server to start..."
sleep 10

sectionMsg "Creating database, user and schema"
runSqlScript "$db_admin_user" "$db_admin_password" "postgres" "$scriptdir/db/create_database.sql" "-v app_db=$db_name -v app_user=$db_user -v app_schema=$db_schema_name -v app_pw=$db_password"

sectionMsg "Creating tables"
runSqlScript "$db_user" "$db_password" "$db_name" "$scriptdir/db/create_tables.sql" "-v app_db=$db_name -v app_user=$db_user -v app_schema=$db_schema_name -v app_pw=$db_password"

# Some times you will need to insert more test data and keep running the migrations after that point in time. Do the
# following lines, and change flyway.target yo your current version.
#
#sectionMsg "Migrating schema with Flyway to create accounts tables"
#./gradlew flywayMigrate -Ddatabase.username=${db_user} -Ddatabase.password=${db_password} -Ddatabase.url=${db_jdbc_url} -Dflyway.target=57
#runSqlScript "$db_user" "$db_password" "$db_name" "$basedir/util/add_accounts_test_data.sql"

# We would also do Flyway migrations
#sectionMsg "Migrating schema with Flyway to latest"
#pushd $basedir
#./gradlew flywayMigrate -Dorder.database.username=${db_user} -Dorder.database.password=${db_password} -Dorder.database.url=${db_jdbc_url}
#popd

echo " "
echo " *** Created database *** "
echo " "
echo " Login with $db_user/$db_password ."
echo " JDBC URL is: $db_jdbc_url"
echo " "
