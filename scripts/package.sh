#!/bin/bash
cd ..
mvn clean
mvn package
cd docker
rm ad_pl_e*
cp ../target/ad_pl_e-0.1.0.jar .
docker build --tag ad_pl_e:1.0 .
docker save -o ad_pl_e_docker.tar ad_pl_e:1.0
gzip  ad_pl_e_docker.tar
cp ad_pl_e_docker.tar.gz ../artifacts/

