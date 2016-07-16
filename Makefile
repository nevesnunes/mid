SOURCEDIR = src
SOURCES := $(shell find $(SOURCEDIR) -name '*.java')

all:
	javac -cp "jars/*" $(SOURCES) -d bin/

run:
	java -cp "jars/*:bin/" mid.Main

.PHONY: run
