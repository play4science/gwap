#!/usr/bin/python
import MySQLdb
import codecs

conn = MySQLdb.connect (
	host = "localhost",
	user = "root",
	passwd = "kneissl",
	db = "metropolitalia_original",
	use_unicode = True,
)

geoPointId=0
locationId=0
locationgeopointId=0

def createLocation(f, options):
	global locationId
	locationId += 1
	global locationgeopointId
	locationgeopointId += 1
	for opt in ('name', 'type'):
		if options.has_key(opt):
			options[opt] = "'%s'" % (options[opt].replace("'","''"),)
	for opt in ('name', 'type', 'containedIn'):
		if not options.has_key(opt) or not options[opt]:
			options[opt] = 'NULL'
	f.write("insert into location (id, name, type, containedin_id) values (%d, %s, %s, %s);\n" %
			(locationId, options['name'], options['type'], options['containedIn']))
	if options.has_key('geolocation'):
		f.write("insert into locationgeopoint (id, location_id, geopoint_id) values (%d, %d, %d);\n" %
				(locationgeopointId, locationId, options['geolocation']))
	return locationId

def createGeoPoint(f, latitude, longitude):
	global geoPointId
	geoPointId += 1
	f.write("insert into geopoint (id, latitude, longitude) values (%d, %f, %f);\n" % (geoPointId, latitude, longitude))
	return geoPointId


with codecs.open("locations.sql", mode="w", encoding="utf-8") as f:
	f.write("begin transaction;\n")
	cursor = conn.cursor()

	# Country
	countryId = createLocation(f, { 'name':'Italia', 'type':'COUNTRY' })

	# Areas
	areaIds = {}
	for area in ('NO', 'NW', 'M', 'S', 'SA'):
		areaIds[area] = createLocation(f, { 'name':area, 'type':'AREA', 'containedIn':countryId })
	
	# Regions
	cursor.execute("select name, zona, ci_id_regione from regioni order by name")
	regionIds = { 0L: None, }
	for row in cursor.fetchall():
		regionIds[row[2]] = createLocation(f, { 'name':row[0], 'type':'REGION', 'containedIn':areaIds[row[1]] })
	
	# Provinces
	#cursor.execute("select name, ci_id_regione, ci_id_comune from comuni where capoluogo=1 order by name")
	provinceIds = { 0L: None, }
	#for row in cursor.fetchall():
	#	provinceIds[row[2]] = createLocation(f, { 'name':row[0], 'type':'PROVINCE', 'containedIn':regionIds[row[1]] })
	with codecs.open('province_ci.txt', mode='r', encoding='utf-8') as fr:
		readProvinces={}
		for line in fr:
			row = line.strip().split("\t")
			if not readProvinces.has_key(row[2]):
				provinceIds[int(row[0])] = createLocation(f, { 'name':row[2], 'type':'PROVINCE', 'containedIn':regionIds[int(row[1])] })
				readProvinces[row[2]] = True

	# Municipalities
	cursor.execute("select name, longitude, latitude, ci_id_comune, ci_id_regione from comuni c order by ci_id_regione, name")
	for row in cursor.fetchall():
		pId = createGeoPoint(f, row[1], row[2])
		createLocation(f, { 'name':row[0], 'type':'MUNICIPALITY', 'containedIn':provinceIds[row[4]], 'geolocation':pId })

	cursor.close()
	conn.close()
	f.write("commit;\n")
