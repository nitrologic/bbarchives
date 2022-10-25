; ID: 445
; Author: Chroma
; Date: 2002-10-01 07:13:18
; Title: JoyU() throttle 0-1 converter
; Description: This little formula takes the -1 to 1 output of the JoyU() command and converts it to a more desirable 0 to 1.

baseThrust = (JoyU()-1) * -0.5
