FROM tomcat:8.0
# deployment path
COPY tomcat-users.xml /usr/local/tomcat/conf
RUN rm -rf /usr/local/tomcat/webapps/*.war
COPY target/*.war /usr/local/tomcat/webapps/
RUN mv /usr/local/tomcat/webapps/*.war /usr/local/tomcat/webapps/pebble.war
