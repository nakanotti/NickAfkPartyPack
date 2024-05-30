#!/bin/bash

#
# Clean old data if different type or version.
#
if [ -d data ]; then
    source .env
    SPIGOT="$(ls -1 data/spigot_server-*.jar 2> /dev/null)"
    if [ -n "${SPIGOT}" ]; then
        TYPE="SPIGOT"
        VERSION="$(sed -re 's/.*server-(.+)\.jar$/\1/' <<< ${SPIGOT})"
    elif [ -f data/.paper.env ]; then
        source data/.paper.env
    fi
    echo "Current TYPE: ${TYPE}"
    echo "Current VERSION: ${VERSION}"
    bClean=false
    if [ ! "${TYPE}" = "${TEST_TYPE}" ]; then
        echo "TYPE:\"${TYPE}\" != \"${TEST_TYPE}\""
        bClean=true
    fi
    if [ ! "${VERSION}" = "${TEST_VERSION}" ]; then
        echo "TYPE:\"${VERSION}\" != \"${TEST_VERSION}\""
        bClean=true
    fi
    if "${bClean}"; then
        echo "Clean old data."
        rm -r data
    fi
fi

#
# wget -N
#
function download_if_update()
{
    local -r url="${1}"
    local -r local_file="${2:-$(basename ${1})}"
    local -r status=$(curl -LI "${url}" -o /dev/null -w '%{http_code}' -s -z "${local_file}")
    if [ "${status}" = "304" ]; then
        echo "No need download for ${local_file}"
        return 0
    fi
    echo "Download ... ${local_file}"
    curl -R "${url}" -o "${local_file}" -s
}

#
# Update plugins
#
pushd ./plugins > /dev/null
download_if_update "https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/build/libs/ProtocolLib.jar"
#download_if_update "https://ci.viaversion.com/job/ViaVersion-DEV/lastSuccessfulBuild/artifact/build/libs/ViaVersion-5.0.0-SNAPSHOT.jar"
#download_if_update "https://ci.viaversion.com/view/ViaBackwards/job/ViaBackwards-DEV/lastSuccessfulBuild/artifact/build/libs/ViaBackwards-5.0.0-SNAPSHOT.jar"
cp -vu ../../target/NickAfkPartyPack-*.jar ./
popd > /dev/null

#
# Launch Server
#
trap 'docker compose down' 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15
docker compose up
docker compose down
