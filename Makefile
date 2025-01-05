.PHONY: run-migration setup-dev-env run-main rebuild

# Setup Dev Container and MySQL server running simultaneously
setup-dev-env:
	docker-compose up -d

# Run Flyway migrations
run-migration:
	mvn flyway:migrate

# Access MySQL CLI
run-database:
	docker exec -it sembackend-db-1 mysql -u root -p

# Run main application
run-main:
	docker exec -u root -it sembackend-devcontainer-1 bash

# Rebuild and restart all containers
rebuild:
	docker-compose down
	docker-compose build --no-cache
	docker-compose up -d