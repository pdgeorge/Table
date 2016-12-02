import RPi.GPIO as GPIO
import time
 
GPIO.setmode(GPIO.BCM)
led1 = 4;
led2 = 5;
led3 = 6;
GPIO.setup(led1, GPIO.OUT)
GPIO.setup(led2, GPIO.OUT)
GPIO.setup(led3, GPIO.OUT)
GPIO.output(led1, 1)
time.sleep(1)
GPIO.output(led2, 1)
time.sleep(1)
GPIO.output(led3, 1)
time.sleep(1)
GPIO.output(led1, 0)
time.sleep(1)
GPIO.output(led2, 0)
time.sleep(1)
GPIO.output(led3, 0)
time.sleep(1)
GPIO.cleanup()
