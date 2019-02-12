# dionysus
Simple Java-Mongo-React Web App for serving up media content

This project is made up of 3 major components.
1) MongoDB
2) Streamer - The java backend
3) ui - The React frontend

## Requirements
In order to run this locally the following must already be present on the target machine.
1) mongod
2) JDK 1.8+
3) npm

## Running locally
The project has several scripts to help for running locally.

### Running Mongo Locally
You can run your mongo locally from anywhere, however the project provides the `runLocalMongo.sh` script in the project root directory for setting up the database within the project's directory structure. This can be useful during development to keep it self contained.

### Running Streamer Locally
From within the Streamer directory there is a script called `localStart.sh`. Upon first run, application will initialize the database into a usable state. It will also initialize a new admin user for first login. The password for this user is randomized and can be found in the log during the first boot of the application. It is recommended that the password be changed shortly after.

### Running UI Locally
This isn't setup yet :)
