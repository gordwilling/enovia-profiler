@echo off
set CLASSPATH=%CLASSPATH%;.\lib\activation.jar;.\lib\jaxb-api.jar;.\lib\jaxb-impl.jar;.\lib\jaxb-xjc.jar;.\lib\jsr173_1.0_api.jar;.\lib\highbar.jar
java -classpath "%CLASSPATH%" com.highbar.tools.mri.viewer.MRIReportViewer