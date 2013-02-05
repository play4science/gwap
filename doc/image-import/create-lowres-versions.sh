#!/bin/bash

error() {
	echo $*
	exit 1
}

#SIZES="1024x768 640x480"
SIZES="800x600"
QUALITY='80%'

#strip trailing "/"
IMAGE_DIR=${1%/}
cd $IMAGE_DIR || error "Please specify the directory where the images are"

BASENAME=`basename $IMAGE_DIR`

for SIZE in $SIZES; do
	mkdir ../$BASENAME-$SIZE
	for FILE in *; do
		[ -e "../$BASENAME-$SIZE/$FILE" ] && continue
		echo Converting $FILE...
		convert -quality $QUALITY -resize $SIZE "$FILE" "../$BASENAME-$SIZE/$FILE"
	done
done
echo done

