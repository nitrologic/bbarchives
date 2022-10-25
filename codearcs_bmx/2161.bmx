; ID: 2161
; Author: xlsior
; Date: 2007-11-24 22:36:14
; Title: Take Screenshot
; Description: Function to take a screenshot at the press of a key

Strict
Local ScreenCount:Int=0		' Number of the current screenshot
Local MaxScreenShot:Int=100	' Maximum number of screenshots allowed

Graphics 640,480

' Main Loop
While Not KeyDown(Key_Escape)
	' Your normal main-loop stuff goes here
	' blah blah blah
	DrawText (MilliSecs(),Rand(0,1000),Rand(0,700))
	' Just draw some text to the screen so we have something to see on the screenshot
	Flip

	If KeyHit (key_space) Then
		' If you hit space, take a screenshot unless you already captured your max.
		If ScreenCount:Int<MaxScreenShot:Int Then
			Takescreenshot("screenshot",ScreenCount)
			' Tthe screenshots will be written to the current folder as: screenshot<number>.jpg
			screencount=screencount+1
		End If 
	End If 
Wend

Function takescreenshot(basefile:String,Count:Int)
	Local picture:TPixmap=GrabPixmap(0,0,GraphicsWidth(),GraphicsHeight())
	SavePixmapJPeg(picture,Basefile+Count:Int+".jpg")
End Function
