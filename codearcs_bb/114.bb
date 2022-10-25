; ID: 114
; Author: DJWoodgate
; Date: 2001-10-27 20:13:17
; Title: Detecting Doubleclicks
; Description: Detecting Mouse Doubleclicks

Doubleclicktime=250

Repeat

Doubleclick1=False
click1=False
Time=MilliSecs()
If MouseDown(1) Then
	mousedown1 = True
Else	
	If  mousedown1 Then ; if we get here mouse button has been released
		Clicktime1 = Time
		If Clicktime1-Lastclicktime1 <= Doubleclicktime Then
			Doubleclick1=True 
			Lastclicktime1=0 : Wait1time=0
		Else
			If  Wait1time=0 Then Wait1time = Clicktime1 + doubleclicktime
			Lastclicktime1 = Clicktime1
		EndIf
		mousedown1 = False
	EndIf 	
EndIf
If Wait1time > 0 And Time > Wait1time Then Click1 = True :  Wait1time=0


If Click1 Then click1count=click1count+1 : Print clicktime1+" singleclick "+click1count
If doubleclick1 Then double1count=double1count+1 : Print Clicktime1+" Doubleclick! "+double1count

Until KeyDown(1)

End
