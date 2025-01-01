.PHONY: setup-database run-migration setup-dev-env

# Setup MySQL container using docker-compose
setup-database:
	docker-compose up -d db

# Setup Dev Container and MySQL server running simultaneously
setup-dev-env:
	docker-compose up -d

# Run Flyway migrations
run-migration:
	mvn flyway:migrate

run-database:
	docker exec -it sembackend-db-1 mysql -u root -p