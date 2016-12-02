import RPi.GPIO as GPIO
import bluetooth
import time

#prepares bluetooth and GPIO
server_sock=bluetooth.BluetoothSocket(bluetooth.RFCOMM)
GPIO.setmode(GPIO.BCM)

#defines which GPIO pins the LEDs are connected to
led1 = 4;
led2 = 5;
led3 = 6;
led1_state = False;
led2_state = False;
led3_state = False;

def setup():
    print "setup started"
    #sets up selected GPIO pins as output
    GPIO.setup(led1, GPIO.OUT)
    GPIO.setup(led2, GPIO.OUT)
    GPIO.setup(led3, GPIO.OUT)

    #opens port 1 on Bluetooth for listening
    port = 1
    server_sock.bind(("",port))
    server_sock.listen(1)
    print "setup finished"

def main():
    print "main started"
    setup()

    print "Listening"
    client_sock,address = server_sock.accept()
    print "Accepted connection from ",address

    for num in range(0,20):
        data = client_sock.recv(1024)
        print "received[%s]" % data
        if data == 1:
            led1_state = not led1_state
        elif data == 2:
            led2_state = not led2_state
        elif data == 3:
            led2_state = not led2_state
        else:
            led1_state = False
            led2_state = False
            led3_state = False
        GPIO.output(led1, led1_state)
        GPIO.output(led2, led2_state)
        GPIO.output(led3, led3_state)

    client_sock.close()
    server_sock.close()

if __name__ == '__main__':
    print "if main... thing"
    main()

# GPIO.output(led1, 1)
#
# GPIO.output(led1, 0)

print "Gonna clean up now"
GPIO.cleanup()
