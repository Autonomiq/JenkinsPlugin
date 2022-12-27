# JenkinsBuilderPlugin
Jenkins build plugin to run tests on Autonomiq service

## Install Requrements

Install Oracle JDK 8 from http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

Install maven on macOS:
```bash
$ brew install maven
```
## Build
To build the plugin
```bash
$ mvn package
```
Plugin file is in:
`target/AutonomiqBuilder.hpi`

## Install Plugin

1. Navigate to `Manage Jenkins / Manage Plugins / (tab) Advanced`
1. Scroll down to `Upload Plugin`
1. Click on `Choose File` then select the `AutonomiqBuilder.hpi` file
1. Click on `Upload`
1. Check the checkbox `Restart Jenkins when installation is complete and no jobs are running`

## Use Plugin

1. On Jenkins main page click on `New Item`
1. Enter job name and click on `Frestyle Project` and then on `OK` at the bottom
1. Click on `Build` tab to scroll to build section
1. Click on `Add Build Step` then select `Autonomiq Plugin`
1. Fill in fields then click on `Save` at the bottom of the page
1. Click on `Build Now` to run the job

(Right now the plugin will just check that it can authenticate with the service)

#### TODO: Review Readme
