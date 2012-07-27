#!/bin/sh
OUTFILE=locations.sql

for table in locations geopoints vtblgeopoints; do
	mysqldump -u mit -pmit --skip-opt --compatible=postgresql --complete-insert metropolitalia_original $table > table_$table.sql
	recode l1..u8 table_$table.sql
done

finishInsert() {
	head -c -2 $OUTFILE > tmp.sql
	cat tmp.sql > $OUTFILE
	rm tmp.sql
	echo ";" >> $OUTFILE
	echo "" >> $OUTFILE
}

echo "BEGIN TRANSACTION;" > $OUTFILE

echo "DELETE FROM locationgeopoint; DELETE FROM geopoint;" >> $OUTFILE

echo "INSERT INTO location (id, name, type, containedin_id) VALUES " >> $OUTFILE
grep INSERT table_locations.sql \
	| sed 's/.*VALUES (//' \
	| sed "s/\\\'/''/g" \
	| awk -F, '{print "(" $1 "," $4 "," toupper($7) "," $9 "),"}' \
	| sed 's/,0)/,NULL)/' \
	>> $OUTFILE
finishInsert

echo "INSERT INTO geopoint (id, latitude, longitude) VALUES " >> $OUTFILE
grep INSERT table_geopoints.sql \
	| sed 's/.*VALUES //;s/);/),/' >> $OUTFILE
finishInsert

echo "INSERT INTO locationgeopoint (id, location_id, geopoint_id) VALUES " >> $OUTFILE
grep INSERT table_vtblgeopoints.sql \
	| sed 's/.*VALUES //;s/);/),/' >> $OUTFILE
finishInsert

echo "COMMIT;" >> $OUTFILE

rm table_locations.sql table_geopoints.sql table_vtblgeopoints.sql
