# Basic Dockerfile configuration
basic:
	echo "FROM ubuntu:18.04" > Dockerfile
	echo "COPY . /app" >> Dockerfile
	echo "ENV GOOGLE_APPLICATION_CREDENTIALS /app/gcloud-key.json" >> Dockerfile
	echo "RUN apt-get update" >> Dockerfile
	echo "RUN apt-get -y install python3 python3-pip libpq-dev" >> Dockerfile
	echo "RUN pip3 install --upgrade google-cloud-pubsub google-cloud-storage psycopg2" >> Dockerfile

# For debugging; can manually spawn a shell
shell: basic
	echo "CMD while true; do sleep 10; done" >> Dockerfile
	docker build -t bc20_shell .

# Compilation server
compile: basic
	echo "RUN apt-get -y install unzip" >> Dockerfile
	echo "CMD ./app/compile_server.py" >> Dockerfile
	docker build -t bc20_compile .
	docker save bc20_compile > bc20_compile.tar

# Game runner server
game: basic
	echo "CMD ./app/game_server.py" >> Dockerfile
	docker build -t bc20_game .
	docker save bc20_game > bc20_game.tar

clean:
	rm -f Dockerfile
	docker rmi bc20_shell bc20_compile bc20_game

.PHONY: shell, basic, compile, game, clean
