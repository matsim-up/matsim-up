[![Build Status](https://app.travis-ci.com/matsim-up/matsim-up.svg?branch=master)](https://app.travis-ci.com/matsim-up/matsim-up)
[![Packagecloud Repository](https://img.shields.io/badge/java-packagecloud.io-844fec.svg)](https://packagecloud.io/matsim-up/matsim-up/)


# matsim-up
Some general classes, utilities and associated tests frequently used by the MATSim team at the University of Pretoria. This repository is essentially based on the [matsim-example-project](https://github.com/matsim-org/matsim-example-project). Continuous integration (CI) is done on [Travis-CI](https://travis-ci.com/matsim-up/matsim-up) and (snapshot) jars are deployed to [PackageCloud](https://packagecloud.io/matsim-up/matsim-up).

## Usage

To use `matsim-up` as a dependency in an external maven project, update the external project's `pom.xml` file by adding the [PackageCloud](https://packagecloud.io/matsim-up/matsim-up) repository

```
<repositories>
	<repository>
		<id>matsim-up-matsim-up</id>
		<url>https://packagecloud.io/matsim-up/matsim-up/maven2</url>
	</repository>
</repositories>
```
and the depency on `matsim-up`
```
<dependencies>
	<dependency>
  		<groupId>org.matsim.up</groupId>
  		<artifactId>matsim-up</artifactId>
  		<version>14.0-PR1720</version>
	</dependency>
</dependencies>
```
where the (pull request) version is indicative of the specific 
[`matsim`](https://github.com/matsim-org/matsim-libs) 
version that this repository is compatible with.
