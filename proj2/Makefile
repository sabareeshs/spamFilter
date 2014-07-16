# a very simple Makefile to compile the project code base

# common vars
JAVAC = javac
OPTS = -d ./classes

compile:
	$(JAVAC) $(OPTS) src/*/*.java src/*/*/*.java

all:	clean compile

jdoc:
	javadoc -d ./javadoc -sourcepath ./src -link "http://java.sun.com/j2se/1.5.0/docs/api/" util pacman pacman.game pacman.ghosts pacman.player classify classify.general classify.spam classify.student

clean:
	rm -rf ./classes/*