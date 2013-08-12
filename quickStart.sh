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
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-spss ---'
cd ../ddieditor-spss
ant resource -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-spss-stat ---'
cd ../ddieditor-spss-stat
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

echo '--- Resource ddieditor-osiris-batch-convert ---'
cd ../ddieditor-osiris-batch-convert
ant  deploy-to-ddieditor-ui -f dda-build.xml
echo '--- Done setup ---'

echo '--- Resource ddi-html ---'
cd ../ddi-html
ant deploy-to-ddieditor-ui -f dda-build.xml
echo '--- Done setup ---'

echo '--- Resource ddieditor-classification ---'
cd ../ddieditor-classification
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-genericode ---'
cd ../ddieditor-genericode
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Resource ddieditor-marc ---'
cd ../ddieditor-marc
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Done setup ---'
