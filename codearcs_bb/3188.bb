; ID: 3188
; Author: Zethrax
; Date: 2015-02-13 19:31:28
; Title: Preference library
; Description: This lib allows you to set, get, save, and load configuration preferences.

; === Preferences ===

; This lib allows you to set, get, save, and load configuration preferences.


; - Interface for use with settings and preferences menus -

; Note that normally the preference values will be stored in global variables of the data type suitable for whatever the value is. Those globals can be used when populating the GUI settings and preferences menus, or you can use the 'GetPreference' function below. Any changes to the settings and preferences should involve updating the global variable that their live value is stored in and should also include updating the preference via 'SetPreference' so that the preferences file is updated.

; GetPreference$( name$, value$ = "" ) ; This function returns a string which represents the preference value with the specified preference 'name$'. The 'value$' parameter is a default value that will be returned if no existing preference with that name exists. See the function declaration for more info.

; SetPreference( name$, value$ ) ; This function sets the preference with the specified preference 'name$' to the specified 'value$'. See the function declaration for more info.

; G_preferences_changed - (global boolean integer) If this holds a True value then the preferences have been changed (via 'GetPreference' or 'SetPreference') and 'SavePreferenceEntries' should be called at the earliest opportunity to save the preferences to file. Note that 'SavePreferenceEntries' doesn't save anything unless 'G_preferences_changed' is set to True, so you can just blind call that function whenever you might need or want to save preferences. Both 'GetPreference' and 'SetPreference' will set this global to True if they make any changes to the preference entries in the 'T_preference' type list, so you usually won't need to explicitely set this global.


; - Functions for use with your program -

; LoadPreferenceEntries()
	; Loads the preferences from a file.
	; Run this at the start of your program before you need to use any of your preference galbals.
	; If the preference file is not found then  the 'GetPreference' function will end up using its default preferences instead and the preferences will be flagged to be saved.

; SavePreferenceEntries()
	; Saves the preferences to a file.
	; This function should be called before shutting down your program 'nicely'. Don't call it for a fatal error shutdown as it can call 'FatalError' itself (which could result in a loop).
	; This function can be called irregardless of whether preferences actually need saving. If the preferences don't need to be saved then the function will return without saving anything.


; NOTES:-

; There should be a folder named 'preferences' inside the folder where this code is run from (inside whichever folder your program is in), or inside whatever folder is specified via 'G_program_save_folderpath$'. If you want to use a different name than 'preferences' for the preferences folder then you can change it by setting a different folder name in the 'G_preferences_folder$' global. This should just be the folder name without any slash characters added.

; A global named 'G_program_save_folderpath$' should be supplied by your program to specify the base folderpath where all your transient data will get saved. The global should be declared and have its value assigned before 'LoadPreferenceEntries' is called (as that's where it is used).
; Note that the folderpath stored in 'G_program_save_folderpath$' must end with a slash character - unless it's an empty string.
; Since the operating system doesn't like data to be dynamically saved in some locations ('Program Files', etc) using this global allows you to specify a different save folder location.
; If you use a different identifier for this global in your program then you'll need to change it where it appears in this library.
; If your program isn't in a location where writes are disabled then you can just set this global to hold an empty string.
; If your program is in a location where writes are disabled then you'll need to specify a writable location that contains the 'preferences' folder. Normally you'll set this up using an installer which will save the folderpath to a file you can load at program start to set the value of 'G_program_save_folderpath$'.

; A function named 'Error( msg$ )' should be provided by your program to issue non-fatal error messages to the player. If you want to use a different identifier for this function in your program then you'll need to change it where it appears in this library.

; A function named 'FatalError( msg$ )' should be provided by your program to issue fatal error messages to the player and shutdown the program. If you want to use a different identifier for this function in your program then you'll need to change it where it appears in this library.

;==========


; === START: Preference globals ===

; > Map save and player profile globals should go here.

; - Graphics.
;Global G_prf_graphics_screen_mode; = C_GMODE_WINDOWED ; 1 = C_GMODE_FULLSCREEN, 2 = C_GMODE_WINDOWED, 3 = C_GMODE_WINDOWED_FULLSCREEN, C_GMODE_WINDOWED_MAXIMIZED
;Global G_prf_fullscreen_resolution_width; = 1280
;Global G_prf_fullscreen_resolution_height; = 720
;Global G_prf_windowed_resolution_width; = 800
;Global G_prf_windowed_resolution_height; = 600
;Global G_prf_field_of_view; = 64 ; Use: CameraZoom G_player_camera, GetCameraZoomFromAngle#( G_prf_field_of_view ) ; Set the player camera's field of view.
;
;; - Audio.
;Global G_prf_master_audio_volume#; = 1.0
;Global G_prf_gameplay_sound_volume#; = 1.0
;Global G_prf_background_music_volume#; = 1.0
;Global G_prf_background_music_muted; = False
;Global G_prf_background_music_randomize; = False
;
;; - Mouse controls.
;Global G_prf_flip_mouse_buttons; = False ; Flip left and right mouse buttons.
;Global G_prf_flip_mouselook_horizontal; = False ; Invert mouselook horizontal direction.
;Global G_prf_flip_mouselook_vertical; = False ; Invert mouselook vertical direction.
;Global G_prf_mouselook_sensitivity#; = 0.25 ; Mouselook sensitivity.
;Global G_prf_allow_mouselook_smoothing; = True ; Enable/disable mouselook smoothing.
;Global G_prf_mouselook_smoothing_level; = 3 ; Mouselook smoothing level.
;
;; - Key bindings.
;Global G_prf_player_move_right; = 32 ; Right. The 'D' key.
;Global G_prf_player_move_left; = 30 ; Left. The 'A' key.
;Global G_prf_player_move_forward; = 17 ; Forward. The 'W' key.
;Global G_prf_player_move_backward; = 31 ; Backward. The 'S' key.
;Global G_prf_player_jump; = 57 ; The 'Spacebar' key.
;Global G_prf_player_sprint; = 42 ; The 'Left-Shift' key.
;Global G_prf_take_screenshot; = 183 ; The 'Print Screen' key.

; === END: Preference globals ===


; The type list used to store preferences for the current profile.
Type T_preference
	Field name$
	Field value$
End Type


; -- The values in the two lines below can be customized.
Global G_preferences_folder$ = "preferences" ; The name of the preferences folder. This should just be the folder name without any slash characters added.
Global G_current_profile$ = "Player" ; The name of the current player profile. This is left over from the profile system, but is still necessary. Leave it here to support a future profile system.
;------
Global G_preferences_folderpath$ ; Holds the current filepath to the preferences file for the current profile.
Global G_preferences_changed ; A flag that indicates that the preferences have been changed and need to be saved. If a save is required then this will be done upon exiting the settings menu and also when the program ends.



Function ApplyNonRestartPreferences()
	; Applies any preferences that don't require a program restart.
	; The contents of this function need to be supplied by you to suit your individual program.

;	; Note: For a release version, first delete the profile and preference files from the "preferences" folder to force a new set of files to be created using the default values set in the 'GetPreference' function calls below.	
;	
;
;	; Get graphics preferences. (done)	
;	; (The graphics mode settings are applied by 'SetGraphics3D' once the graphics screen is ready to be created. Updating them requires a program restart so we can't do that here.)
;	; (The field of view setting will be applied when the player character is created during map loading. No need to apply it here.
;	;SetPlayerCameraFOV( G_prf_field_of_view, False )	
;
;	; Get and apply audio preferences.
;	; > These will also set the preference globals, but won't change the preference entries.
;	SetMasterAudioVolume( G_prf_master_audio_volume#, False )
;	SetGameplaySoundVolume( G_prf_gameplay_sound_volume#, False )
;	SetBackgroundMusicVolume( G_prf_background_music_volume#, False )
;	SetBackgroundMusicMuted( G_prf_background_music_muted, False )
;	SetBackgroundMusicRandomize( G_prf_background_music_randomize, False )
;
;	
;	; - Mouse controls.
;	; (These settings are initially applied in the 'player.bb' file.
;	SetFlipMouseButtons( G_prf_flip_mouse_buttons, False ) ; Flip left and right mouse buttons.
;	; Flip mouselook horizontal (This doesn't need to be explicitely applied as it is used dynamically.)
;	; Flip mouselook vertical (This doesn't need to be explicitely applied as it is used dynamically.)
;	; Mouselook sensitivity. (This doesn't need to be explicitely applied as it is used dynamically.)
;	; Enable/disable mouselook smoothing. (This doesn't need to be explicitely applied as it is used dynamically.)
;	SetMouselookSmoothingLevel( G_prf_mouselook_smoothing_level, False ) ; Mouselook smoothing level.

	; - Key bindings. (done)
	; (These don't need to be explicitely applied as they are used dynamically.)

	; If default preference values were used by 'GetPreference' then they need to be saved, so we call 'SavePreferenceEntries'.
	; If nothing needs to be saved then 'SavePreferenceEntries' won't do anything, so it's safe to call it blind.
	SavePreferenceEntries
End Function



Function AssignPreferencesToGlobals()
	; Assigns the preferences for the current profile.
	; The contents of this function need to be supplied by you to suit your individual program.
	; Normally this is where you'll have all your 'GetPreference' calls to copy the value of preference entries to to their respective preference globals.
	; This function is also called by 'SetCurrentPreferenceProfile' after switching to a new profile. It's important that all the code required to load, get, sanitize, and apply the preferences for the new profile be present here.	

;	; Note: For a release version, first delete the profile and preference files from the "preferences" folder to force a new set of files to be created using the default values set in the 'GetPreference' function calls below.
;	
;
;	; Graphics preferences.
;	; (These settings are applied by 'SetGraphics3D' once the graphics screen is ready to be created. Updating them requires a program restart.)
;	G_prf_graphics_screen_mode = GetPreference$( "graphics_screen_mode", "1" ) ; Full screen mode.
;	G_prf_fullscreen_resolution_width = GetPreference$( "fullscreen_resolution_width", "" ) ; Value should be "" for release version to force the value to be properly allocated for the user's system.
;	G_prf_fullscreen_resolution_height = GetPreference$( "fullscreen_resolution_height", "" ) ; Value should be "" for release version to force the value to be properly allocated for the user's system.
;	G_prf_windowed_resolution_width = GetPreference$( "windowed_resolution_width", "800" ) ; Value should be "800" for release version.
;	G_prf_windowed_resolution_height = GetPreference$( "windowed_resolution_height", "600" ) ; Value should be "600" for release version.
;	; (This setting is initially applied in the 'player.bb' file and can be updated via 'SetPlayerCameraFOV( False )'.)
;	G_prf_field_of_view = GetPreference$( "field_of_view", "64" ) ; Value should be "64" for release version.	
;
;	; Audio preferences.
;	G_prf_master_audio_volume# = GetPreference$( "master_audio_volume", "1.0" )
;	G_prf_gameplay_sound_volume# = GetPreference$( "gameplay_sound_volume", "0.5" )
;	G_prf_background_music_volume# = GetPreference$( "background_music_volume", "0.5" )
;	G_prf_background_music_muted = GetPreference$( "background_music_muted", "0" )
;	G_prf_background_music_randomize = GetPreference$( "background_music_randomize", "0" )
;	
;	; - Mouse control preferences.
;	; (These settings are initially applied in the 'player.bb' file.
;	G_prf_flip_mouse_buttons = GetPreference$( "flip_mouse_buttons", "0" ) ; Flip left and right mouse buttons. (This needs a function set up to apply it.)
;	G_prf_flip_mouselook_horizontal = GetPreference$( "flip_mouselook_horizontal", "0" ) ; Invert mouselook horizontal direction. (This needs a function set up to apply it.)
;	G_prf_flip_mouselook_vertical = GetPreference$( "flip_mouselook_vertical", "0" ) ; Invert mouselook vertical direction. (This needs a function set up to apply it.)
;	G_prf_mouselook_sensitivity# = GetPreference$( "mouselook_sensitivity", "0.25" ) ; Mouselook sensitivity. (This doesn't need to be explicitely applied as it is used dynamically.)
;	G_prf_allow_mouselook_smoothing = GetPreference$( "allow_mouselook_smoothing", "1" ) ; Enable/disable mouselook smoothing. (This doesn't need to be explicitely applied as it is used dynamically.)
;	G_prf_mouselook_smoothing_level = GetPreference$( "mouselook_smoothing_level", "3" ) ; Mouselook smoothing level. (This will get setup in 'CreatePlayer' but needs a function set up to apply settings changes.)
;
;	; - Key binding preferences.
;	G_prf_player_move_right = GetPreference$( "player_move_right", "32" ) ; Right. The 'D' key.
;	G_prf_player_move_left = GetPreference$( "player_move_left", "30" ) ; Left. The 'A' key.
;	G_prf_player_move_forward = GetPreference$( "player_move_forward", "17" ) ; Forward. The 'W' key.
;	G_prf_player_move_backward = GetPreference$( "player_move_backward", "31" ) ; Backward. The 'S' key.
;	G_prf_player_jump = GetPreference$( "player_jump", "57" ) ; The 'Spacebar' key.
;	G_prf_player_sprint = GetPreference$( "player_sprint", "42" ) ; The 'Left-Shift' key.
;	G_prf_take_screenshot = GetPreference$( "take_screenshot", "14" ) ; The 'Backspace' key.

	; If default preference values were used by 'GetPreference' then they need to be saved, so we call 'SavePreferenceEntries'.
	; If nothing needs to be saved then 'SavePreferenceEntries' won't do anything, so it's safe to call it blind.
	SavePreferenceEntries
End Function



Function GetPreference$( name$, value$ = "" )
; Finds and returns the preference value with the specified 'name$'.
; If a preference with the specified name is not found then the default 'value$' supplied as a parameter will be returned instead and a new element will be created on the preference list with the specified name and value. In this case the name and value strings will be trimmed of surrounding whitespace.
	
	For pref.T_preference = Each T_preference
		If name$ = pref\name$
			value$ = pref\value$
			found = True
			Exit
		EndIf
	Next
	If Not found
		; Create a new preference entry and set its name and value from the supplied parameters.
		pref.T_preference = New T_preference
		pref\name$ = Trim( name$ )
		pref\value$ = Trim( value$ )
		G_preferences_changed = True ; Flag that the preferences need to be saved to file.
	EndIf
	Return value$ ; Return either the found value or the default value.
End Function



Function SetPreference( name$, value$ )
; Finds and sets the preference value with the specified 'name$'.
; If a preference with the specified name is not found then a new element will be created on the preference list with the specified name and value.
; The preference name can contain any character other than an equals sign or an end-of-line character. It can also contain spaces, but any surrounding spaces or other whitespace will be trimmed.
; The preference value can contain any character other than an end-of-line character. It can also contain spaces, but any surrounding spaces or other whitespace will be trimmed.

	; Sanitize the strings.
	name$ = Trim( name$ ) : value$ = Trim( value$ )

	For pref.T_preference = Each T_preference
		If name$ = pref\name$
			pref\value$ = value$
			found = True
			Exit
		EndIf
	Next
	If Not found
		; Create a new preference entry and set its name and value from the supplied parameters.
		pref.T_preference = New T_preference
		pref\name$ = name$
		pref\value$ = value$
	EndIf
	G_preferences_changed = True ; Flag that the preferences need to be saved to file.
End Function



Function LoadPreferenceEntries()
	; Loads the preferences for the current profile.
	; Run this at the start of your program before you need to use any of your preference galbals.
	; If the preference file is not found then  the 'GetPreference' function will end up using its default preferences instead and the preferences will be flagged to be saved.	

	; Set the base path for the preferences folder.
	; Note that the value of 'G_program_save_folderpath$' must already have been set by this point.
	; Also note that 'G_program_save_folderpath$' must either be an empty string or a folderpath terminated with a backslash.
	G_preferences_folderpath$ = G_program_save_folderpath$ + G_preferences_folder$ + "\"
	
	Delete Each T_preference ; Clear the existing preferences. Allows new preferences to be loaded when switching profiles.
	file = ReadFile( G_preferences_folderpath$ + G_current_profile$ + ".ini" )
	If file
		While Not Eof( file )
			l$ = Trim( ReadLine( file ) )
			If l$ <> "" ; If the line is not blank...
				s = Instr( l$, "=" )
				If s
					pref.T_preference = New T_preference
					pref\name$ = Trim( Left( l$, s - 1 ) )
					pref\value$ = Trim( Right( l$, Len( l$ ) - s ) )
				EndIf
			EndIf
		Wend
		CloseFile file
	Else ; The preferences file doesn't exist, so flag that one needs to be created with default preferences.
		G_preferences_changed = True ; Flag that the default preferences that will be set when using 'GetPreference' will need to be saved to file.
	EndIf
End Function



Function SavePreferenceEntries()
	; Saves the preferences to a file.
	; This function should be called before shutting down your program 'nicely'. Don't call it for a fatal error shutdown as it can call 'FatalError' itself (which could result in a loop).
	; This function can be called irregardless of whether preferences actually need saving. If the preferences don't need to be saved then the function will return without saving anything.
	
	If G_preferences_changed = False Then Return ; Abort if the preferences don't need saving.
	
	file = WriteFile( G_preferences_folderpath$ + G_current_profile$ + ".ini" )
	If file
		For pref.T_preference = Each T_preference
			WriteLine file, pref\name$ + "=" + pref\value$
		Next
		CloseFile file
		G_preferences_changed = False ; Set the flag to false to indicate that the preferences no longer need saving.
	Else
		; If we've reached this point and can't create the file then it's probably time to pop an error message and exit.
		; If we keep going and this save location is dodgy then we may end up having more critical save problems later on or saving data in places we shouldn't.
		FatalError "Unable to save preferences. Check that the '" + G_preferences_folder$ + "' folder exists in the save folder at '" + G_program_save_folderpath$ + "' and that write permissions exist for that location."
	EndIf
End Function


; === EXAMPLE USAGE ===


; (This is just some code I pulled from my current project as an example of how I use this lib. Some of the comments included are notes to myself.)


;Include "preferences.bb" ; This assumes you have this lib saved to this file.
;LoadPreferenceEntries ; Load the preferences into the 'T_preference' preference entries type list.
;AssignPreferencesToGlobals ; Assign you preferences to your preference globals in this function. The contents of the function need to be supplied by you. I've left some of the (commented out) code from my current project in there as an example.
;
;
;; *** Do all your program start stuff here. Basically anything that can make use of the preference globals, but doesn't need to apply preferences to objects that don't yet exist should go here.
;
;
;; Apply any preference settings that don't require a program restart.
;; > Note that this needs to go here as many of the objects configured by this function won't exist before this point.
;ApplyNonRestartPreferences ; Apply any preference changes that don't require a program restart. This should go after you've created the objects that these preferences will be applied to. The contents of the function need to be supplied by you. I've left some of the (commented out) code from my current project in there as an example. Whether you stick all your preference application stuff into this function or do it in other parts of your program is up to you. 
;
;
;; *** The rest of your program goes here (menus, main loop, etc).
;
;
;;===========
;
;
;; Here's an example of one of my functions called in 'ApplyNonRestartPreferences'. It shows how I handle updating preferences from that function (where the pref global and entry have already been set) and from a settings menu change (where the pref global and entry need to be updated).
;
;
;Function SetMasterAudioVolume( volume#, update_prefs = True )
;	; Sets the master audio volume from the preference global.
;	; This affects both gameplay sounds and background music.
;	; In the event GUI sounds and other sounds are added then is should be extended to also affect them.
;	; update_prefs - True = Update the preference global and entry. False = Don't update.
;	; Note that if 'update_prefs' is true then this is being called from the settings menu, so both the preference global and entry will need to be updated. If false then this is being called from the 'ApplyNonRestartPreferences' function, so both the preference global and entry are already set and don't need to be updated. There are no other places where this will be called from.
;
;	
;	If update_prefs
;		G_prf_master_audio_volume# = volume#
;		SetPreference( "master_audio_volume", G_prf_master_audio_volume# ) ; This also sets 'G_preferences_changed = True' to flag that a preference save is required.
;	EndIf
;	
;	; Apply setting.
;	SetBackgroundMusicVolume( G_prf_background_music_volume#, False ) ; Sync the background music volume with the volume changes.
;	SyncGameplaySoundChannels ; Sync the gameplay sound volume with the volume changes. This will get called anyway on a return from the menu, but we'll leave this here for the moment.
;End Function
