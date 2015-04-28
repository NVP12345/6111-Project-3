#!/usr/bin/python
import sys
import inspect
from sets import Set

class Row:
    def __init__(self, grade, year, category, meanscore, level1Pct, level2Pct, level3Pct, level4Pct, level3And4Pct):
        frame = inspect.currentframe()
        args, _, _, values = inspect.getargvalues(frame)
        for i in args:
            setattr(self, i, values[i])
            setattr(self, 'print_' + i, i + "=" + str(values[i]))

def compute_meanscore_range(meanscore):
    try:
        meanscore = int(meanscore)
    except ValueError, e:
        return 'Unknown'

    if meanscore < 675:
        return '<675'
    if meanscore < 700:
        return '675-699'
    return '700+'

def compute_pct_range(pct):
    try:
        pct = float(pct.replace('%', ''))
    except ValueError, e:
        return 'Unknown'

    if pct < 10:
        return '<10%'
    if pct < 20:
        return '10-20%'
    if pct < 30:
        return '20-30%'
    if pct < 40:
        return '30-40%'
    if pct < 50:
        return '40-50%'
    if pct < 60:
        return '50-60%'
    if pct < 70:
        return '60-70%'
    if pct < 80:
        return '70-80%'
    if pct < 90:
        return '80-90%'
    return '90%+'

def parse_row(row):
    return Row(
        row[1].strip(),
        row[2].strip(),
        row[3].strip(),
        compute_meanscore_range(row[5].strip()),
        compute_pct_range(row[7].strip()),
        compute_pct_range(row[9].strip()),
        compute_pct_range(row[11].strip()),
        compute_pct_range(row[13].strip()),
        compute_pct_range(row[15].strip())
    )

def get_rows():
    with open(sys.argv[1], 'r') as lines:
        return Set(filter((lambda x: x.year == '2011' and (x.grade == '7' or x.grade == '8')), Set([parse_row(line.split(',')) for line in lines])))

def print_row(row):
    print "%s, %s, %s, %s, %s, %s, %s, %s" % (
        row.print_grade,
        row.print_category,
        row.print_meanscore,
        row.print_level1Pct,
        row.print_level2Pct,
        row.print_level3Pct,
        row.print_level4Pct,
        row.print_level3And4Pct
    )

if '__main__' == __name__:
    for row in get_rows():
        print_row(row)
