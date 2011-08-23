#!/bin/sh -v
#
# setup and build
#

#
# build products and properties
#
echo '--- Init ddi-3-xmlbeans ---'
cd ../ddi-3-xmlbeans
ant init 

echo '--- Resource ddieditor-ui ---'
cd ../ddieditor-ui
ant resource -f dda-build.xml

echo '--- Resource ddieditor-model ---'
cd ../ddieditor-model
pwd
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-spss ---'
cd ../ddieditor-spss
ant resource -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-line ---'
cd ../ddieditor-line
ant resource -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource jounal-study-info-export ---'
cd ../jounal-study-info-export
ant  deploy-to-ddieditor-ui -f dda-build.xml
echo '--- Done setup ---'
