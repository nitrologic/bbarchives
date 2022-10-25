; ID: 2888
; Author: BlitzSupport
; Date: 2011-09-09 14:38:44
; Title: Screen-centered text
; Description: Centres text on screen, split over multiple lines if required

Function ScreenCenteredText (t:String, splitter:String = "|")

	' Turn t$ into an array of strings...

	Local line:String [] = t.Split (splitter)
	
	Local lineheight:Int = TextHeight (t)
	Local totalheight:Int = lineheight * line.length
	
	For Local loop:Int = 0 Until line.length

		DrawText line [loop],	GraphicsWidth () / 2 - TextWidth (line [loop]) / 2,..					' X
								GraphicsHeight () / 2 + (loop * lineheight) - (totalheight / 2)	' Y
	Next
	
End Function

Graphics 640, 480

Local msg:String = "Hello, here is some screen-centered text,|which is conveniently split onto several|lines using the ~qpipe~q character,|though you can specify which character to use!"

Repeat

	Cls
	
	ScreenCenteredText msg
	
	Flip
	
Until KeyHit (KEY_ESCAPE)

End
