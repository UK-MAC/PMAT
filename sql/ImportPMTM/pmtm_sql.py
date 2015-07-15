import re
import types
import time
import datetime
import logging
import MySQLdb as sql

LOGGER = logging.getLogger('SQL')

#======================================================================================================================

class Cursor(object):
    def __init__(self, cur):
        self._cur = cur
    def __enter__(self):
        return self._cur
    def __exit__(self, *args):
        self._cur.close()

#======================================================================================================================

class Connection(object):

    URL      = <SQL Web Address>
    UserName = <Database Username>
    PassWord = <User Password>
    Database = 'ichnaea'

    def __init__(self):
        self._conn = sql.Connect(self.URL, self.UserName, self.PassWord)
        self._conn.autocommit(False)
        self._conn.select_db(self.Database)
    def __enter__(self):
        return self._conn
    def __exit__(self, *args):
        self._conn.close()

#======================================================================================================================

class SqlObject(object):

    BusinessKey = ()
    TableName   = None
    
    def __init__(self, values):
        self.values = values
        self.id     = None

    def fetch_or_save(self, cur):
        self.fetch(cur)
        if self.id is None:
            self.save(cur)

    def save(self, cur, update_id=True):
        SQL = 'INSERT INTO %s (%s) VALUES (%s)' % (self.TableName, self.save_names(), self.save_values())
        LOGGER.debug(SQL)
        n = cur.execute(SQL)
        if n == 0:
            raise Exception('Failed to save %s' % self.TableName)
        if update_id:
            self.fetch(cur)
            if self.id is None:
                raise Exception('Failed to save %s' % self.TableName)
        
    def fetch(self, cur):
        SQL = 'SELECT ID FROM %s WHERE %s' % (self.TableName, self.find_clause())
        LOGGER.debug(SQL)
        n = cur.execute(SQL)
        if n == 1:
            self.id = cur.fetchone()[0]
        elif n > 1:
            raise Exception('Multiple entries found for %s' % self.TableName)

    def delete(self, cur):
        SQL = 'DELETE FROM %s WHERE ID=%d' % (self.TableName, self.id)
        LOGGER.debug(SQL)
        n = cur.execute(SQL)
        if n == 0:
            raise Exception('Failed to delete %s' % self.TableName)

    def save_names(self):
        return ', '.join(self.values.iterkeys())

    def save_values(self):
        return ', '.join(_sql_tostring(value) for value in self.values.itervalues())

    def find_clause(self):
        return ' AND '.join('%s=%s' % (name, _sql_tostring(self.values[name])) for name in self.BusinessKey)

#======================================================================================================================

class Application(SqlObject):

    BusinessKey = ('Name', 'VersionMajor', 'VersionMinor', 'VersionCode')
    TableName   = 'Application'

    def __init__(self, name, version):
        (major, minor, build, code) = _parse_version(version)
        SqlObject.__init__(self, {
            'Name'        : name,
            'VersionMajor': major,
            'VersionMinor': minor,
            'VersionBuild': build,
            'VersionCode' : code,
            'Description' : '',
            'Private'     : False,
            'FullName'    : '',
        })

#======================================================================================================================

class Compiler(SqlObject):

    BusinessKey = ('Name', 'Vendor', 'VersionMajor', 'VersionMinor', 'VersionBuild')
    TableName   = 'Compiler'

    def __init__(self, name, vendor, version):
        (major, minor, build, _) = _parse_version(version)
        SqlObject.__init__(self, {
            'Name'        : name,
            'Vendor'      : vendor,
            'VersionMajor': major,
            'VersionMinor': minor,
            'VersionBuild': build,
        })

#======================================================================================================================

class Machine(SqlObject):

    BusinessKey = ('Name', 'Vendor')
    TableName   = 'Machine'

    def __init__(self, name, vendor):
        SqlObject.__init__(self, {
            'Name'  : name,
            'Vendor': vendor,
        })

#======================================================================================================================

class MPI(SqlObject):

    BusinessKey = ('Name', 'Vendor', 'VersionMajor', 'VersionMinor', 'VersionBuild')
    TableName   = 'MPI'

    def __init__(self, name, vendor, version):
        (major, minor, build, _) = _parse_version(version)
        SqlObject.__init__(self, {
            'Name'        : name,
            'Vendor'      : vendor,
            'VersionMajor': major,
            'VersionMinor': minor,
            'VersionBuild': build,
        })

#======================================================================================================================

class OS(SqlObject):

    BusinessKey = ('Name', 'VersionMajor', 'VersionMinor', 'VersionBuild', 'Vendor', 'VersionBuildMinor', 'Kernel')
    TableName   = 'OperatingSystem'

    def __init__(self, name, vendor, version, kernel):
        (major, minor, build, build_minor) = _parse_version(version)
        SqlObject.__init__(self, {
            'Name'             : name,
            'Vendor'           : vendor,
            'VersionMajor'     : major,
            'VersionMinor'     : minor,
            'VersionBuild'     : build,
            'VersionBuildMinor': build_minor,
            'Kernel'           : kernel,
        })

#======================================================================================================================

class Processor(SqlObject):

    BusinessKey = ('Name', 'Vendor', 'ProcessorArchitecture', 'CoresPerProcessor', 'ThreadsPerCore', 'ClockSpeedHz')
    TableName   = 'Processor'

    def __init__(self, name, vendor, arch, clock, nprocs, nthreads):
        SqlObject.__init__(self, {
            'Name'                 : name,
            'Vendor'               : vendor,
            'ProcessorArchitecture': arch,
            'CoresPerProcessor'    : int(nprocs),
            'ThreadsPerCore'       : int(nthreads),
            'ClockSpeedHz'         : int(clock),
        })

#======================================================================================================================

class Flag(SqlObject):

    BusinessKey = ('Flag',)
    TableName   = 'Flags'

    def __init__(self, flag):
        SqlObject.__init__(self, {'Flag': flag})

#======================================================================================================================

class Run(SqlObject):

    BusinessKey = ('RunCreator', 'Machine', 'OperatingSystem', 'Processor', 'Compiler', 'MPI', 'Application', 'RunDate', 'Tag')
    TableName   = 'Run'

    def __init__(self, creator, machine, os, processor, compiler, mpi, application, run_date, run_id, tag, file):
        SqlObject.__init__(self, {
            'RunCreator'     : creator,
            'Date'           : sql.TimestampFromTicks(time.time()),
            'Private'        : False,
            'Machine'        : machine.id if machine else None,
            'OperatingSystem': os.id if os else None,
            'Processor'      : processor.id if processor else None,
            'Compiler'       : compiler.id if compiler else None,
            'MPI'            : mpi.id if mpi else None,
            'Application'    : application.id if application else None,
            'RunDate'        : sql.TimestampFromTicks(run_date),
            'RunID'          : run_id,
            'Tag'            : tag,
            'File'           : file,
        })
        self.run_id = run_id

    def fetch(self, cur):
        if self.run_id:
            SQL = 'SELECT ID FROM %s WHERE RunID=%s' % (self.TableName, _sql_tostring(self.run_id))
            LOGGER.debug(SQL)
            n = cur.execute(SQL)
            if n == 1:
                self.id = cur.fetchone()[0]
            elif n > 1:
                raise Exception('Multiple entries found for %s' % self.TableName)
        else:
            SqlObject.fetch(self, cur)

#======================================================================================================================

class SubRun(SqlObject):

    BusinessKey = ('ParentRun', 'Sequence')
    TableName   = 'SubRun'

    def __init__(self, run, sequence):
        SqlObject.__init__(self, {'ParentRun': run.id, 'Sequence': sequence})

#======================================================================================================================

class Parameter(SqlObject):

    TableName = 'Parameter'

    def __init__(self, sub_run, name, value, rank):
        double_value  = None
        integer_value = None
        string_value  = None

        value = value.strip()

        if value.lower() in ('true', 'false'):
            string_value = value.lower()
            value_type   = 'String'
        elif re.compile('^[+-]?\d+$').match(value):
            integer_value = int(value)
            value_type    = 'Integer'
        elif re.compile(r'^[+-]?\d+\.\d*([eE][+-]?\d+)?$').match(value):
            double_value = float(value)
            value_type   = 'Double'
        else:
            string_value = value
            value_type   = 'String'

        SqlObject.__init__(self, {
            'Name'        : name,
            'Type'        : value_type,
            'SubRunOwner' : sub_run.id,
            'DoubleValue' : double_value,
            'IntegerValue': integer_value,
            'StringValue' : string_value,
            'Rank'        : _rank_to_int(rank,0),
	    'ThreadID'	  : _rank_to_int(rank,1),
        })

#======================================================================================================================

class Timer(SqlObject):

    TableName = 'Result'

    def __init__(self, sub_run, name, value, rank, error, count, pause):
        SqlObject.__init__(self, {
            'Name'      : name,
            'Value'     : float(value),
            'Error'     : float(error),
            'Rank'      : _rank_to_int(rank,0),
	    'ThreadID'	: _rank_to_int(rank,1),
            'ErrorType' : 'PMTM_STANDARD',
            'SubRun'    : sub_run.id,
            'Count'     : int(count),
            'PauseCount': int(pause),
        })

#======================================================================================================================

class Overhead(Timer):

    def __init__(self, sub_run, name, value, rank, error):
        name = 'Timer overhead for ' + name
        Timer.__init__(self, sub_run, name, value, 'All Ranks', error, '10000', '0')

#======================================================================================================================

def _parse_version(version_string):
    match = re.compile(r'[vV]?(\d+)\.(\d+)(\.(\d+))?(.*)').match(version_string)
    if match:
        major = int(match.group(1))
        minor = int(match.group(2))
        build = int(match.group(4)) if match.group(4) else 0
        code  = match.group(5).strip()
        return (major, minor, build, code)
    
    match = re.compile(r'(\d{2})(\d)(\d)').match(version_string)
    if match:
        major = int(match.group(1))
        minor = int(match.group(2))
        build = int(match.group(3))
        code  = ''
        return (major, minor, build, code)

    return (0, 0, 0, 0)

#======================================================================================================================

def _sql_tostring(obj):
    if type(obj) in types.StringTypes:
        return sql.string_literal(obj)
    elif type(obj) in (types.IntType, types.LongType, types.FloatType):
        return str(obj)
    elif type(obj) in (types.BooleanType,):
        return str(int(obj))
    elif type(obj) in (types.NoneType,):
        return sql.NULL
    elif type(obj) in (datetime.datetime,):
        return sql.string_literal(str(obj))
    else:
        raise Exception('Unknown object type %s' % type(obj))

#======================================================================================================================

def _rank_to_int(rank,thread):
    rank_map = {
        'All Ranks'   : -1,
        'Any Rank'    : -2,
        'Rank Average': -3,
        'Rank Maximum': -4,
        'Rank Minimum': -5,
        'Unknown'     : -6,
    }
    if rank in rank_map:
	if thread == 1:
		return None
	else:
		return rank_map[rank]
    else:
	if rank.isdigit() and thread != 1:
	    return int(rank)
	elif rank.isdigit():
	    return None
	else:
	    (r,t) = rank.split('.')
	    if thread == 1:
		return int(t)
	    else:
		return int(r)

#======================================================================================================================

