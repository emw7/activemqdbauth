FROM apache/activemq-classic:5.18.3

ARG BUILD_DIR=./build

COPY ./activemq/ /opt/apache-activemq/
# EMW7 put in lib/emw7?
COPY ${BUILD_DIR}/lib /opt/apache-activemq/lib/
COPY ${BUILD_DIR}/activemqdbauth.jar /opt/apache-activemq/lib/

EXPOSE 8161 61616 5672 61613 1883 61614 1099
ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["activemq", "console"]
