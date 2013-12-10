#!/usr/bin/python
#coding=utf-8

import serial,time
print ( time.strftime("%Y-%m-%d\t") )
print 'Initial serial port'

ser = serial.Serial()
ser.port = "/dev/ttyACM0"
ser.baudrate = 9600
ser.bytesize = serial.EIGHTBITS 
ser.parity = serial.PARITY_NONE
ser.stopbits = serial.STOPBITS_ONE
#ser.timeout = 0

try: 
    ser.open()
except Exception , e:
    print "error open serial port:" + str(e)
    exit()

if ser.isOpen():

    try:
        ser.flushInput()
        ser.flushOutput() 
    
        numOfLines = 0
        while True:
            #sep = int( time.strftime("%S") % 10 )
            #if sep == 0 :
                #ser.write("hello,PC write time is : " + time.strftime("%Y-%m-%d %X \n") )
             #   print("hello,PC write time is : " + time.strftime("%Y-%m-%d %X \n") )
            response = ser.readline()
            print( str(numOfLines) + ": read data:" + response )
            numOfLines += 1
            '''
            if( numOfLines > 10 ):
                break
            '''
        ser.close()
    except Exception, e1:
        print "error communicating ... " +  str(e1)

else:
    print"can: not open serial port"
    
    
                  
