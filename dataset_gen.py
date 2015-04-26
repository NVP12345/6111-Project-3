#!/usr/bin/python
import sys
import inspect
from sets import Set

class Row:
    def __init__(self, datestop, pct, crimsusp, typeofid, arstmade, offunif, frisked, searched, contrabn, agerange, htrange, weightrange, haircolor, eyecolor, build, premname, city):
        frame = inspect.currentframe()
        args, _, _, values = inspect.getargvalues(frame)
        for i in args:
            setattr(self, i, values[i])

def parse_row(row):
    return Row(
        'datestop=' + row[3].strip(),
        'pct=' + row[1].strip(),
        'crimsusp=' + row[9].strip(),
        'typeofid=' + row[11].strip(),
        'arstmade=' + row[14].strip(),
        'offunif=' + row[20].strip(),
        'frisked=' + row[22].strip(),
        'searched=' + row[23].strip(),
        'contrabn=' + row[24].strip(),
        'agerange=' + row[83].strip(),
        'htrange=' + row[84] + "'" + row[85] + "''",
        'weightrange=' + row[86].strip(),
        'haircolor=' + row[87].strip(),
        'eyecolor=' + row[88].strip(),
        'build=' + row[89].strip(),
        'premname=' + row[94].strip(),
        'city=' + row[100].strip()
    )

def get_rows():
    with open(sys.argv[1], 'r') as lines:
        return Set(filter((lambda x: x.datestop[:10] == 'datestop=5'), Set([parse_row(line.split(',')) for line in lines])))

def print_row(row):
    print "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s" % (
        row.datestop,
        row.pct,
        row.crimsusp,
        row.typeofid,
        row.arstmade,
        row.offunif,
        row.frisked,
        row.searched,
        row.contrabn,
        row.agerange,
        row.htrange,
        row.weightrange,
        row.haircolor,
        row.eyecolor,
        row.build,
        row.premname,
        row.city
    )

if '__main__' == __name__:
    for row in get_rows():
        print_row(row)
