## Process this file with automake to produce Makefile.in
## Created by Anjuta


uidir = $(pkgdatadir)/ui
ui_DATA = xmas_mode.ui


## The main script
bin_SCRIPTS = xmas_mode.py

## Directory where .class files will be installed
xmas_modedir = $(pythondir)/xmas_mode


xmas_mode_PYTHON = \
	xmas_mode.py


EXTRA_DIST = $(ui_DATA)


# Remove ui directory on uninstall
uninstall-local:

	-rm -r $(uidir)

	-rm -r $(pkgdatadir)

xmas_modeEXTRA_DIST = paho.mqtt.client 
