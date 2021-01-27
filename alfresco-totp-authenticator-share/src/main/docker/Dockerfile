FROM ${docker.share.image}:${alfresco.share.version}

ARG TOMCAT_DIR=/usr/local/tomcat

# Copy Dockerfile to avoid an error if no JARs exist
COPY Dockerfile extensions/*.jar $TOMCAT_DIR/webapps/share/WEB-INF/lib/

# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps_share/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps_share $TOMCAT_DIR/webapps/share -directory -nobackup -force

COPY share-config-custom.xml $TOMCAT_DIR/shared/classes/alfresco/web-extension

COPY log4j.properties $TOMCAT_DIR/webapps/share/WEB-INF/classes
COPY hotswap-agent.properties $TOMCAT_DIR/webapps/share/WEB-INF/classes

# Patch for https://github.com/Alfresco/acs-community-packaging/issues/367
RUN sed -i 's/\(<show-authorization-status>\)true\(<\/show-authorization-status\)/\1false\2/' $TOMCAT_DIR/webapps/share/WEB-INF/classes/alfresco/share-config.xml