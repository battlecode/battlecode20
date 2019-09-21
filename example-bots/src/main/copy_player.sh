#!/bin/bash
if [ -d $2 ]; then
	rm -r $2
fi
cp -r $1 $2
find $2 -name '*.java' | xargs sed -i "s/$1/$2/g"
