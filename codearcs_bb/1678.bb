; ID: 1678
; Author: Alaric
; Date: 2006-04-17 18:55:05
; Title: Gprint/Ginput/locate
; Description: graphical input/print commands to replace B2D commands

;Note: other than the globals (and the functions), the rest of this program is just an example.  

Graphics 800,600,16,2 
SetBuffer(BackBuffer())
Global printx,printy
Color 255,0,0
Line 0,0,GraphicsHeight(),GraphicsWidth()

Strng$=ginput$("Hello?  ")
Color 0,255,0
gprint "Hi!"
Color 0,0,255
locate(100,100)
Color 255,255,255
gprint(strng)
locate 0,0

For loop = 1 To 100
	Color Rand(255),Rand(255),Rand(255)
	gprint("Can you see me? "+loop)
	Delay(50)
Next

WaitKey()

Function gInput$(PrintSTR$)
	Local graphicsshiftbuffer=CreateImage(GraphicsWidth(),GraphicsHeight())
	Local InputSTR$ = ""
	Local NextKey = 0
	Local NextLet$
	
	If GraphicsHeight() <= printy;this entire "If" is just a way to shift the graphics buffer up when 
									;the bottom of the "graphics" is reached.  This is (pretty much) the 
									;same as the old input command. See gprint for entire commentation.   
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,FrontBuffer(),ImageBuffer(graphicsshiftbuffer)
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,-StringHeight(printstring),ImageBuffer(graphicsshiftbuffer),FrontBuffer()
		Viewport 0,GraphicsHeight()-StringHeight(printstring),GraphicsWidth(),GraphicsHeight()
		Cls
		Viewport 0,0,GraphicsWidth(),GraphicsHeight()
		Printy = GraphicsHeight()-StringHeight(printstring)
	End If
	Viewport printx,printy,GraphicsWidth()-printx,StringHeight(printstr$);I use this command to ensure 
																  ;that when I use the CLS command I don't
																  ;clear the ENTIRE screen, just the
																  ;Input line.
	Cls ;clear the line where the Input will be placed.  
	Text printx,printy, printstr$ + inputstr$ ;draw the string.  
	Flip;flip the viewport area
	Repeat
		nextlet$ = ""
		Nextkey = WaitKey() ;returns the ASCII value of whichever key was pressed.  
		If nextkey = 13 Then Exit ;check to see if return was hit.  
		If Nextkey <> 0 Then nextlet$ = Chr$(nextkey) ;If the key was actually pressed then make nextlet
													  ;equal to the letter signified by the ASCII value
													  ;inside of nextkey.  
		If NextLet$ <> "" And nextkey <> 8			  ;check to see if nextlet is a letter or 
			InputSTR$ = InputSTR$ + NextLet$;add nextlet to the output
		ElseIf nextkey = 8;check to see if delete was pressed.  
			InputSTR$ = Left$(InputSTR$,Len(InputSTR$) - 1) ;remove a letter from inputstr
		End If
		Cls ;clear the input line
		Text printx,printy, printstr$ + inputstr$;draw the text to the screen
		Flip ;flip the viewport buffer
	Forever ;loop
	Viewport 0,0,GraphicsWidth(),GraphicsHeight() ;return the viewport to normal.  
	printy=printy+15 ;increase the printy variable so that the next "gprint" or "ginput" command will
					 ;not be right on top of this one.  
	Return inputstr ;return whatever the user typed in.  
End Function

Function gPrint(printstring$)
	Local graphicsshiftbuffer=CreateImage(GraphicsWidth(),GraphicsHeight())
	If GraphicsHeight() <= printy+StringHeight(printstring);this entire "If" is just a way to shift the graphics buffer up when the
								;bottom of the "graphics" is reached.  This is (pretty much) the same
								;as the old input command.  
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,FrontBuffer(),ImageBuffer(graphicsshiftbuffer)
		;this line copies the entire frontbuffer to the image graphicsshiftbuffer.  
		
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,-StringHeight(printstring),ImageBuffer(graphicsshiftbuffer),FrontBuffer()
		;this line copies the image graphicsshiftbuffer back to the frontbuffer, but down one
		;line of text.  
		
		Viewport 0,GraphicsHeight()-StringHeight(printstring),GraphicsWidth(),GraphicsHeight()
		;this command resets the origin to a different area and allows me to CLS without clearing
		;the entire screen.  
		
		Cls
		;clear the area specified by the Viewport command.  
		Flip
		
		Viewport 0,0,GraphicsWidth(),GraphicsHeight()
		;reset the viewport to the entire screen.  

		Text printx,printy, printstring$
		;draw the text to the screen.  
		printy=printy-StringHeight(printstring)
	Else
		Text printx,printy, printstring	
		printy = printy + StringHeight(printstring)
		;since the entire screen (including the above text) has been moved up, there is no need
		;to move the next line of text down.  
	End If
	Flip
End Function

Function locate(NewPrintX,NewPrintY)
	printx = newprintx
	printy = newprinty
End Function
