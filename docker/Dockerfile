FROM ubuntu

RUN apt --assume-yes update
RUN apt --assume-yes install openjdk-8-jdk
COPY ad_pl_e-0.1.0.jar .

CMD [ "sh", "-c", "java -jar ad_pl_e-0.1.0.jar $my_params" ]
