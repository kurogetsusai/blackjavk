JC=javac
JCFLAGS=
JRE=java
SOURCES= Main.java MainWindow.java AnIntegerThatActuallyWorksProperlyWithPointers.java
OBJECTS=$(SOURCES:.java=.class)
EXECUTABLE=Main.class
RM=rm -f

all: $(OBJECTS)

%.class: %.java
	$(JC) $(JCFLAGS) $<

run:
	$(JRE) $(EXECUTABLE:.class=)

clean:
	$(RM) $(OBJECTS) $(EXECUTABLE)
