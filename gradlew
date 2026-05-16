#!/bin/bash
DIR = $(CD " $(dirname " $ 0 ")"；pwd)
JAVA_OPTS="-Xmx2048m "
exec " $ DIR/gradle/wrapper/gradle " " $ @ "
