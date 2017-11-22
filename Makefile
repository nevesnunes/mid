SOURCEDIR = src
SOURCES := $(shell find $(SOURCEDIR) -name '*.java')

all:
	$(shell mkdir -p bin)
	javac -cp "jars/*" $(SOURCES) -d bin/

run:
	java -cp "jars/*:bin/" mid.Main

.PHONY: run
