Import data from google sheets. The sheet name should me in format DD-MM.
The column A is for county name.
The column B is for number of cases in that day.

To run docker image use this command:
docker run --name covid-service --network host --detach pis/covid-service:1.0-SNAPSHOT