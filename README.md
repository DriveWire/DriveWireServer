# DriveWire Server

This project is a refactor of the Color Computer [DriveWire 4](https://sites.google.com/site/drivewire4/) project. For now, this project is in an experimental stage, and is likely to change significantly.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Development Prerequisites

In order to build DriveWire 4, the following software is required:
* Java JVM >= 1.8 ([Oracle JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html))
* Gradle ([Gradle](https://gradle.org/install/))
* GIT ([GIT](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git))


### Development Tools Installation

A step by step series of examples that tell you how to get a development env running

Clone (download) the DriveWire source

```
git clone <repository url>
```

Change directory into the project and execute

```
gradle clean build fatCapsule
```

\- Or -

```
./gradlew
```


## Running the application

Build using the steps above, or download the [Pre-built binary](https://github.com/DriveWire/DriveWireServer/releases/download/4.4.0-alpha1/DriveWireServer-4.4.0-capsule.jar)

If you build yourself - the jar file will be in the `build/libs` directory.

Currently, as this only implements the server functionality, the best way to get a configuration file setup is to run the existing DriveWire 4 UI, execute the wizard, then copy the created config.xml file and point to it using the --config option:

```
cd build/libs
java -jar DriveWireServer-4.4.0-capsule.jar --config=<path_to_config>
```

_Note: if you do not supply `--config` a default (probably not useful) config will be loaded instead._

## Contributing


Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/DriveWire/DriveWireServer/tags). 

## Authors

* **Aaron Wolfe**, **Boisy Pitre**, **Christopher Hawks**, **Jim Hathaway**, **Neil Andrew Cook**, **Tim Lindner**, **Tormod Volden**, **Wayne Campbell** - The original authors of DriveWire

* **Nathan Byrd** - [CognitiveGears](https://github.com/cognitivegears)

See also the list of [contributors](https://github.com/DriveWire/DriveWireServer/contributors) who participated in this project.

## License

This project is licensed under the GPL 3 - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Great thanks to the CoCo community for their support in keeping this project and our favorite 8-bit computer alive.
