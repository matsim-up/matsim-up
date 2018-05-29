[![Travis Build Status](https://travis-ci.com/matsim-up/matsim-up.svg?branch=master)](https://travis-ci.com/matsim-up/matsim-up)

# matsim-up
Some general classes, utilities and associated tests frequently used by the MATSim team at the University of Pretoria. This repository is essentially based on the [matsim-example-project](https://github.com/matsim-org/matsim-example-project).

## Usage

To use `matsim-up` as a dependency in an external maven project, update the external project's `pom.xml` file by adding the [JitPack](https://jitpack.io) repository

```
<repository>
	<id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```

and the depency on `matsim-up`

```
<dependency>
	<groupId>org.matsim.up</groupId>
	<artifactId>matsim-up</artifactId>
	<version>0.10.0-SNAPSHOT</version>
</dependency>
```