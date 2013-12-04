# Typer Timings

Show the time spent in the Scala type-checker in Eclipse

# Developer info

The projects can readily be imported inside Eclipse. Additionally, you have maven `pom` files
based on Tycho, enabling command line builds.

## Building:

This template uses [plugin-profiles](https://github.com/scala-ide/plugin-profiles) to manage the build. Check its documentation for detailed information. The command to use looks like this:

    mvn -Pscala-2.10.x,eclipse-indigo,scala-ide-stable clean install

