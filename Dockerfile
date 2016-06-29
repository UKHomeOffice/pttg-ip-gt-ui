FROM quay.io/ukhomeofficedigital/openjdk8:v0.1.2

ENV PTTG_API_ENDPOINT localhost
ENV USER pttg
ENV GROUP pttg
ENV NAME pttg-income-proving-ui

ARG JAR_PATH
ARG VERSION

WORKDIR /app

RUN groupadd -r ${GROUP} && \
    useradd -r -g ${USER} ${GROUP} -d /app && \
    mkdir -p /app && \
    chown -R ${USER}:${GROUP} /app

RUN curl --silent --location https://rpm.nodesource.com/setup_5.x | bash -
RUN yum install -y nodejs git-all wget unzip
ADD package.json /app/package.json
ADD bower.json /app/bower.json
RUN npm install && node_modules/bower/bin/bower install --allow-root

ADD ${JAR_PATH}/${NAME}-${VERSION}.jar /app
ADD run.sh /app

RUN chmod a+x /app/run.sh

EXPOSE 8080

ENTRYPOINT /app/run.sh
