[![Build Flow](https://github.com/HRV-Mart/Backend-User/actions/workflows/build.yml/badge.svg)](https://github.com/HRV-Mart/Backend-User/actions/workflows/build.yml)
# Backend-User
## Set up application locally
```
git clone https://github.com/HRV-Mart/Backend-User.git
gradle clean build
```
## Set up application using Docker
```
docker run  --name HRV-Mart-Backend-User -it --init --net="host" -d harsh3305/hrv-mart-backend-user:latest
```