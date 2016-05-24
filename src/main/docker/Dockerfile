FROM quay.io/ukhomeofficedigital/openjdk8:v0.1.2

# prequisite - docker cp FCW_BUILD_CONTAINER:/code/build/libs/uk.gov.digital.ho.proving.income.family.cwtool-0.1.0.jar .
# nb - current working directory is 'code'

ADD pttg-family-migration-ui-*.jar app.jar

# as current working dir is code
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/code/app.jar"]



