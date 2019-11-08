#!/usr/bin/env python
# -*- coding:utf-8 -*-

import sys
import os
import requests

def write_stdout(s):
    sys.stdout.write(s)
    sys.stdout.flush()

def write_stderr(s):
    sys.stderr.write(s)
    sys.stderr.flush()

def alert(msg=None):
    if msg is None:
        return
    # alert
    write_stderr(msg)

def parseData(data):
    tmp = data.split('\n')
    pheaders = dict([ x.split(':') for x in tmp[0].split() ])
    pdata = None
    if len(tmp) > 1:
        pdata = tmp[1]
    return pheaders, pdata

def main():
    while True:
        write_stdout('READY\n')
        line = sys.stdin.readline()  # read header line from stdin
        write_stderr(line)
        headers = dict([ x.split(':') for x in line.split() ])
        data = sys.stdin.read(int(headers['len'])) # read the event payload

        if headers['eventname'] == 'PROCESS_STATE_EXITED' or\
           headers['eventname'] == 'PROCESS_STATE_FATAL' or\
           headers['eventname'] == 'PROCESS_STATE_STOPPED':
            pheaders, pdata = parseData(data)
            from_state = pheaders['from_state']
            process_name = pheaders['processname']
            if headers['eventname'] == 'PROCESS_STATE_EXITED' and\
                not int(pheaders['expected']):
                msg = '进程%s(PID: %s)异常退出，请检查进程状态.'\
                    % (process_name, pheaders['pid'])
                alert(msg=msg)
            if headers['eventname'] == 'PROCESS_STATE_FATAL':
                msg = '进程%s启动失败，请检查进程状态.'\
                    % (process_name)
                alert(msg=msg)
        elif headers['eventname'] == 'PROCESS_LOG_STDERR':
            pheaders, pdata = parseData(data)
            process_name = pheaders['processname']
            pid = pheaders['pid']
            msg = '进程%s(PID: %s)错误输出，请检查进程状态，错误输出信息: %s.' \
                % (process_name, pid, pdata)
            alert(msg=msg)
        #echo RESULT
        write_stdout('RESULT 2\nOK') # transition from READY to ACKNOWLEDGED

if __name__ == '__main__':
    main()
