#!/usr/bin/env bash
set -e
[ -n "${DEBUG}" ] && set -x

GRADLE_IMAGE="quay.io/ukhomeofficedigital/gradle-nodejs:v2.13.1"
GIT_COMMIT=${GIT_COMMIT:-$(git rev-parse --short HEAD)}
GIT_COMMIT=${GIT_COMMIT:0:7}
VERSION=$(grep ^version build.gradle | cut -d= -f 2 | tr -d ' ' | sed -e "s|'||g")

build_nodejs() {
  docker build -t "${GRADLE_IMAGE}" -f Dockerfile.nodejs .
}

build() {

  MOUNT="${PWD}:/code"
  # Mount in the local gradle cache into the docker container
  [ -d "${HOME}/.gradle/caches" ] && MOUNT="${MOUNT} -v ${HOME}/.gradle/caches:/root/.gradle/caches"

  # Mount in local maven repository into the docker container
  [ -d "${HOME}/.m2/repository" ] && MOUNT="${MOUNT} -v ${HOME}/.m2/repository:/root/.m2/repository"
  
  # Mount in local gradle user directory
  [ -d "${HOME}/.gradle" ] && MOUNT="${MOUNT} -v ${HOME}/.gradle:/root/.gradle"

  ENV_OPTS="GIT_COMMIT=${GIT_COMMIT} -e VERSION=${VERSION}"
  [ -n "${BUILD_NUMBER}" ] && ENV_OPTS="BUILD_NUMBER=${BUILD_NUMBER} -e ${ENV_OPTS}"

  docker run -e ${ENV_OPTS} -v ${MOUNT} "${GRADLE_IMAGE}" "${@}"
}

setProps() {
  [ -n "${BUILD_NUMBER}" ] && VERSION="${VERSION}-${BUILD_NUMBER}"
  [ -n "${GIT_COMMIT}" ] && VERSION="$VERSION.${GIT_COMMIT}"
  echo "VERSION=${VERSION}" >> version.properties
}

dockerBuild() {
  docker build --build-arg VERSION=${VERSION} --build-arg JAR_PATH=build/libs \
      -t quay.io/ukhomeofficedigital/pttg-income-proving-ui:${VERSION} .
}

dockerPublish() {
  docker push quay.io/ukhomeofficedigital/pttg-income-proving-ui:${VERSION}
}

build_nodejs
build "${@}"
setProps
dockerBuild
dockerPublish