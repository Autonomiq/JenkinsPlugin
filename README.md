This plugin allows users of [AutonomIQ](https://saucelabs.com/platform/low-code-testing) to trigger execution of suites via Jenkins

## Install Plugin

1. Navigate to `Manage Jenkins / Manage Plugins / (tab) Advanced`
2. Scroll down to `Upload Plugin`
3. Click on `Choose File` then select the `AutonomiqBuilder.hpi` file
4. Click on `Upload`
5. Check the checkbox `Restart Jenkins when installation is complete and no jobs are running`

## Use Plugin

1. On Jenkins main page click on `New Item`
2. Enter job name and click on `Frestyle Project` and then on `OK` at the bottom
3. Click on `Build` tab to scroll to build section
4. Click on `Add Build Step` then select `Autonomiq Plugin`
5. Provide AutonomIQ instance URL, username and password.
6. Choose required project in the AutonomIQ user account from dropdown list.
7. Click on Run test suites checkbox
8. Choose available platform and browser combination.
9. Enter list of suite names to run (one per line).
10. Click on save
11. Once user clicks on build now, suite execution will begin in AutonomIQ.

Note: (In case user needs to add multiple branches and and execute suites from different project. Click on Add build step again to add multiple branches and follow steps from (1-6)).

# View execution status:

Once the execution is completed, in order to view the status of execution.
Click on corresponding build > Click on console output.





