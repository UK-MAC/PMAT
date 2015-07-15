#!/usr/bin/env python

import os
import sys
import logging
import shutil
from optparse import OptionParser

import pmtm_sql
import pmtm_reader

#======================================================================================================================

class PmtmSqlStore(object):

    PmtmStoreDirectory = <Store Directory>

    def __init__(self, conn, reader):
        self.reader      = reader
        self.conn        = conn
        self.application = None
        self.compiler    = None
        self.machine     = None
        self.mpi         = None
        self.os          = None
        self.processor   = None

    def set_store_file_name(self):
        self.store_file = os.path.join(
            self.PmtmStoreDirectory,
            'tmp',
            #self.reader.application.name,
            #self.reader.application.version,
            #self.reader.nprocs,
            #self.reader.machine.name,
            #self.reader.compiler.name,
            #self.reader.compiler.version,
            #self.reader.mpi.name,
            #self.reader.mpi.version,
        )

    def copy_file(self):
        if not os.path.exists(self.store_file):
            os.makedirs(self.store_file)
        shutil.copy(self.reader.file_name, self.store_file)

    def save(self):
        with pmtm_sql.Cursor(self.conn.cursor()) as self.cursor:
            self.save_application()
            self.save_compiler()
            self.save_machine()
            self.save_mpi()
            self.save_os()
            self.save_processor()
            self.save_flags()
            self.save_run()
            self.save_sub_runs()
        self.copy_file()
        self.cursor = None

    def save_parameters(self, sub_run):
        for parameter in self.reader.parameters:
            values = parameter._asdict()
            sql_param = pmtm_sql.Parameter(sub_run, **values)
            sql_param.save(self.cursor, update_id=False)
        if self.reader.nprocs:
            sql_param = pmtm_sql.Parameter(sub_run, name='PE Count', rank='Unknown', value=self.reader.nprocs)
            sql_param.save(self.cursor, update_id=False)

    def save_overheads(self, sub_run):
        for overhead in self.reader.overheads:
           values = overhead._asdict()
           sql_overhead = pmtm_sql.Overhead(sub_run, **values)
           sql_overhead.save(self.cursor, update_id=False)

    def save_timers(self, sub_run):
        for timer in self.reader.timers:
            values = timer._asdict()
            sql_timer = pmtm_sql.Timer(sub_run, **values)
            sql_timer.save(self.cursor, update_id=False)

    def save_sub_runs(self):
        sub_run = pmtm_sql.SubRun(
                run=self.run,
                sequence=0,
            )
        sub_run.fetch_or_save(self.cursor)
        self.save_parameters(sub_run)
        self.save_overheads(sub_run)
        self.save_timers(sub_run)

    def save_run(self):
        self.set_store_file_name()
        self.run = pmtm_sql.Run(
                creator=os.getlogin(),
                machine=self.machine,
                os=self.os,
                processor=self.processor,
                compiler=self.compiler,
                mpi=self.mpi,
                application=self.application,
                run_date=self.reader.datetime,
                run_id=self.reader.run_id,
                tag=self.reader.tag,
                file=self.store_file,
            )
        self.run.fetch(self.cursor)
        if self.run.id is not None:
            raise Exception('Run already in database')
        else:
            self.run.save(self.cursor)

    def save_flags(self):
        self.flags = []
        for flag in self.reader.flags:
            if flag:
                sql_flag = pmtm_sql.Flag(flag)
                sql_flag.fetch_or_save(self.cursor)
                self.flags.append(sql_flag)

    def save_application(self):
        if self.reader.application:
            values = self.reader.application._asdict()
            self.application = pmtm_sql.Application(**values)
            self.application.fetch_or_save(self.cursor)

    def save_compiler(self):
        if self.reader.compiler:
            values = self.reader.compiler._asdict()
            self.compiler = pmtm_sql.Compiler(**values)
            self.compiler.fetch_or_save(self.cursor)

    def save_machine(self):
        if self.reader.machine:
            values = self.reader.machine._asdict()
            self.machine = pmtm_sql.Machine(**values)
            self.machine.fetch_or_save(self.cursor)

    def save_mpi(self):
        if self.reader.mpi:
            values = self.reader.mpi._asdict()
            self.mpi = pmtm_sql.MPI(**values)
            self.mpi.fetch_or_save(self.cursor)

    def save_os(self):
        if self.reader.os:
            values = self.reader.os._asdict()
            self.os = pmtm_sql.OS(**values)
            self.os.fetch_or_save(self.cursor)

    def save_processor(self):
        if self.reader.processor:
            values = self.reader.processor._asdict()
            self.processor = pmtm_sql.Processor(**values)
            self.processor.fetch_or_save(self.cursor)

#======================================================================================================================

def main(args):
    parser = OptionParser('usage: %prog <file>')
    parser.add_option('-d', '--debug', action='store_true', dest='debug', default=False,
            help='Run in debug mode')
    parser.add_option('-x', '--dry-run', action='store_true', dest='dry_run', default=False,
            help='Check file without updating database')

    (options, args) = parser.parse_args(args)

    if len(args) != 2:
        parser.error('no file given')

    file_name = args[1]

    if options.debug:
        logging.basicConfig(level=logging.DEBUG, format='%(levelname)s:%(name)s> %(message)s')

    reader = pmtm_reader.Reader()

    with open(file_name) as file:
        reader.parse(file)

    with pmtm_sql.Connection() as conn:
        try:
            store = PmtmSqlStore(conn, reader)
            store.save()
        except Exception, ex:
            logging.warn('Error: file "%s", "%s"' % (file_name, ex))
            conn.rollback()
            raise ex
        else:
            if options.dry_run:
                conn.rollback()
            else:
                conn.commit()

    return 0

#======================================================================================================================

if __name__ == '__main__':
    try:
        sys.exit(main(sys.argv))
    except Exception, ex:
        print ex
        sys.exit(-1)

#======================================================================================================================

