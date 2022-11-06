#!/bin/bash
mvn clean install
docker build -t messenger-client .