.PHONY: build test lint check install clean

build:
	python3 scripts/dev.py build

test:
	python3 scripts/dev.py test

lint:
	python3 scripts/dev.py lint

check:
	python3 scripts/dev.py check

install:
	python3 scripts/dev.py install

clean:
	./gradlew clean
