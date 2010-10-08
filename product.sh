#! /bin/sh

#
# build products
#

# ddi-3-xmlbeans
cd ../ddi-3-xmlbeans
ant init

# ddieditor-ui
cd ../ddieditor-ui
ant resource

# ddadb
cd ../ddadb
vi source/resources/dda-app.properties

#
# tag build in svn
#

#
# copy stuff into right place in final productcd
#
