FROM ${docker.acs.image}:${alfresco.platform.version}

ARG TOMCAT_DIR=/usr/local/tomcat
ARG USERNAME=${alfresco.platform.docker.user}

USER root

# Copy Dockerfile to avoid an error if no JARs exist
COPY Dockerfile extensions/*.jar $TOMCAT_DIR/webapps/alfresco/WEB-INF/lib/

# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force

COPY alfresco-global.properties $TOMCAT_DIR/shared/classes/alfresco-global.properties
COPY dev-log4j.properties $TOMCAT_DIR/shared/classes/alfresco/extension
COPY disable-webscript-caching-context.xml $TOMCAT_DIR/shared/classes/alfresco/extension

# Copy Dockerfile to avoid an error if no license file exists
COPY Dockerfile license/*.* $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes/alfresco/extension/license/

USER ${USERNAME}

# HOTSWAP AGENT
# Install and configure Trava OpenJDK (OpenJDK pre-built with DCEVM and hotswap agent for Java 11)
#COPY java11-openjdk-dcevm-linux.tar.gz $TOMCAT_DIR
#RUN tar -xvf $TOMCAT_DIR/java11-openjdk-dcevm-linux.tar.gz -C /usr/java/ && \
#    rm $TOMCAT_DIR/java11-openjdk-dcevm-linux.tar.gz && \
#    alternatives --install /usr/bin/java java /usr/java/dcevm-11.0.9+2/bin/java 40000 && \
#    alternatives --install /usr/bin/javac javac /usr/java/dcevm-11.0.9+2/bin/javac 40000 && \
#    alternatives --install /usr/bin/jar jar /usr/java/dcevm-11.0.9+2/bin/jar 40000 && \
#    alternatives --set java /usr/java/dcevm-11.0.9+2/bin/java && \
#    alternatives --set javac /usr/java/dcevm-11.0.9+2/bin/javac && \
#    alternatives --set jar /usr/java/dcevm-11.0.9+2/bin/jar && \
#    ln -sfn /usr/java/dcevm-11.0.9+2 /usr/java/latest && \
#    ln -sfn /usr/java/dcevm-11.0.9+2 /usr/java/default

# Copy the configuration properties file in the classpath
#COPY hotswap-agent.properties $TOMCAT_DIR/shared/classes
COPY hotswap-agent.properties $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes
