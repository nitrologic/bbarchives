; ID: 2892
; Author: matibee
; Date: 2011-10-04 12:34:55
; Title: Bitmaps in DefData statements
; Description: A simple method for including bitmaps in text-only listings.

SuperStrict
Import MaxGui.Drivers

Global window:TGadget = CreateWindow:TGadget("Blitzmax image to data utility.  Drag file below...",60,60,320,320,Null,WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS|WINDOW_ACCEPTFILES)
Global te:TGadget = CreateTextArea:TGadget( 10, 10, 300, 300, window:TGadget )
SetGadgetLayout( te, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED )

Repeat
	WaitEvent()
	Select EventID()
		Case EVENT_WINDOWACCEPT
			Local file$ = EventExtra().tostring()
			Print file$
			Local image:TImage = LoadImage(file)
			If image <> Null
				Local txt$ = StripDir( file$ )
				txt$ = Replace( txt$, ".", "_" )
				txt$ = Replace( txt$, " ", "_" )
				txt$ = "#" + txt$ + "~n"
				txt$ :+ "DefData " + ImageWidth( image ) + ", " + ImageHeight( image ) + "~n"
				Local tp:TPixmap = LockImage( image )
				For Local y:Int = 0 To ImageHeight( image ) - 1
					Local rowstring$ = "DefData ~q"
					For Local x:Int = 0 To ImageWidth( image ) - 1
						rowstring :+ Hex$( ReadPixel( tp, x, y ) )
					Next 
					txt$ :+ rowstring + "~q~n"										
				Next 
				UnlockImage( image )
				te.SetText( txt$ )
			Else 
				Notify( "Invalid image file!" )
			End If 				
		Case EVENT_APPTERMINATE
			End
		Case EVENT_WINDOWCLOSE
			Select EventSource()
				Case window End 
			End Select
		End Select
Forever
