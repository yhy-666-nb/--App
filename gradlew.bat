#!/bin/bash
cd "$(dirname "$0")"
chmod +x gradle/wrapper/gradle-wrapper.jar 2>/dev/null
./gradle/wrapper/gradle assembleDebug --no-daemon 2>/dev/null
