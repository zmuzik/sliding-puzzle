#!/bin/bash

if [ $# -lt 1 ]; then
  echo "tsv file with translations not supplied"
  echo "usage:"
  echo "./generate_xlations.sh xlations.tsv"
  exit 0
fi

if [ ! -f $1 ]; then
  echo "file does not exist"
  exit 0
fi

RES_XML_HEADER="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
RES_LIST_START="<resources>"
RES_LIST_END="</resources>"
RES_ITEM_START="    <string name=\""
RES_ITEM_MIDDLE="\">"
RES_ITEM_END="</string>"
DEFAULT_LOC="en"

filename=$1
firstline=""

function getTargetFile() {
  if [[ $1 == "$DEFAULT_LOC" ]]; then
    echo -n "./app/src/main/res/values/strings.xml"
  else
    echo -n "./app/src/main/res/values-"$1"/strings.xml"
  fi
}

while read rawline
do
  # strip eol
  line=`echo -n "$rawline" | sed 's/\r$//g'`
  if [[ $firstline == "" ]]; then
    #process header
    firstline=$line
    #get all available languages and save them into $langs
    tokenscount=`echo "$firstline" | wc -w`
    for i in `seq 2 $tokenscount`
    do
      token=`echo -n "$firstline" | cut -d '	' -f $i`
      langs="$langs$token "
    done
    echo "languages:"
    echo $langs

    #write starting part of xml files
    for lang in $langs
    do
      targetfile=$(getTargetFile $lang)
      echo $RES_XML_HEADER > $targetfile
      echo $RES_LIST_START >> $targetfile
    done
  else
    #process regular lines
    key=`echo "$line" | cut -d '	' -f 1`
    echo $key

    for i in `seq 2 $tokenscount`
    do
      lang=`echo "$firstline" | cut -d '	' -f $i`
      targetfile=$(getTargetFile $lang)
      text=`echo "$line" | cut -d '	' -f $i`

      echo "${RES_ITEM_START}${key}${RES_ITEM_MIDDLE}${text}${RES_ITEM_END}" >> $targetfile
    done
  fi
done < $filename

for lang in $langs
do
  targetfile=$(getTargetFile $lang)
  echo $RES_LIST_END >> $targetfile
done
