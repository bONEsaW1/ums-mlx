!macro checkSystemOS
!macroend

!macro installPrivateJRE
  File "${PROJECT_BASEDIR}\src\main\external-resources\UMS.bat"
  File /oname=win32\service\wrapper.conf "${PROJECT_BASEDIR}\src\main\external-resources\windows-service-wrapper\wrapper-without-jre.conf"
!macroend

!macro uninstallPrivateJRE
  Delete /REBOOTOK "$INSTDIR\UMS.bat"
!macroend

!include setup.nsi
