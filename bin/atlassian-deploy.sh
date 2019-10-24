#!/bin/bash
set -xe

SCRIPT_DIR=${0%/*}
DIST_DIR=$SCRIPT_DIR/../dist
MAVEN_BASE_URL=https://packages.atlassian.com
VERSION_FILE=$SCRIPT_DIR/../build/VERSION.txt

if [ ! -f $VERSION_FILE ]; then
    echo "Version file $VERSION_FILE not found - did you build?";
    exit 1
fi

VERSION=`cat $VERSION_FILE`

case $1 in
"snapshot")
    POM_DIR=$SCRIPT_DIR/../build/snapshot
    SUFFIX="-SNAPSHOT"
    MAVEN_URL=$MAVEN_BASE_URL/maven/3rdparty-snapshot
    MAVEN_REPO=atlassian-3rdparty-snapshot
    ;;
"release")
    POM_DIR=$SCRIPT_DIR/../dist
    SUFFIX=
    MAVEN_URL=$MAVEN_BASE_URL/maven/3rdparty
    MAVEN_REPO=atlassian-3rdparty
    ;;
*)
    echo "Usage: $0 [snapshot|release]"
    exit 1
esac

shopt -s nullglob
POM_FILES=($POM_DIR/*-$VERSION$SUFFIX.pom)
if [ ${#POM_FILES[@]} -eq 0 ]; then
    echo "No POM files found in $POM_DIR for version $VERSION$SUFFIX!";
    exit 1
fi

set +x
# set parameter to TRUE to actually deploy, otherwise this does a dry run (ie. check if all files exist)
function deploy() {
    for POM in ${POM_FILES[@]}
    do
        BASE=$(basename $POM $SUFFIX.pom)
        ARTIFACT_ID=$(basename $POM -$VERSION$SUFFIX.pom)

        if [ "$1" = false ]; then
            echo "Checking for dist and source JAR for POM $POM"
        fi

        JAR=$DIST_DIR/$BASE.jar
        if [ ! -f $JAR ]; then
            echo "JAR file $JAR not found!"
            exit 1
        fi

        SOURCE_JAR=$DIST_DIR/$BASE-sources.jar
        if [ ! -f $SOURCE_JAR ]; then
            echo "Source JAR file $SOURCE_JAR not found!"
            exit 1
        fi

        if [ "$1" = true ]; then
            echo "Deploying $ARTIFACT_ID:"
            echo "    POM: $POM"
            echo "    Binary: $JAR"
            echo "    Source: $SOURCE_JAR"
            mvn deploy:deploy-file -Durl=$MAVEN_URL \
                   -DrepositoryId=$MAVEN_REPO \
                   -Dfile=$JAR \
                   -DgroupId=org.codehaus.jackson \
                   -DartifactId=$ARTIFACT_ID \
                   -Dversion=$VERSION$SUFFIX \
                   -Dpackaging=jar \
                   -DpomFile=$POM \
                   -Dsources=$SOURCE_JAR
        fi

    done
}

deploy false
if [ ! "$2" = "--dry-run" ]; then
    echo "Files checked OK, starting deploy..."
    deploy true
fi