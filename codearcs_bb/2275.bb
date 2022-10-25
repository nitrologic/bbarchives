; ID: 2275
; Author: _33
; Date: 2008-06-17 21:41:29
; Title: Sounding the PC Speaker
; Description: This little program is an example to sound some notes on the PC speaker

api_Beep(Note2Frequency(59),100)
api_Beep(Note2Frequency(67),100)
api_Beep(Note2Frequency(71),100)
api_Beep(Note2Frequency(66),100)
api_Beep(Note2Frequency(61),100)
api_Beep(Note2Frequency(68),100)
api_Beep(Note2Frequency(73),100)
api_Beep(Note2Frequency(68),100)
api_Beep(Note2Frequency(63),100)
api_Beep(Note2Frequency(70),100)
api_Beep(Note2Frequency(75),100)
api_Beep(Note2Frequency(63),500)
End

Function Note2Frequency#(note#)
   Return (440.0 * 2.0^((note - 69.0) / 12.0))
End Function
