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

def compute_agerange(age):
    try:
        age = int(age)
    except ValueError, e:
        return 'Unknown'

    if age < 20:
        return '<20'
    if age < 30:
        return '20-29'
    if age < 40:
        return '30-39'
    if age < 50:
        return '40-49'
    return '50+'

def compute_htrange(feet, inches):
    total_inches = None
    try:
        total_inches = 12 * int(feet) + int(inches)
    except ValueError, e:
        return 'Unknown'

    if total_inches < 60:
        return "<5'0''"
    if total_inches < 66:
        return "5'0''- 5'5''"
    if total_inches < 72:
        return "5'5''- 5'11''"
    if total_inches < 78:
        return "6'0''- 6'5''"
    return "6'6''+'"

def compute_weightrange(weight):
    try:
        weight = int(weight)
    except ValueError, e:
        return 'Unknown'

    if weight < 150:
        return '<150'
    if weight < 160:
        return '150-159'
    if weight < 170:
        return '160-169'
    if weight < 180:
        return '170-179'
    if weight < 190:
        return '180-189'
    if weight < 200:
        return '190-199'
    if weight < 225:
        return '200-224'
    if weight < 250:
        return '225-249'
    return '250+'

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
        'agerange=' + compute_agerange(row[83].strip()),
        'htrange=' + compute_htrange(row[84], row[85]),
        'weightrange=' + compute_weightrange(row[86].strip()),
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
