#! /bin/sh

#
# build products and properties
#
echo '--- ddi-3-xmlbeans ---'
cd ../ddi-3-xmlbeans
ant init

echo '--- ddieditor-ui ---'
cd ../ddieditor-ui
ant resource

echo '--- edit db connections ---'
cd ../ddadb
vi db.properties

echo '--- copy journal-study-info-export setup ---'
cd ../jounal-study-info-export
ant hibernate-tool-deploy
ant deploy-to-ddieditor-ui

echo '--- check db connection ---'
vi ../ddieditor-ui/bin/resources/hibernate/hibernate.cfg.xml

#
# version bump
#
echo '--- increment bundle versions ---'
# lib-java
vi ../lib-java/META-INF/MANIFEST.MF

# util
vi ..lib-java/META-INF/MANIFEST.MF

# ddieditor

# ddieditor-ui

# ddadb

# jounal-study-info-export

# ddieditor-ui product bundle

echo '--- svn commit incremented bundle versions ---'

#
# tag build in svn
#

#
# copy stuff into right place in final product
#
