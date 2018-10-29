#include <EEPROM.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <Wire.h>

// WiFi
const char* ssid = "Totalplay-667F";
const char* password = "667F8C66QgpDsydg";
const char* mqtt_server = "192.168.100.14";

WiFiClient espClient;
PubSubClient client(espClient);

#define LED 13

void setup_wifi() {

  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int len) {
  char state[len];
  Serial.print("XMas Mode from ");
  Serial.print(topic);
  Serial.print(" switched to ");
  for(int i=0;i<len;i++) {
    char received = (char) payload[i];
    state[i] = received;
    Serial.print(received);
  }
  Serial.println(".");

  Serial.print("(");
  Serial.print(state[1]);
  Serial.println(")");
  
  if(state[1] == 'N') {
    digitalWrite(LED, HIGH);
  } else if(state[1] == 'F') {
    digitalWrite(LED, LOW);
  }
  
}

void reconnect() {
  while(!client.connected()) {
    Serial.println("Reconnecting to MQTT...");
    if(client.connect("xmas-client")) {
      Serial.println("Connected!");
      client.subscribe("xmas-mode");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
    }
    delay(5000);
  }
}

void setup() {
  Serial.begin(9600);

  setup_wifi();
  
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  pinMode(LED, OUTPUT);
}

void loop() {
  if(!client.connected()) {
    reconnect();
  }

  client.loop();
}
