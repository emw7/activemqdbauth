# syntax = docker/dockerfile:experimental
# This line above is necessary to activate buildkit on all platforms as of 2021/03.

# Only for getting the activemq libraries.
FROM apache/activemq-classic:5.18.3 as lib-stage

FROM maven:3.9.6-amazoncorretto-11 as build-stage

ARG SKIPTEST=false
ARG SYSSKIPTEST=-DskipTests
ARG PROJECT_DIR=/project

WORKDIR $PROJECT_DIR

RUN mkdir -p /opt/apache-activemq/lib

COPY . $PROJECT_DIR

COPY --from=lib-stage /opt/apache-activemq/lib /opt/apache-activemq/lib

RUN --mount=type=cache,target=/emw7 \
    if [ "$SKIPTEST" = false ]; then SYSSKIPTEST=""; fi \
    && echo SKIPTEST: $SKIPTEST, SYSSKIPTEST: $SYSSKIPTEST \
    && export MVNOPTS="-Dmaven.repo.local=/emw7/pkg/mvnrepo" \
    && echo COMMIT_SHA: $COMMIT_SHA \
    && echo PIPELINE_ID: $PIPELINE_ID \
    && mvn -e -B  \
           -s maven_settings.xml \
           package dependency:copy-dependencies -DincludeScope=runtime \
           -U $MVNOPTS \
           -Dbuild.sha=$COMMIT_SHA -Dbuild.pipeline.id=$PIPELINE_ID \
    && ./lib.sh

# Stop at this stage with the option "--target export-stage"
# to obtain an image with only the build results, convenient to
# use with the "-o path/to/dir" option of docker build.
FROM scratch as export-stage
ARG PROJECT_DIR=/project
COPY --from=build-stage $PROJECT_DIR/target/activemqdbauth.jar .
COPY --from=build-stage $PROJECT_DIR/build/lib/ ./lib

