#include <Adafruit_NeoPixel.h>
#include "SparkFun_Tlc5940.h"
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define PIN 6          //defines the NeoPixel strip to be controlled from pin 6
#define SO 2           //defines the Serial Output for the 74HC165 to be controlled from pin 2
#define SH_LD 8        //defines the SH/LD for the 74HC165 to be controlled from pin 3
#define CLK 4          //defines the CLK for the 74HC165 to be controlled from pin 4
#define NUM_LEDS 16    //defines the number of LEDs on the NeoPixel strip to be 16

#define BRIGHTNESS 50

Adafruit_NeoPixel strip = Adafruit_NeoPixel(NUM_LEDS, PIN, NEO_GRBW + NEO_KHZ800);

int gamma[] = {
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
    0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,
    1,  1,  1,  1,  1,  1,  1,  1,  1,  2,  2,  2,  2,  2,  2,  2,
    2,  3,  3,  3,  3,  3,  3,  3,  4,  4,  4,  4,  4,  5,  5,  5,
    5,  6,  6,  6,  6,  7,  7,  7,  7,  8,  8,  8,  9,  9,  9, 10,
   10, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 15, 15, 16, 16,
   17, 17, 18, 18, 19, 19, 20, 20, 21, 21, 22, 22, 23, 24, 24, 25,
   25, 26, 27, 27, 28, 29, 29, 30, 31, 32, 32, 33, 34, 35, 35, 36,
   37, 38, 39, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 50,
   51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 66, 67, 68,
   69, 70, 72, 73, 74, 75, 77, 78, 79, 81, 82, 83, 85, 86, 87, 89,
   90, 92, 93, 95, 96, 98, 99,101,102,104,105,107,109,110,112,114,
  115,117,119,120,122,124,126,127,129,131,133,135,137,138,140,142,
  144,146,148,150,152,154,156,158,160,162,164,167,169,171,173,175,
  177,180,182,184,186,189,191,193,196,198,200,203,205,208,210,213,
  215,218,220,223,225,228,231,233,236,239,241,244,247,249,252,255 };

void setup() {
  Serial.begin(115200);
  Tlc.init();
  // This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  // End of trinket special code
  strip.setBrightness(BRIGHTNESS);
  strip.begin();
  strip.show(); // Initialize all pixels to 'off'
  pinMode(SH_LD, OUTPUT);
  pinMode(CLK, OUTPUT);
  pinMode(SO, INPUT);
}

void loop() {
  int i, j, k;
  byte b;

  for(j=0; j<256; j++) {
    for(i=0; i<strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel((i+j) & 255)); //sends Wheel the byte value where bits in (i+j) and 255 match. EG 00110011 & 11111111 = 00110011. This only allows the last 8 bits to be read.
    }
    k = map(j, 0, 256, 0, 8);
    b = checkButtons();
    updateButtons(k, b);
    strip.show();
    delay(10);
    Serial.println(b);
    Serial.println(k);
  }
}

// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3,0);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3,0);
  }
  WheelPos -= 170;
  return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0,0);
}

uint8_t red(uint32_t c) {
  return (c >> 8); //shifts the bits received 8 bits to the right
}
uint8_t green(uint32_t c) {
  return (c >> 16);
}
uint8_t blue(uint32_t c) {
  return (c);
}

void updateButtons(int k, int b) {
  static int pattern = 0;
  int kArray0[] = {0, 1, 2, 3, 4, 5, 6, 7};
  int kArray1[] = {1, 6, 5, 4, 3, 2, 7, 0};
  int kArray2[] = {2, 5, 7, 3, 4, 6, 1, 0};
  int kArray3[] = {3, 5, 2, 1, 7, 0, 4, 6};
  int kArray4[] = {4, 2, 7, 3, 5, 0, 1, 6};
  int kArray5[] = {5, 4, 1, 3, 7, 2, 0, 6};
  int kArray6[] = {6, 1, 3, 7, 4, 2, 0, 5};
  int kArray7[] = {7, 2, 4, 1, 6, 5, 3, 0};

  if(( b!= pattern) && (b != 0)) {
    pattern = b;
  }

  switch (pattern) {
    case 2:
      Tlc.clear();
      Tlc.set(kArray1[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 4:
      Tlc.clear();
      Tlc.set(kArray2[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 8:
      Tlc.clear();
      Tlc.set(kArray3[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 16:
      Tlc.clear();
      Tlc.set(kArray4[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 32:
      Tlc.clear();
      Tlc.set(kArray5[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 64:
      Tlc.clear();
      Tlc.set(kArray6[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    case 128:
      Tlc.clear();
      Tlc.set(kArray7[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
    default:
      Tlc.clear();
      Tlc.set(kArray0[k], 4094); //sets the LEDs in the buttons to a PWM value between 0 and 4095
      Tlc.update();
      break;
  }
  Serial.print("The pattern is ");
  Serial.println(pattern);
}

byte checkButtons() {
  int buttonState = 0;
  byte buttonStates;
  int i;
  
  digitalWrite(CLK, LOW);
  digitalWrite(SH_LD, LOW);
  delay(1);
  digitalWrite(SH_LD, HIGH);
  
  for(i=0; i<8; i++) {
    buttonState = digitalRead(SO);
    digitalWrite(CLK, LOW);
    delay(1);
    digitalWrite(CLK, HIGH);
    delay(1);
    buttonStates = (buttonStates << 1)|buttonState;
  }
  
  return buttonStates;
}

