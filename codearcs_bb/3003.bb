; ID: 3003
; Author: Zethrax
; Date: 2012-11-22 16:58:51
; Title: DisableStickyKeys and RestoreStickyKeys functions
; Description: These functions disable and restore the sticky keys prompt that occurs when you repeatedly press the shift key.

Function DisableStickyKeys()
	; Turns off the sticky keys prompt and hotkeys.	
	; Run this function at the begining of your program.

	; Returns the stickykeys flags. The returned integer value should be saved in a global
	; and provided as the parameter for the 'RestoreStickyKeys' function.

	; The 'RestoreStickyKeys' function must be run prior to program shutown to restore
	; the user's stickykeys settings. This is no way to run a railroad, but seems to be
	; best practice in this case. Welcome to the Windows API.
	
	; Reference links:-
	; http://msdn.microsoft.com/en-us/library/ee416808(v=vs.85).aspx
	; http://msdn.microsoft.com/en-us/library/dd373652(v=vs.85).aspx
	; http://msdn.microsoft.com/en-us/library/ms724947(VS.85).aspx
	;
	; Requirements:-
	; Grab the user32.dll decls from: http://www.blitzbasic.com/codearcs/codearcs.php?code=1179
	; Put them in your userlibs folder in a text file named: user32.decls
	
	Local SPI_GETSTICKYKEYS = $0000003A
	Local SPI_SETSTICKYKEYS = $0000003B
	Local SKF_HOTKEYACTIVE_MASK = $00000004 Xor $ffffffff
	Local SKF_CONFIRMHOTKEY_MASK = $00000008 Xor $ffffffff
	Local stickykeys_flags
	
	Local filepath$ = "stickykeys.dat"
	
	; Create a bank to store the STICKYKEYS structure.
	Local bank = CreateBank( 8 )

	; Set the first integer in the bank to the total size of the bank (8 bytes).
	PokeInt bank, 0, 8

	; Open the file that holds the STICKYKEYS flags.
	file = ReadFile( filepath$ )
	
	If file ; If the file exists then the stickykeys state wasn't properly	
	; restored last time - possibly due to a crash during the last session.
	
		; Grab the STICKYKEYS flags from the file so we can restore the state.
		PokeInt bank, 4, ReadInt( file )
		CloseFile file
		
	Else ; If the file doesn't exist then the stickykeys state was properly	
	; restored last time, so we get it via SystemParametersInfo.
		
		; - Get the STICKYKEYS structure.
		; First parameter is the value of the SPI_GETSTICKYKEYS Windows constant.
		; Second parameter is the size of the STICKYKEYS structure again (8 bytes).
		; Third parameter is the address of the STICKYKEYS structure (the bank handle).
		; Fourth parameter is not used and should be a zero.
		api_SystemParametersInfo( SPI_GETSTICKYKEYS, 8, bank, 0 )
		
	EndIf

	; Save the value of the second INT in the STICKYKEYS structure to a file.
	; This allows the state to be restored in case a program crash happens before
	; the 'RestoreStickyKeys' function can be run.
	file = WriteFile( filepath$ )
	If file
		WriteInt file, PeekInt( bank, 4 )
		CloseFile file
	EndIf

	stickykeys_flags = PeekInt( bank, 4 )

	; Turn off the SKF_HOTKEYACTIVE and SKF_CONFIRMHOTKEY flags
	PokeInt bank, 4, ( stickykeys_flags And SKF_HOTKEYACTIVE_MASK ) And SKF_CONFIRMHOTKEY_MASK

	; Set the first integer in the bank to the total size of the bank (8 bytes).
	PokeInt bank, 0, 8

	; - Set the STICKYKEYS structure.
	; First parameter is the value of the SPI_SETSTICKYKEYS Windows constant.
	; Second parameter is the size of the STICKYKEYS structure again (8 bytes).
	; Third parameter is the address of the STICKYKEYS structure (the bank handle).
	; Fourth parameter is not used and should be a zero.
	api_SystemParametersInfo( SPI_SETSTICKYKEYS, 8, bank, 0 )

	FreeBank bank
	
	; Return the flags so they can be stored in a global and uses with the 'RestoreStickyKeys' function.
	Return stickykeys_flags
End Function



Function RestoreStickyKeys( stickykeys_flags )
	; Restores the original state of the sticky keys prompt and hotkeys.
	
	; Run this function at the end of your program.
	
	; The 'stickykeys_flags' parameter should be the value returned by 'DisableStickyKeys'.

	Local SPI_SETSTICKYKEYS = $0000003B
	
	filepath$ = "stickykeys.dat"
	
	; Create a bank to store the STICKYKEYS structure.
	bank = CreateBank( 8 )
	
	; Set the first integer in the bank to the total size of the bank (8 bytes).
	PokeInt bank, 0, 8
	
	; Store the sticky keys flags in the second integer in the bank.
	PokeInt bank, 4, stickykeys_flags
	
	; - Set the STICKYKEYS structure.
	; First parameter is the value of the SPI_SETSTICKYKEYS Windows constant.
	; Second parameter is the size of the STICKYKEYS structure again (8 bytes).
	; Third parameter is the address of the STICKYKEYS structure (the bank handle).
	; Fourth parameter is not used and should be a zero.
	api_SystemParametersInfo( SPI_SETSTICKYKEYS, 8, bank, 0 )

	FreeBank bank

	; Delete the temporary file used to store the stickykeys flags.
	If FileType( filepath$ ) = 1 Then DeleteFile filepath$
End Function
