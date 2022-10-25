; ID: 1738
; Author: Grisu
; Date: 2006-06-22 13:40:52
; Title: MaxGUI: Adding the correct German hotkey text
; Description: Replace

1. 
Open the "win32menu.cpp" located inside the "mod\brl\win32gui.mod\win32gui" folder with a text editor.

2.
There search for the following line:

"if( modifier & 2 ) strcat( buf,"Ctrl+" );"

and replace it with:

"if( modifier & 2 ) strcat( buf,"Strg+" );"

3.
Rebuild all modules. 
The correct German text for Control (CTRL) which is Steuerung (STRG) is now displayed.
