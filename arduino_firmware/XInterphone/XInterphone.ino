
#include <SoftwareSerial.h>

#include <Usb.h>
#include <adk.h>

USB Usb;
ADK adk(&Usb,"gmail.czzsunset", // Manufacturer Name
             "XInterphone", // Model Name
             "Android send message via Interphone", // Description (user-visible string)
             "1.0", // Version
             "http://empty_nowsfdas", // URL (web page to visit if no installed apps support the accessory)
             "123456789sfdsafasf"); // Serial Number (optional)
             
SoftwareSerial mySerial(6, 7); // RX, TX

void power_on_a1(){
    pinMode(3, OUTPUT);
    pinMode(2, OUTPUT);
    digitalWrite(3, HIGH);
    analogWrite( 2, LOW);
}

void setup()  
{
  
  power_on_a1();
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  Serial.println("Init Usb");
  if (Usb.Init() == -1) {
    Serial.print("\r\nOSCOKIRQ failed to assert");
    while(1); //halt
  }

  Serial.println("Begin software serial");

  mySerial.begin(9600);
  delay(100);
  int sent = mySerial.write("AT+DMOCONNECT\r\n");
  delay(100);
 while (mySerial.available())
    Serial.write(mySerial.read()); 
  
  
 mySerial.write("AT+DMOAUTOPOWCONTR=1\r\n");
 delay(100);
 while (mySerial.available())
    Serial.write(mySerial.read());  
 char msg[] = "AT+DMOMES=xI'mSB\r\n";
 msg[10]=5;
 mySerial.write(msg);
 delay(100);
 while (mySerial.available())
    Serial.write(mySerial.read()); 
  Serial.println("Goodbye!");
  
}

void android2interphone(){
    // Recieve  message from Android-device, send it to Arduino
    
    uint8_t msg[120] = "AT+DMOMES=";        // Message format: "AT+DMOMES=$XXXX"
    
    uint16_t len = 0 ;  //sizeof(msg);


    uint8_t rcode = adk.RcvData(&len, msg+11);     //  Received from android
    
    if(rcode && rcode != hrNAK)
      USBTRACE2("Data rcv rcode. :", rcode);
      
    if(len > 0) {
        Serial.print(F("\r\nRcv data from Android: "));


        msg[10]  = len;        
        
        msg[10+len+1 ] = '\r';
        msg[10+len+2] = '\n';
        msg[10+len+3] = '\0';
        
        Serial.println((char*)msg);
        mySerial.write( (char*)msg);
        
        while (mySerial.available()){
            Serial.write(mySerial.read());
        }
        
      
    }    

}


void interphone2android(){
    
    // Check whether received message from android
    uint8_t  msg[120];
    uint16_t idx = 0;
    while (mySerial.available()){
      uint8_t c = mySerial.read(); // Recv a byte from interphone. format: +DMOMES=$xxxx
      msg[idx++] = c;
      Serial.write(c);    
    }
    
    if(idx > 0){
      uint16_t len = (uint16_t)msg[8];
      adk.SndData(len+2, msg+9);
    }else{
      // Send invalid data, in case the android InputStream.read blocks
      msg[0] = 0xff;
      adk.SndData(1, msg);
    }    
}

void loop() // run over and over
{
  
  Usb.Task();
  if( adk.isReady()){
    android2interphone();
    interphone2android();    
  }
  

  delay(20);
  
 //  if (Serial.available())
 //  mySerial.write(Serial.read());
}

