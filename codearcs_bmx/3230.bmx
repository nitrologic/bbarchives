; ID: 3230
; Author: BlitzSupport
; Date: 2015-11-11 22:17:52
; Title: Use VLC DLL to play video
; Description: Simple VLC-based video player

' MaxGUI/Win32-specific example!

' See description -- some setup required!

' Resources:

' https://wiki.videolan.org/LibVLC_Tutorial/
' https://www.videolan.org/developers/vlc/doc/doxygen/html/modules.html
' Search engine + VLC function names!

SuperStrict

Import maxgui.drivers

' LibVLC functions...

Global libvlc_new:Byte Ptr							(argc:Int, argv:Byte Ptr)			"win32"
Global libvlc_release:Byte Ptr						(instance:Byte Ptr)					"win32"	' Void return

Global libvlc_media_new_path:Byte Ptr				(instance:Byte Ptr, path:Byte Ptr)	"win32"
Global libvlc_media_release:Int						(media:Byte Ptr)					"win32"	' Void return

Global libvlc_media_player_new_from_media:Byte Ptr	(media:Byte Ptr)					"win32"
Global libvlc_media_player_set_hwnd:Int				(player:Byte Ptr, hwnd:Int)			"win32"	' Void return
Global libvlc_media_player_play:Int					(player:Byte Ptr)					"win32"
Global libvlc_media_player_stop:Int					(player:Byte Ptr)					"win32"	' Void return
Global libvlc_media_player_release:Int				(player:Byte Ptr)					"win32"	' Void return

Local file:String = RequestFile ("Select a media file...")

If Not file Then End

' Assign functions from DLL...

Global libvlc:Int = LoadLibraryA ("libvlc.dll")

If libvlc

	libvlc_new							= GetProcAddress (libvlc, "libvlc_new")
	libvlc_release						= GetProcAddress (libvlc, "libvlc_release")
	
	libvlc_media_new_path				= GetProcAddress (libvlc, "libvlc_media_new_path")
	libvlc_media_player_new_from_media	= GetProcAddress (libvlc, "libvlc_media_player_new_from_media")
	libvlc_media_release				= GetProcAddress (libvlc, "libvlc_media_release")
	
	libvlc_media_player_set_hwnd		= GetProcAddress (libvlc, "libvlc_media_player_set_hwnd")
	libvlc_media_player_play			= GetProcAddress (libvlc, "libvlc_media_player_play")
	libvlc_media_player_stop			= GetProcAddress (libvlc, "libvlc_media_player_stop")
	libvlc_media_player_release			= GetProcAddress (libvlc, "libvlc_media_player_release")
	
	Local instance:Byte Ptr
	Local media:Byte Ptr
	Local player:Byte Ptr
	
	' Create VLC instance...
	
	instance = libvlc_new (0, Null)
	
	If instance
	
		' Load media from filename...
		
		media = libvlc_media_new_path (instance, file.ToCString ())
		
		If media
		
			' Create VLC player...
			
			player = libvlc_media_player_new_from_media (media)
			
			If player
			
				' Blitz window...
			
				Local window:TGadget = CreateWindow ("Media player", 320, 200, 640, 480)
				
				If window
				
					' Inner client area of window...
					
					Local hwnd:Int = QueryGadget (window, QUERY_HWND_CLIENT)
					
					' Tell VLC to render into client area...
					
					libvlc_media_player_set_hwnd player, hwnd
					
					' Play!
					
					libvlc_media_player_play player
	
					Repeat
						
						Select WaitEvent ()
							
							Case EVENT_WINDOWCLOSE
								Exit

						End Select
						
					Forever
					
					' Stop and release VLC player...
					
					If player
						libvlc_media_player_stop	player
						libvlc_media_player_release	player
					EndIf
					
				EndIf
			
			EndIf
		
			' Release media...
			
			libvlc_media_release media

		EndIf
		
		' Release VLC instance...
		
		libvlc_release instance
		
	EndIf
	
EndIf
