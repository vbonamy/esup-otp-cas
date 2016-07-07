CAS4 Overlay Template
============================

Generic CAS version 4.0.X maven war overlay using esup-otp-api
### Installation
- git clone https://github.com/EsupPortail/esup-otp-cas.git
- git checkout 4.0.X
- mvn clean package
- move cas.war into your servlets server webapps directory
- launch your server

esup-otp-api must be running to work, if you use CAS in a secure mode (HTTPS) you have to use a reverse proxy between http://localhost:3000 and https://localhost:3443