#!/usr/bin/env python

import unittest
import logging
import time

import pmtm_reader

class ReadTests(unittest.TestCase):

    #------------------------------------------------------------------------------------------------------------------

    def setUp(self):
        # Set level to logging.DEBUG to get more output.
        logging.basicConfig(level=logging.WARN, format='%(levelname)s:%(name)s> %(message)s')

    #------------------------------------------------------------------------------------------------------------------

    def test_read_0_2_6_file(self):
        reader = pmtm_reader.Reader()

        with open('PMTM-Version-0.2.6.csv') as file:
            reader.parse(file)

        self.assertEqual(reader.version, (0, 2, 6))

        self.assertEqual(reader.application, ('Chimaera', 'v1.6.1'))

        datetime = time.mktime(time.strptime('22-04-2010 20:30', '%d-%m-%Y %H:%M'))
        self.assertEqual(reader.datetime, datetime)

        self.assertEqual(len(reader.parameters), 35)
        
        param_names = sorted(set(p.name for p in reader.parameters))
        self.assertEqual(param_names, ['cells per tile', 'meshx', 'meshy', 'meshz'])

        self.assertEqual(len(reader.timers), 33)
        
        timer_names = sorted(set(t.name for t in reader.timers))
        self.assertEqual(timer_names, ['W Tile Calculation Time'])

        self.assertEqual(len(reader.overheads), 2)

        overhead_names = sorted(set(o.name for o in reader.overheads))
        self.assertEqual(overhead_names, ['pause-continue', 'start-stop'])

        self.assertEqual(reader.run_id, None)
        self.assertEqual(reader.compiler, None)
        self.assertEqual(reader.machine, None)
        self.assertEqual(reader.mpi, None)
        self.assertEqual(reader.os, None)
        self.assertEqual(reader.processor, None)
        self.assertEqual(reader.flags, [])
        self.assertEqual(reader.environ, {})

    #------------------------------------------------------------------------------------------------------------------

    def test_read_1_0_0_file(self):
        reader = pmtm_reader.Reader()

        with open('PMTM-Version-1.0.0.csv') as file:
            reader.parse(file)

        self.assertEqual(reader.version, (1, 0, 0))

        self.assertEqual(reader.application, ('DL_Poly', 'v2.17.2'))

        datetime = time.mktime(time.strptime('23-03-2011 18:03', '%d-%m-%Y %H:%M'))
        self.assertEqual(reader.datetime, datetime)

        self.assertEqual(reader.run_id, '20110323-180317-0000020189')

        self.assertEqual(len(reader.parameters), 12)
        
        param_names = sorted(set(p.name for p in reader.parameters))
        self.assertEqual(param_names, ['eps', 'fac', 'tol'])

        self.assertEqual(reader.nprocs, '4')

        self.assertEqual(len(reader.timers), 11)
        
        timer_names = sorted(set(t.name for t in reader.timers))
        self.assertEqual(timer_names, ['Application Time', 'Ewald 1 Time', 'Inter Angle Three-Body-Force Time'])

        self.assertEqual(len(reader.overheads), 2)

        overhead_names = sorted(set(o.name for o in reader.overheads))
        self.assertEqual(overhead_names, ['pause-continue', 'start-stop'])

        self.assertEqual(reader.compiler, None)
        self.assertEqual(reader.machine, None)
        self.assertEqual(reader.mpi, None)
        self.assertEqual(reader.os, None)
        self.assertEqual(reader.processor, None)
        self.assertEqual(reader.flags, [])
        self.assertEqual(reader.environ, {})

    #------------------------------------------------------------------------------------------------------------------

    def test_read_2_0_0_file(self):
        reader = pmtm_reader.Reader()

        with open('PMTM-Version-2.0.0.csv') as file:
            reader.parse(file)

        self.assertEqual(reader.version, (2, 0, 0))

        self.assertEqual(reader.application, ('CHIM', ''))

        datetime = time.mktime(time.strptime('27-05-2011 17:28', '%d-%m-%Y %H:%M'))
        self.assertEqual(reader.datetime, datetime)

        self.assertEqual(reader.run_id, '20110527-172810-0000016840')

        self.assertEqual(len(reader.parameters), 4)
        
        param_names = sorted(set(p.name for p in reader.parameters))
        self.assertEqual(param_names, ['max SN', 'meshx', 'meshy', 'min SN'])

        self.assertEqual(reader.nprocs, '1')

        self.assertEqual(len(reader.timers), 12)
        
        timer_names = sorted(set(t.name for t in reader.timers))
        self.assertEqual(timer_names, ['PMTM_CHIMMAIN', 'PMTM_CHIM_BVALUE', 'PMTM_CHIM_WALLCLOCK'])

        self.assertEqual(len(reader.overheads), 2)

        overhead_names = sorted(set(o.name for o in reader.overheads))
        self.assertEqual(overhead_names, ['pause-continue', 'start-stop'])

        self.assertEqual(reader.compiler, ('Intel', 'Intel', '11.1'))
        self.assertEqual(reader.machine, None)
        self.assertEqual(reader.mpi, ('Intel', 'IntelMPI', '4.0.3'))
        self.assertEqual(reader.os, ('Red_Hat', 'RHEL', '4.1.2-46', 'Linux_2.6.18-128.1.6.el5.Bull.6'))
        self.assertEqual(reader.processor, None)
        self.assertEqual(reader.flags, [])
        self.assertEqual(reader.environ, {})

    #------------------------------------------------------------------------------------------------------------------

    def test_read_2_1_1_file(self):
        reader = pmtm_reader.Reader()

        with open('PMTM-Version-2.1.1.csv') as file:
            reader.parse(file)

        self.assertEqual(reader.version, (2, 1, 1))

        self.assertEqual(reader.application, ('DL_Poly', 'v2.17.2'))

        datetime = time.mktime(time.strptime('16-11-2011 18:27', '%d-%m-%Y %H:%M'))
        self.assertEqual(reader.datetime, datetime)

        self.assertEqual(reader.run_id, '20111116-182755-0000012999')

        self.assertEqual(len(reader.parameters), 8)
        
        param_names = sorted(set(p.name for p in reader.parameters))
        self.assertEqual(param_names, ['eps', 'fac'])

        self.assertEqual(reader.nprocs, '4')

        self.assertEqual(len(reader.timers), 14)
        
        timer_names = sorted(set(t.name for t in reader.timers))
        self.assertEqual(timer_names, ['Ewald 1 Time', 'Inter Angle Three-Body-Force Time'])

        self.assertEqual(len(reader.overheads), 2)

        overhead_names = sorted(set(o.name for o in reader.overheads))
        self.assertEqual(overhead_names, ['pause-continue', 'start-stop'])

        self.assertEqual(reader.compiler, ('Intel', 'Intel', '12.1'))
        self.assertEqual(reader.machine, (None, 'WILLOW'))
        self.assertEqual(reader.mpi, ('Intel', 'IntelMPI', ''))
        self.assertEqual(reader.os, ('Red_Hat', 'RHEL', '4.1.2-46', 'Linux_2.6.18-128.1.6.el5.Bull.6'))
        self.assertEqual(reader.processor, None)
        self.assertEqual(reader.flags, ['-heap-arrays 64', '-xHOST', '-O3', ''])
        self.assertEqual(reader.environ, {})

    #------------------------------------------------------------------------------------------------------------------

    def test_read_2_1_1_file_with_infinities(self):
        reader = pmtm_reader.Reader()

        with open('PMTM-Version-2.1.1-inf.csv') as file:
            reader.parse(file)

        self.assertEqual(reader.version, (2, 1, 1))

        self.assertEqual(reader.application, ('DL_Poly', 'v2.17.2'))

        datetime = time.mktime(time.strptime('16-11-2011 18:27', '%d-%m-%Y %H:%M'))
        self.assertEqual(reader.datetime, datetime)

        self.assertEqual(reader.run_id, '20111116-182755-0000012999')

        self.assertEqual(len(reader.parameters), 8)
        
        param_names = sorted(set(p.name for p in reader.parameters))
        self.assertEqual(param_names, ['eps', 'fac'])

        self.assertEqual(reader.nprocs, '4')

        self.assertEqual(len(reader.timers), 14)
        
        timer_names = sorted(set(t.name for t in reader.timers))
        self.assertEqual(timer_names, ['Ewald 1 Time', 'Inter Angle Three-Body-Force Time'])

        self.assertEqual(len(reader.overheads), 2)

        overhead_names = sorted(set(o.name for o in reader.overheads))
        self.assertEqual(overhead_names, ['pause-continue', 'start-stop'])

        self.assertEqual(reader.compiler, ('Intel', 'Intel', '12.1'))
        self.assertEqual(reader.machine, (None, 'WILLOW'))
        self.assertEqual(reader.mpi, ('Intel', 'IntelMPI', ''))
        self.assertEqual(reader.os, ('Red_Hat', 'RHEL', '4.1.2-46', 'Linux_2.6.18-128.1.6.el5.Bull.6'))
        self.assertEqual(reader.processor, None)
        self.assertEqual(reader.flags, ['-heap-arrays 64', '-xHOST', '-O3', ''])
        self.assertEqual(reader.environ, {})

    #------------------------------------------------------------------------------------------------------------------

if __name__ == '__main__':
    unittest.main()
