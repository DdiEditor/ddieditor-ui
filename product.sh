#!/bin/sh

#
# version bump
#
# function versionbump()
versionbump()
{
projectpath=$1
echo $projectpath
fullpath=../$projectpath/META-INF/MANIFEST.MF
vi $fullpath
svn ci $fullpath  -m 'Version bump'
return;
}

echo '--- Increment bundle versions [Bundle-Version: ] in MANIFEST.MF files ---'
echo '--- And commit version bump in SVN ---'
echo 'Do you want to version bump: [yes|no]'
read doversionbump
if [ $doversionbump = 'yes' ]
then
versionbump lib-java
versionbump util
versionbump ddieditor
versionbump ddieditor-ui
versionbump ddadb
versionbump jounal-study-info-export
versionbump ddieditor-spss
versionbump ddieditor-line

# ddieditor-ui product bundle
vi OSGI-INF/l10n/bundle.properties
svn ci OSGI-INF/l10n/bundle.properties -m 'Version bump'
else
echo 'No version bumping taking place'
fi

#
# tag build in svn
#
echo '--- Tag build in SVN ---'
echo 'Do you want to tag in SVN: [yes|no]'
read svntag
if [ $svntag = 'yes' ]
then
echo 'Submit version no. for SVN tag:'
read version

svnurl=svn://192.168.99.101
memo='Dev release taging: $version'
svn mkdir $svnurl/dda/tags/ddiftp/test-$version -m '$memo'

# dbdda
svn copy $svnurl/dda/trunk/ddadb $svnurl/dda/tags/ddiftp/test-$version/ddadb -m '$memo' 

# jounal-study-info-export
svn copy $svnurl/dda/trunk/jounal-study-info-export $svnurl/dda/tags/ddiftp/test-$version/jounal-study-info-export -m '$memo'

# ddiftp
svn copy $svnurl/dda/trunk/ddiftp $svnurl/dda/tags/ddiftp/test-$version/ -m '$memo'

else
echo 'No SVN tagging taking place'
fi

#
# setup and build
#
echo 'Do you want to setup and build: [yes|no]'
read build
if [ $build = 'yes' ]
then
#
# clean up deploy
#
rm deploy -r
mkdir deploy

#
# build products and properties
#
echo '--- Init ddi-3-xmlbeans ---'
cd ../ddi-3-xmlbeans
ant init 

echo '--- Resource ddieditor-ui ---'
cd ../ddieditor-ui
ant resource -f dda-build.xml

echo '--- Edit db connections ---'
vi ../ddadb/db.properties

echo '--- Copy journal-study-info-export setup ---'
cd ../jounal-study-info-export
#ant hibernate-tool-deploy -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml
cd ../ddieditor-ui/

echo '--- Check db connection ---'
vi bin/resources/hibernate/hibernate.cfg.xml

echo '--- Copy ddieditor-line ---'
cd ../ddieditor-line
ant resource -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Copy ddieditor-spss setup ---'
cd ../ddieditor-spss
ant deploy-to-ddieditor-ui -f dda-build.xml
cd ../ddieditor-ui/

#
# execute product build
#
echo
echo 'Execute product generation in Eclipse, hit RETURN when done'
read done

#
# copy stuff into right place in final product
#
echo '--- Copy resources into right place in arch distributions ---'
cd deploy
mkdir tmp
cp linux.gtk.x86/ddieditor/plugins/ddieditor-ui_*.jar tmp

cd tmp
jar fx ddieditor-ui_*.jar bin/resources
cd bin

#function doresourcecopy()
doresourcecopy()
{
archpath=$1
echo $archpath
fullpath=../../$archpath/ddieditor/
cp -r resources $fullpath 
ls $fullpath/resources -al
return;
}

doresourcecopy linux.gtk.x86
doresourcecopy linux.gtk.x86_64
doresourcecopy win32.win32.x86
doresourcecopy win32.win32.x86_64

cd ../..
rm tmp -r
else
echo 'Not setting up and not building'
fi


#
# Zip
#
echo '--- Zipping product builds ---'
#function zipbuild()
zipbuild()
{
path=$1
cd $path
zip -r ../ddieditor.$path.zip ddieditor
cd ..
}

echo 'Do you want to zip the product build: [yes|no]'
read zip
if [ $zip = 'yes' ]
then
echo 'Zipping product builds'
cd deploy
zipbuild linux.gtk.x86
zipbuild linux.gtk.x86_64
zipbuild win32.win32.x86
zipbuild win32.win32.x86_64
cd ..
else
echo 'Not zipping product build'
fi

#
# End
#
echo '--- The end, thank you for flying DDA RCP build script today ---'
