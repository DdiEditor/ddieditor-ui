#! /bin/sh
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

echo '--- Resource ddieditor-spss ---'
cd ../ddieditor-spss
ant resource -f dda-build.xml

echo '--- Done setup ---'
