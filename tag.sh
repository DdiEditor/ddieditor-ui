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

versionbump ddadb
versionbump jounal-study-info-export
versionbump ddieditor-osiris-batch-convert
versionbump ddieditor-osiris2ddi3

versionbump ddi-3-xmlbeans
versionbump ddieditor
versionbump ddieditor-classification
versionbump ddieditor-model
versionbump ddieditor-line
versionbump ddieditor-spss
versionbump ddieditor-sample
versionbump ddieditor-spss-stat
versionbump ddieditor-spss-xmlbeans
versionbump ddieditor-ui
versionbump ddieditor-ui-headlessbuild
versionbump ddieditor-xmlbeans
versionbump lib-java
versionbump util

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

echo 'Name of branch for SVN tag:'
read name

svnurl=svn://192.168.99.101
memo='Dev release taging: $version'
svn mkdir $svnurl/dda/tags/ddiftp/release-$version -m '$memo'

# dbdda
svn copy $svnurl/dda/branches/ddieditor/$name/ddadb $svnurl/dda/tags/ddiftp/release-$version/ddadb -m '$memo' 

# jounal-study-info-export
svn copy $svnurl/dda/branches/ddieditor/$name/jounal-study-info-export $svnurl/dda/tags/ddiftp/release-$version/jounal-study-info-export -m '$memo'

# ddieditor-osiris-batch-convert
svn copy $svnurl/dda/branches/ddieditor/$name/ddieditor-osiris-batch-convert $svnurl/dda/tags/ddiftp/release-$version/ddieditor-osiris-batch-convert -m '$memo'

# ddieditor-osiris2ddi3
svn copy $svnurl/dda/branches/ddieditor/$name/ddieditor-osiris2ddi3 $svnurl/dda/tags/ddiftp/release-$version/ddieditor-osiris2ddi3 -m '$memo'

# ddiftp
svn copy $svnurl/dda/branches/ddieditor/$name/ddiftp $svnurl/dda/tags/ddiftp/release-$version/ -m '$memo'

else
echo 'No SVN tagging taking place'
fi

#
# End
#
echo '--- The end, thank you for flying DDA RCP build script today ---'
