@echo off

echo Universal Media Server MLX
echo ---------------------
echo In case of troubles with UMS.exe, this shell will launch Universal Media Server in a more old fashioned way
echo You can try to reduce the Xmx parameter value if you keep getting "Cannot create Java virtual machine" errors...
echo If private JRE bundle in "..\jre" subfolder not found this script will use system-wide Java installation.
echo ------------------------------------------------
pause

SET JVM_ARGS=-Xmx1024M -Djava.net.preferIPv4Stack=true -Dsun.java2d.d3d=false -Dfile.encoding=UTF-8 -classpath update.jar;ums.jar net.pms.PMS

IF EXIST "%~dp0jre\bin\javaw.exe" (
   start "" "%~dp0jre\bin\javaw.exe" %JVM_ARGS%
) ELSE (
   start javaw %JVM_ARGS%
)
