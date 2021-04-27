# Table

BlueNao is an Android application used to connect to paired devices. 
It is extremely simple, it only sends basic information but that's all that is needed.

RPi_BT_GPIO allows the Raspberry Pi to act as a Bluetooth server, accepting any incomming connection from any previously paired Bluetooth device.
Depending on what is received it will then control the GPIO pins.

html_php_control is an example web-page (which needs trimming) and python script to control the GPIO pins of a Raspberry Pi through a web browser.

Arduino_table is the code used by the Arduino Micro inside of the table to control the buttons and lights.

TODO: 
Make a wiring diagram for what has been done so far.
Create communication between Raspberry Pi and Arduino that works regardless of initial communication (BT or HTML)
Prepare RPi for communication with additional Arduinos

2021 NOTE: This is several years old. The physical elements I was using to build this project have been lost in several moves so the project has been effectively abandoned unforetunately. One day I may pick it back up but that would result in a complete overhaul.
