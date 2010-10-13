#! /bin/sh
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
cd ../ddadb
vi db.properties

echo '--- Copy journal-study-info-export setup ---'
cd ../jounal-study-info-export
ant hibernate-tool-deploy -f dda-build.xml
ant deploy-to-ddieditor-ui -f dda-build.xml

echo '--- Check db connection ---'
vi ../ddieditor-ui/bin/resources/hibernate/hibernate.cfg.xml

#
# version bump
#
echo '--- Increment bundle versions [Bundle-Version: ] in MANIFEST.MF files ---'
echo '--- And commit version bump in SVN ---'

function versionbump()
{
projectpath=$1
echo $projectpath
fullpath=../$projectpath/META-INF/MANIFEST.MF
vi $fullpath
svn ci $fullpath  -m 'Version bump'
return;
}

versionbump lib-java

versionbump util

versionbump ddieditor

versionbump ddieditor-ui

versionbump ddieditor-ui product bundle

versionbump ddadb

versionbump jounal-study-info-export

#
# tag build in svn
#
echo '--- Tag build in SVN ---'
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

#
# execute product build
#
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

function doresourcecopy()
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

#
# End
#
echo '--- The end, thank you for flying DDA RCP build script today ---'
