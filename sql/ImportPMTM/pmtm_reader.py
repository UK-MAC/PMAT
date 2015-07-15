import csv
import time
import os
import logging
from collections import namedtuple

Application = namedtuple('Application', 'name version')
Machine     = namedtuple('Machine', 'vendor name')
Parameter   = namedtuple('Parameter', 'rank name value')
Timer       = namedtuple('Timer', 'rank name value error count pause')
Overhead    = namedtuple('Overhead', 'rank name value error')
OS          = namedtuple('OS', 'vendor name version kernel')
Processor   = namedtuple('Processor', 'vendor name arch clock nprocs nthreads')
Compiler    = namedtuple('Compiler', 'vendor name version')
MPI         = namedtuple('MPI', 'vendor name version')

DATE_FORMAT = '%d-%m-%Y'
TIME_FORMAT = '%H:%M'

#======================================================================================================================

class Reader(object):

    def __init__(self):
        self.environ     = {}
        self.datetime    = 0
        self.overheads   = []
        self.parameters  = []
        self.timers      = []
        self.flags       = []
        self.run_id      = None
        self.nprocs      = None
	self.max_openmp_threads = None
        self.application = None
        self.compiler    = None
        self.machine     = None
        self.mpi         = None
        self.os          = None
        self.processor   = None

    def parse(self, file):
        self.file_name = os.path.abspath(file.name)
        first_line = True

        for line in csv.reader(file):
            if line:
                logging.debug('Reading line starting with "%s"' % line[0])
            if first_line:
                if line != ['Performance Modelling Timing File']:
                    raise Exception('Invalid PMTM file')
                first_line = False
            elif line == ['End of File']:
                break
            elif line:
                self.parse_line(line)

        if line != ['End of File'] and self.version > (0, 2, 6):
            raise Exception('Incomplete PMTM file: "%s"' % self.file_name)

    def process_pmtm_version(self, cols):
        self.version = tuple(int(i) for i in cols[0].split('.'))

    def process_application(self, cols):
        tokens  = cols[0].split(' ')
        name    = tokens[0]
        version = ' '.join(tokens[1:])
        self.application = Application(name, version)

    def process_run_id(self, cols):
        self.run_id = cols[0]

    def process_nprocs(self, cols):
        self.nprocs = cols[0]

    def process_max_openmp_threads(self, cols):
	self.max_openmp_threads = cols[0]

    def process_machine(self, cols):
        if self.version <= (2, 1, 1):
            # Inserting 'None' for vendor in older PMTM files.
            cols.insert(0, None)
        self.machine = Machine._make(cols)

    def process_processor(self, cols):
        self.processor = Processor._make(cols)

    def process_os(self, cols):
        self.os = OS._make(cols)

    def process_system(self, cols):
        # Duplication of process_os to handle different name in PMTM files.
        self.os = OS._make(cols)

    def process_compiler(self, cols):
        self.compiler = Compiler._make(cols)

    def process_mpi(self, cols):
        self.mpi = MPI._make(cols)

    def process_tag(self, cols):
        self.tag = cols[0]

    def process_flags(self, cols):
        self.flags = cols

    def process_date(self, cols):
        self.datetime += time.mktime(time.strptime(cols[0], DATE_FORMAT))

    def process_time(self, cols):
        no_year_time = time.mktime(time.strptime('0', '%M'))
        self.datetime += (time.mktime(time.strptime(cols[0], TIME_FORMAT)) - no_year_time)

    def process_overhead(self, cols):
        self.overheads.append(Overhead._make(cols[::2]))

    def process_parameter(self, cols):
        self.parameters.append(Parameter._make(cols[::2]))

    def process_timer(self, cols):
        self.timers.append(Timer._make(cols[:7:2] + cols[9::2]))

    def process_environ(self, cols):
        tokens = ', '.join(cols).split('=')
        name   = tokens[0]
        value  = '='.join(tokens[1:])
        self.environ[name] = value

    def process_specific(self, cols):
        name   = cols[0]
        value  = cols[2]
	self.parameters.append(Parameter._make(['0',name,value]))

    def process(self, name, cols):
        if name != '#Type':
            method = getattr(self, 'process_%s' % name.lower().replace(' ', '_'))
            method(cols)

    def parse_line(self, cols):
        line_type = cols[0].strip()
        if line_type:
	    if line_type.lower() == 'specific':
                self.process(line_type, [i.strip() for i in cols[1:]])
	    else:	
                self.process(line_type, [i.strip() for i in cols[2:]])

#======================================================================================================================

