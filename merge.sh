#!/bin/sh

# svn merge script

#svn path
SVN_PATH=svn://192.168.99.101/dda
LOCAL_PROJECT_PATH=/home/dak/workspace-trunk
SVN_MERGE_TO=trunk
SVN_BRANCH=branches/ddieditor/anakin
SVN_DRY_RUN=--dry-run

#
# function merge()
# $1 arg: name of module to merge
#
svnmerge()
{
echo ---
echo --- $2
echo ---
cd $2
svn merge $SVN_DRY_RUN $SVN_PATH/$SVN_MERGE_TO/$1/$2 $SVN_PATH/$SVN_BRANCH/$1/$2 $LOCAL_PROJECT_PATH/$2
cd ..
echo ''
}

# execute 
echo '--- Run SVN merge with dry-run ---'
echo 'Do you want to run SVN merge with dry-run: [yes|no]'
read svndry
if [ $svndry = 'yes' ]
then
echo 'Running SVN merge with dry-run!'
else
echo 'Merging SVN modules!'
SVN_DRY_RUN=''
fi

cd ..

#svnmerge '' ddadb
#svnmerge '' jounal-study-info-export
#svnmerge '' ddieditor-osiris-batch-convert
#svnmerge '' ddieditor-osiris2ddi3

#svnmerge ddiftp ddi-3-xmlbeans
#svnmerge ddiftp ddieditor
svnmerge ddiftp ddieditor-classification
#svnmerge ddiftp ddieditor-model
#svnmerge ddiftp ddieditor-line
#svnmerge ddiftp ddieditor-spss
svnmerge ddiftp ddieditor-sample
#svnmerge ddiftp ddieditor-spss-stat
#svnmerge ddiftp ddieditor-spss-xmlbeans
#svnmerge ddiftp ddieditor-ui
#svnmerge ddiftp ddieditor-ui-headlessbuild
#svnmerge ddiftp ddieditor-xmlbeans
#svnmerge ddiftp lib-java
#svnmerge ddiftp util

cd ddieditor-ui
echo '--- Merge done! Now resolve confilicts and commit the changes ;- )  ---'

