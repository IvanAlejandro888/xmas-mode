#!/usr/bin/env python3
# -*- Mode: Python; coding: utf-8; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- 
#
# main.py
# Copyright (C) 2017 Ivan Avalos <ivan.avalos.diaz@hotmail.com>
# 
# xmas-mode is free software: you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the
# Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# xmas-mode is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along
# with this program.  If not, see <http://www.gnu.org/licenses/>.

from gi.repository import Gtk, GdkPixbuf, Gdk
import os, sys, uuid

# MQTT Paho client
import paho.mqtt.client as mqtt
import paho.mqtt.publish as publish

BROKER_URL = "192.168.100.14"
PUBLISH_TOPIC = "xmas-mode"
QOS = 0

#Comment the first line and uncomment the second before installing
#or making the tarball (alternatively, use project variables)
#UI_FILE = "src/xmas_mode.ui"
UI_FILE = "/usr/local/share/xmas_mode/ui/xmas_mode.ui"

mqttc = mqtt.Client("xmas_"+str(uuid.uuid4()))

class GUI:
	def __init__(self):

		self.builder = Gtk.Builder()
		self.builder.add_from_file(UI_FILE)
		self.builder.connect_signals(self)

		window = self.builder.get_object('window')
		
		mqttc.on_connect = self.on_connect
		mqttc.on_message = self.on_message
		mqttc.connect(BROKER_URL, 1883, 60)

		window.show_all()

		print("__init__");

		mqttc.loop_start()

	def on_window_destroy(self, window):
		mqttc.loop_stop()
		Gtk.main_quit()

	def on_switch_state_set (self, switch, arg):
		if switch.get_active():
			publish.single("xmas-mode", "ON", hostname=BROKER_URL)
		else:
			publish.single("xmas-mode", "OFF", hostname=BROKER_URL)

	def on_connect(self, client, userdata, flags, rc):
		print("Connected with result code "+str(rc))
		client.subscribe("xmas-mode")

	def on_subscribe(self, client, userdata, mid, granted_qos):
		print("Subscribed: "+str(mid)+" "+str(granted_qos))

	def on_message(self, client, userdata, message):
		decoded = str(message.payload.decode("utf-8"))
		switch = self.builder.get_object('xmas-switch')

		if decoded[1] is 'N':
			switch.set_state(True)
		if decoded[1] is 'F':
			switch.set_state(False)


def main():
	app = GUI()
	Gtk.main()
		
if __name__ == "__main__":
	sys.exit(main())

