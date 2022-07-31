#include "ArduinoQueue.h"
#include "ESPDateTime.h"
#include <Firebase_ESP_Client.h>
#include <addons/RTDBHelper.h>

// 1. Define the WiFi credentials
#define WIFI_SSID  "POCO X3 Pro"          //"POCO X3 Pro", "Tamers", "D_L_AR-AEEV03J 9987"
#define WIFI_PASSWORD "123123123"                 //"123123123", "zbey_f_tezak", "123123123"

// 2. Define the RTDB URL and database secret
#define DATABASE_URL "breathingclassifier-default-rtdb.firebaseio.com/" //<databaseName>.firebaseio.com or <databaseName>.<region>.firebasedatabase.app
#define DATABASE_SECRET "t0Nhkx14jif44tjnd9u8LOOgfMyMQHYUe4qh3XA4"

// 3. Define the Firebase Data object
FirebaseData fbdo;

// 4. Define the FirebaseAuth data for authentication data and FirebaseConfig data for config data
FirebaseAuth auth;
FirebaseConfig config;

// 5. Define the System Configration
#define FSR 35
#define NewIDPath "/NewID"
#define LED_BUILT_IN  2

// 6. Define Global Variables
ArduinoQueue<int> ReadingsQueue(18000);
String patientIDPath = "/Patterns/";
int monthDays[12] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

void SetDateTime(long int TimeStamp, int* Year, int* Month, int* Day, int* Hour, int* Mint, int* Sec)
{
  // Setting the Data and Time from the global time stamp
  Serial.printf("Time Stamp now: %ld\n", TimeStamp);
  *(Sec) = TimeStamp % 60;
  TimeStamp /= 60;
  Serial.printf("Time Stamp now: %ld\n", TimeStamp);
  TimeStamp -= 60 * 4;
  *(Mint) = TimeStamp % 60;
  TimeStamp /= 60;
  Serial.printf("Time Stamp now: %ld\n", TimeStamp);
  *(Hour) = TimeStamp % 24;
  TimeStamp /= 24;
  TimeStamp -= 12;
  *Year = 1970 + TimeStamp / 365;
  TimeStamp %= 365;
  *Month = 0;

  //Check if the year is a leap year
  if (*Year % 4 == 0)
    monthDays[1] = 29;

  while (TimeStamp > 0)
  {
    Serial.printf("Time Stamp In lop: %ld\n", TimeStamp);
    TimeStamp -= monthDays[*Month];
    *Month = *Month + 1;
  }
  *Day = TimeStamp + monthDays[*Month - 1];
}

//Reading the FSR Value
void ReadSensor (void * par)
{
  // Clearing the Queue before Startiing
  while (ReadingsQueue.item_count())
  {
    Serial.println(ReadingsQueue.dequeue());
  }

  // Continous Reading the data
  while (1)
  {

    static float SumOfReadings = 0; // -> To Calculate the Average
    static float Aver = 0;        //-> Intitialize the average
    static int Count = 0;
    //Reading the change in voltage and calibrate it by deducting the average
    float Reading = analogRead(FSR) * (4000 / 4095.0);
    Reading -= Aver;
    SumOfReadings += Reading;

    //Check if the Queue is full, remove the first reading before adding a new reading
    if (ReadingsQueue.item_count() == 18000)
      ReadingsQueue.dequeue();

    ReadingsQueue.enqueue(Reading);

    //Calibrating the average every 10 Seconds
    if (Count == 100)
    {
      Aver += SumOfReadings / Count;
      SumOfReadings = 0;
      Count = 0;
    }
    Serial.print("The Sensors Reading is:");
    Serial.println(Reading);
    vTaskDelay(99 / portTICK_PERIOD_MS); //-> holds the function untill the next 100ms comes
  }
}

//Sending the Data to the database
void DataBaseSend (void * par)
{
  // Initializing Variables
  long int GlobalTimeStamp = 0;
  float reading = 0;
  String patientIDPathWithCounter;
  FirebaseJson json;
  String DayNumber = "/Day", HourNumber = "/Hour", MinNumber = "/Minute", SecNumber = "/Seconds";
  int Year = 0, Month = 0 , Day = 0, Hour = 0, Mint = 0, Sec = 0, QueCount = 0;

  //getting the current TiemStamp and set the Data and time Accordingly
  Serial.printf("Set timestamp... %s\n", Firebase.RTDB.setTimestamp(&fbdo, "/Global Timestamp") ? "ok" : fbdo.errorReason().c_str());
  Serial.print("TIMESTAMP (Seconds): ");
  Serial.println(fbdo.to<String>());
  Serial.println("Setting Time.......");
  SetDateTime(fbdo.to<int>(), &Year, &Month, &Day, &Hour, &Mint, &Sec);
  Serial.printf("Year NOW:   %d\n", Year);
  Serial.printf("Month NOW:   %d\n", Month);
  Serial.printf("Day NOW:   %d\n", Day);
  Serial.printf("Hour NOW:   %d\n", Hour);
  Serial.printf("Min NOW:   %d\n", Mint);
  Serial.printf("Sec NOW:   %d\n", Sec);

  while (1)
  {
    QueCount = ReadingsQueue.item_count();
    if (WiFi.status() != WL_CONNECTED)
    {
      WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
      Serial.print("Reconnecting to Wi-Fi......");
      while (WiFi.status() != WL_CONNECTED)
      {
        digitalWrite(LED_BUILT_IN, HIGH);
        delay(50);
        digitalWrite(LED_BUILT_IN, LOW);
        delay(50);
      }
    }

    if (QueCount >= 10)
    {
      for (int i = 0; i < 10; i++)
      {
        json.set(String(i), ReadingsQueue.dequeue());
      }
      patientIDPathWithCounter = patientIDPath + "/" + String(Year) + "/" + String(Month) + "/" + String(Day) + "/" + String(Hour) + "/" + String(Mint) + "/" + String(Sec);

      //modiy the Data and time each reading
      Sec++;
      if (Sec == 60)
      {
        Mint++;
        Sec = 0;
        if (Mint == 60)
        {
          Hour++;
          Mint = 0;
          if (Hour == 24)
          {
            Day++;
            Hour = 0;
            if (Day > monthDays[Month - 1])
            {
              Month++;
              Day = 1;
              if (Month > 12)
              {
                Year++;
                Month = 1;
                if (Year % 4 == 0)
                  monthDays[1] = 29;
                else
                  monthDays[1] = 28;

              }
            }
          }
        }
      }
      Serial.printf("Sending data... %s\n", Firebase.RTDB.setJSON(&fbdo, patientIDPathWithCounter, &json) ? fbdo.to<FirebaseJson>().raw() : fbdo.errorReason().c_str());
    }
    vTaskDelay(1 / portTICK_PERIOD_MS);
  }
}

void setup() {
  String ID;
  // put your setup code here, to run once:
  Serial.begin(115200);
  
  //Setting Pins Configration
  pinMode(FSR, INPUT);
  pinMode(LED_BUILT_IN, OUTPUT);
  delay(100);
  
  //Connecting to WIFI
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi......");
  while (WiFi.status() != WL_CONNECTED)
  {
    digitalWrite(LED_BUILT_IN, HIGH);
    delay(50);
    digitalWrite(LED_BUILT_IN, LOW);
    delay(50);
  }
  WiFi.setAutoConnect(1);
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  
  //Connecting to Firebase Database
  config.database_url = DATABASE_URL;
  config.signer.tokens.legacy_token = DATABASE_SECRET;
  Firebase.begin(&config, &auth);
  Serial.printf("Firebase Client v%s\n\n", FIREBASE_CLIENT_VERSION);
  Serial.println("Connected To the FireBase");

  //Setting the Patients ID to the next new ID
  Serial.printf("Patient ID: %s\n", Firebase.RTDB.getString(&fbdo, F(NewIDPath)) ? fbdo.to<const char *>() : fbdo.errorReason().c_str());
  ID = Firebase.RTDB.getString(&fbdo, F(NewIDPath)) ? fbdo.to<const char *>() : fbdo.errorReason().c_str();
  patientIDPath = patientIDPath + ID;
  Serial.printf("Setting New Patient ID: %s\n", Firebase.RTDB.setInt(&fbdo, F(NewIDPath), ID.toInt() + 1) ? "ok" : fbdo.errorReason().c_str());

  //Starting the 2 Main Tasks, and pinning each to a core
  xTaskCreatePinnedToCore(DataBaseSend, "Sending Data to the Data Base", 10000, NULL, 1, NULL, 0);
  xTaskCreatePinnedToCore(ReadSensor, "Reading From The Sensor", 1024, NULL, 1, NULL, 1);
  
  // Delete "setup and loop" task
  vTaskDelete(NULL);

}

void loop() {
  // put your main code here, to run repeatedly:

}
