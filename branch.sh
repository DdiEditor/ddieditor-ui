#!/bin/sh

#
# Branch function
# 
dobranch()
{
echo ''
memo="Created $2 in branch: $1"

svnurl=svn://192.168.99.101/dda
branchpath=branches/ddieditor/$1/

svn copy -rHEAD $svnurl/trunk/$2 $svnurl/$branchpath -m '$memo'

echo ''
echo $memo
return;
}

echo '--- Branch code for new development sprit ---'
echo 'Do you want to branch: [yes|no]'
read dobranchddiftp
if [ $dobranchddiftp = 'yes' ]
then

echo ''
echo 'Enter branch name: '
read brachname

svnurl=svn://192.168.99.101/dda
branchpath=branches/ddieditor/$brachname/
svn mkdir $svnurl/$branchpath --parents -m 'Created new ddieditor branch'

dobranch $brachname ddiftp
dobranch $brachname ddadb
dobranch $brachname ddieditor-osiris-batch-convert
dobranch $brachname jounal-study-info-export

else
echo 'No branching taking place'
fi

#
# End
#
echo ''
echo '--- The end, thank you for flying ddieditor branch script today :) ---'
