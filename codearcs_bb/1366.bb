; ID: 1366
; Author: jfk EO-11110
; Date: 2005-05-03 10:32:17
; Title: Prevent Windows Screensaver
; Description: A simple way to prevent windows screensavers from becoming active

; prevent windows screensaver

; the following code should be executed in the mainloop, or 
; may be in a function when the variable is global.

if millisecs()>prevent_screensaver
 prevent_screensaver=millisecs()+30000
 movemouse mousex(),mousey()
endif
