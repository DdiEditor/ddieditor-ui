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
git commit $fullpath  -m 'Version bump'
return;
}

#
# gittagging
#
# function gittagging()
gittagging()
{
projectpath=$1
version=$2
memo='Dev release taging: '$version
echo $projectpath
cd ../$projectpath
git tag -a $version -m '$memo'
return;
}

#
# gitpush
#
# function gitpush()
gitpush()
{
projectpath=$1
echo $projectpath
cd ../$projectpath
git push
return;
}


echo '--- Increment bundle versions [Bundle-Version: ] in MANIFEST.MF files ---'
echo '--- And commit version bump in SVN ---'
echo 'Do you want to version bump: [yes|no]'
read doversionbump
if [ $doversionbump = 'yes' ]
then
versionbump ddadb
versionbump ddieditor
versionbump ddieditor-bek1007
versionbump ddieditor-classification
versionbump ddieditor-genericode
versionbump ddieditor-line
versionbump ddieditor-model
versionbump ddieditor-osiris-batch-convert
versionbump ddieditor-print-ddic
versionbump ddieditor-spss
versionbump ddieditor-spss-stat
versionbump ddieditor-ui
versionbump jounal-study-info-export
versionbump lib-java
versionbump util

# ddieditor-ui product bundle
vi OSGI-INF/l10n/bundle.properties
git commit OSGI-INF/l10n/bundle.properties -m 'Version bump'
else
echo 'No version bumping taking place'
fi

#
# tag build in git
#
echo '--- Tag build in Git ---'
echo 'Do you want to tag in Git: [yes|no]'
read gittag
if [ $gittag = 'yes' ]
then
echo 'Submit version no. for Git tag:'
read version

gittagging bek1007-xmlbeans $version
gittagging ddadb $version
gittagging ddi-3-xmlbeans $version
gittagging ddieditor $version
gittagging ddieditor-bek1007 $version
gittagging ddieditor-classification $version
gittagging ddieditor-distribution $version
gittagging ddieditor-genericode $version
gittagging ddieditor-line $version
gittagging ddieditor-model $version
gittagging ddieditor-osiris-batch-convert $version
gittagging ddieditor-print-ddic $version
gittagging ddieditor-project $version
gittagging ddieditor-sample $version
gittagging ddieditor-spss $version
gittagging ddieditor-spss-stat $version
gittagging ddieditor-spss-xmlbeans $version
gittagging ddieditor-ui $version
gittagging ddieditor-ui-headlessbuild $version
gittagging ddieditor-xmlbeans $version
gittagging genericode-xmlbeans $version
gittagging jounal-study-info-export $version
gittagging lib-java $version
gittagging util $version
else
echo 'No Git tagging taking place'
fi

#
# git push to upstream
#
echo '--- Push to remove Git repository ---'
echo 'Do you want to push to upstream in Git: [yes|no]'
read gitpush
if [ $gitpush = 'yes' ]
then
gitpush bek1007-xmlbeans
gitpush ddadb
gitpush ddi-3-xmlbeans
gitpush ddieditor
gitpush ddieditor-bek1007
gitpush ddieditor-classification
gitpush ddieditor-distribution
gitpush ddieditor-genericode
gitpush ddieditor-line
gitpush ddieditor-model
gitpush ddieditor-osiris-batch-convert
gitpush ddieditor-print-ddic
gitpush ddieditor-project
gitpush ddieditor-sample
gitpush ddieditor-spss
gitpush ddieditor-spss-stat
gitpush ddieditor-spss-xmlbeans
gitpush ddieditor-ui
gitpush ddieditor-ui-headlessbuild
gitpush ddieditor-xmlbeans
gitpush genericode-xmlbeans
gitpush jounal-study-info-export
gitpush lib-java
gitpush util
else
echo 'No Git tagging taking place'
fi

#
# End
#
echo '--- The end, thank you for flying DDA RCP build script today ---'
