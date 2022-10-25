; ID: 1367
; Author: Jake
; Date: 2005-05-04 05:02:26
; Title: Merge .cfx Animations
; Description: Appends .cfx animations for identical character models.

Include "bless\bless.bb" ; Required

Global g_hwnd, g_window;
Global g_file1, g_file2, g_file3, g_merge, g_suggest;

; Initialize window and stuff
InitGui()
CreateTimer(60) ; To force message pump to keep reading events, even when out of focus

; For Drag-and-drop (see MSDN)
Bless_DragAcceptFiles(g_hwnd, True)

; Register the Windows events and windows we want to pay attention to
Bless_RegisterEvent(BLESS+7, WM_DROPFILES, g_hwnd, g_hwnd)

Repeat
	Select WaitEvent()
		Case $803 ; End
			Bless_UnregisterEvent(0) ; Highly recommended to unregister all BLESS events
			End
		Case $103
			If EventData() >= BLESS And EventData() <= BLESSMAX Then HandleBlessEvent(EventData())
			; else handle standard keychar event
		Case $401

			file1$ = GadgetText( g_file1 );
			file2$ = GadgetText( g_file2 );
			file3$ = GadgetText( g_file3 );

			exists1 = FileType( file1$ );
			exists2 = FileType( file2$ );
	
			If( EventSource() = g_merge )
			
				If( exists1 = 1 And exists2 = 1 )  

					If( file3$ = "" )
						file3$ = SuggestFile$( file1$, file2$ )				
						SetGadgetText( g_file3, file3$ );
					End If

					extn1$ = Left( FileExtn( file1$ ), 3 );
					extn2$ = Left( FileExtn( file2$ ), 3 );
					extn3$ = Left( FileExtn( file3$ ), 3 );

					If( extn1$ = "cfx" And extn2$ = "cfx" And extn3$ = "cfx" )  

						exists3 = FileType( file3$ );

						If( exists3 = 1 )
							If( Confirm( "Output file exists - overwrite: " + FileName( file1$ ) + " ?" ) = 1 )
								exists3 = 0;
								DeleteFile( file3$ )
							End If
						End If

						If( exists3 = 0 )
							If ( MergeFiles( file1$, file2$, file3$ ) = True )
								Notify( "Files merged successfully" );
							Else
								Notify( "Error merging files!", True );
							End If
						End If
						
					Else

						If( extn1$ <> "cfx" ) Then Notify( "Input 1 is not a .cfx file!", True );
						If( extn2$ <> "cfx" ) Then Notify( "Input 2 is not a .cfx file!", True );
						If( extn3$ <> "cfx" ) Then Notify( "Output is Not a .cfx file!", True );
						
					End If
	
				Else
	
					If( exists1 = 0 ) Then Notify( "Input 1: " + file1$ + " not found!", True );
					If( exists2 = 0 ) Then Notify( "Input 2: " + file2$ + " not found!", True );				
	
				End If
			End If

			If( EventSource() = g_suggest )

				If( exists1 = 1 And exists2 = 1 )
					SetGadgetText( g_file3, SuggestFile$( file1$, file2$ ) );
				Else
					Notify( "Please select valid input files", True );
				End If
			
			End If
			
	End Select

Forever

Function HandleBlessEvent(id)
	Bless_EventData(id, Bless_EventData)
	Select EventData()
		Case BLESS+7 ; WM_DROPFILES
			fname$=String(" ", 256)
			Bless_DragQueryFile(Bless_EventData\wparam, 0, fname, Len(fname))
			Bless_DragFinish(Bless_EventData\wparam)

			FlushMouse();
			y# = MouseY() - GadgetY( g_window );
			If( y < 60 )
				SetGadgetText( g_file1, fname );
			Else If( y > 90 )
				SetGadgetText( g_file3, fname );			
			Else
				SetGadgetText( g_file2, fname );
			End If
		Default

	End Select
End Function

Function InitGui()
	; window gadgets
	g_window 	= CreateWindow( "Animation Merger Utility",100, 200, 415, 140, 0, 35 )
	CreateLabel( "Input 1:", 10, 15, 45, 24, g_window );
	g_file1		= CreateTextField( 55, 10, 350, 24, g_window );
	CreateLabel( "Input 2:", 10, 45, 45, 24, g_window );
	g_file2		= CreateTextField( 55, 40, 350, 24, g_window );
	CreateLabel( "Output:", 10, 75, 45, 24, g_window );
	g_file3		= CreateTextField( 55, 70, 300, 24, g_window );
	g_merge 	= CreateButton( "Merge!", 55, 105, 350, 24, g_window );
	g_suggest 	= CreateButton( "Auto", 360, 70, 45, 24, g_window );	
	; main window handle
	g_hwnd = QueryObject(g_window ,1)
End Function


Function FileName$( file$ )
	name$ = TrimPath$( file$ )
	Return name$;
End Function


Function FileExtn$( file$ )
	extn$ = TrimPath$( file$ )
	ext = Instr( extn$, "." );
	If( ext > 0 )
		extn$ = Right( extn$, Len( extn$ ) - ext );
	Else
		extn$ = "";
	End If
	Return extn$;
End Function


Function FileRoot$( file$ )
	root$ = TrimPath$( file$ )
	ext = Instr( root$, "." );
	If( ext > 0 )
		root$ = Left( root$, ext - 1 );
	End If
	Return root$;
End Function


Function FilePath$( file$ )
	name$ = TrimPath$( file$ )
	path$ = Left( file$, Len( file$ ) - Len( name$ ));
	Return path$;
End Function


Function TrimPath$( file$ )
	length = Len( file$ );
	slashPos = Instr( file$, "\" );
	If( slashPos > 0 ) Then Return TrimPath( Right( file$, length - slashPos ) );
	Return file$;
End Function


Function MergeFiles( file1$, file2$, output$ )

	fin1 = ReadFile( file1$ );
	fin2 = ReadFile( file2$ );
	fout = WriteFile( output$ );

	If( fin1 = 0 Or fin2 = 0 Or fout = 0 ) Then Return False;
	
	frames1 = 0;
	While( frames1 = 0 )
		lin$ = ReadLine( fin1 );
		If( Instr( lin$, "SetTotalFrames" ) > 0 )
			start   = Instr( lin$, "(" ) + 1;
			finish  = Instr( lin$, ")" ) - 1;
			frames1 = Mid$( lin$, start, (finish - start) + 1 );
		Else
			WriteLine( fout, lin$ )
		End If
	Wend

	frames2 = 0;
	While( frames2 = 0 )
		lin$ = ReadLine( fin2 );
		If( Instr( lin$, "SetTotalFrames" ) > 0 )
			start   = Instr( lin$, "(" ) + 1;
			finish  = Instr( lin$, ")" ) - 1;
			frames2 = Mid$( lin$, start, (finish - start) + 1 );
		End If
	Wend	

	WriteLine( fout, "SetTotalFrames(" + (frames1+frames2) + ")" );

	While( Not Eof( fin1 ) )

		matrices = False
		While( matrices = False )
			lin$ = ReadLine( fin1 );
			WriteLine( fout, lin$ )
			If( Instr( lin$, "bone:SetZConstraints" ) > 0 )
				matrices = True;
			End If
		Wend
	
		matrices = False
		While( matrices = False )
			lin$ = ReadLine( fin2 );
			If( Instr( lin$, "bone:SetZConstraints" ) > 0 )
				matrices = True;
			End If
		Wend	

		matrices = True;
		While( matrices = True )
			lin$ = ReadLine( fin1 );
			If( Instr( lin$, "bone:SetId" ) > 0 )
				matrices = False;
			Else
				WriteLine( fout, lin$ )			
			End If
		Wend

		matrices = True;
		While( matrices = True )
			lin$ = ReadLine( fin2 );
			If( Instr( lin$, "bone:SetId" ) > 0 )
				matrices = False;
				WriteLine( fout, lin$ )
			Else
				If( Instr( lin$, "bone:SetKey" ) > 0 )
					start   = Instr( lin$, "(" ) + 1;
					finish  = Instr( lin$, "," ) - 1;
					key = Mid$( lin$, start, (finish - start) + 1 );
					WriteLine( fout, "bone:SetKey(" + (key+frames1) + Right$( lin$, Len(lin$) - finish ) );
				Else
					WriteLine( fout, lin$ )
				End If
			End If
		Wend

		lin$ = ReadLine( fin1 );
		WriteLine( fout, lin$ )

		lin$ = ReadLine( fin1 );
		WriteLine( fout, lin$ )			
		
	Wend

	CloseFile( fin1 );
	CloseFile( fin2 );
	CloseFile( fout );	
	
	Return True;

End Function

Function SuggestFile$( file1$, file2$ )
	path$  = FilePath( file1$ );
	root1$ = FileRoot( file1$ );
	root2$ = FileRoot( file2$ );
	common$ = Common$( root1$, root2$ );
	comlen  = Len( common$ );
	root1$ = Mid( root1$, comlen + 1, 5 );
	root2$ = Mid( root2$, comlen + 1, 5 );
	Return path$ + common$ + root1$ + "_" + root2$ + ".cfx";
End Function


Function Common$( file1$, file2$ )
	len1 = Len( file1$ );
	len2 = Len( file2$ );
	If( len1 > len2 )
		For c = 1 To len2
			If( Left( file1$, c ) <> Left( file2$, c ) ) Then Return Left( file1$, c - 1);			
		Next
		Return file2$
	Else
		For c = 1 To len1
			If( Left( file1$, c ) <> Left( file2$, c ) ) Then Return Left( file1$, c - 1);			
		Next
		Return file1$
	End If
End Function
