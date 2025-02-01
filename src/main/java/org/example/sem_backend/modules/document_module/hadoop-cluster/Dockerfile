FROM apache/hadoop:3.3.5

USER root
RUN apt update && apt install -y openjdk-8-jdk && \
    ln -s /usr/lib/jvm/java-8-openjdk-amd64 /usr/lib/jvm/default-java

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"

CMD ["bash"]
