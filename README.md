# University Life Assistant: A Time Tracker App

### Introduction
University Life Assistant is a time tracker app that leverages geolocation and NFC proximity tags to detect daily activities and events such as studying, going to the gym, doing groceries, etc. It uses local persistent storage and caching to retrieve a list of recent activities and scans GPS data and NFC tags to track new events. It can integrates with native Android notifications to send reminders, weekly reports or alerts.

### Project Information
* Name: University Life Assistant
* Package Name: com.cs446.group18.timetracker
* Language: Java
* Min SDK: API26
* Target SDK: API29
* Device/Simulator: Pixel 4, API 29

### Installation
Simply run the Gradle build from Android Studio and load the app to a compatible device.

### Google Map API Key
For this project, we used **BuildConfigs** to hide Google Map **API_KEY** that is not checked into source control. **BuildConfigs** will parse **API_KEY** from **apikey.properties** file.<br /><br />
To enable geolocation service, please create a file named **apikey.properties** in project root directory ("TimeTracker") with the value for **API_KEY**:
```
API_KEY="YOUR_API_KEY"
```
