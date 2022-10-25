; ID: 3010
; Author: Zethrax
; Date: 2012-12-12 00:04:36
; Title: Disable Sticky Keys (Windows)
; Description: Disable and restore the sticky keys prompt that appears when you repeatedly press the shift key.

' Import this file into your program. Don't forget to remove the demo code at the bottom.

Strict

Import BRL.Blitz
Import BRL.FileSystem
Import BRL.Bank


Extern "Win32"
	Function SystemParametersInfoA:Int( uAction:Int, uParam:Int, lpvParam:Byte Ptr, fuWinIni:Int )
End Extern


Type TStickyKeys

	Global filepath:String = "stickykeys.dat"
	
	Global stickykeys_flags:Int
	
	
	Function Disable()
		Rem
		Turns off the Sticky Keys prompt and hotkeys.
		
		Sticky keys is an annoying feature of the Windows operating system. To find out why it's a problem, tap your
		shift key five or more times in a row. If you use that key in your game's controls then the player won't be happy.
		
		To disable Sticky Keys just run this function at the begining of your program (eg. TStickyKeys.Disable).
		
		The Windows API flags that control the Sticky Keys prompt and hotkeys are automatically restored again when your program ends.
	
		The TStickyKeys.Restore function will be called automatically by the OnEnd handler set in TStickyKeys.Disable.
		
		Notes:-
			The functions will save and delete a temporary file named "stickykeys.dat". That file is used to restore the state of the
			sticky keys on the users system in case the program crashes before the 'TStickyKeys.Restore' function can be run
			(this seems to be best practice in this case). If files can't be saved to the folder the program is in then the
			'filepath' global variable set above should be changed to save to a folder that can be saved to.
			
		You can find the Blitz3D (and probably BlitzPlus) version of this code at:-
			http://www.blitzbasic.com/codearcs/codearcs.php?code=3003
		
		Reference links:-
			http://msdn.microsoft.com/en-us/library/ee416808(v=vs.85).aspx
			http://msdn.microsoft.com/en-us/library/dd373652(v=vs.85).aspx
			http://msdn.microsoft.com/en-us/library/ms724947(VS.85).aspx
		End Rem
		
		Local SPI_GETSTICKYKEYS:Int = $0000003A
		Local SPI_SETSTICKYKEYS:Int = $0000003B
		Local SKF_HOTKEYACTIVE_MASK:Int = $00000004 ~ $ffffffff
		Local SKF_CONFIRMHOTKEY_MASK:Int = $00000008 ~ $ffffffff
		
		' Create a bank to store the STICKYKEYS structure.
		Local bank:TBank = CreateBank( 8 )
	
		' Set the first integer in the bank to the total size of the bank (8 bytes).
		PokeInt bank, 0, 8
	
		' Open the file that holds the STICKYKEYS flags.
		Local file:TStream = ReadFile( TStickyKeys.filepath )
		
		If file ' If the file exists then the stickykeys state wasn't properly	
		' restored last time - possibly due to a crash during the last session.
		
			' Grab the STICKYKEYS flags from the file so we can restore the state.
			PokeInt bank, 4, ReadInt( file )
			CloseFile file
			
		Else ' If the file doesn't exist then the stickykeys state was properly	
		' restored last time, so we get it via SystemParametersInfo.
			
			' - Get the STICKYKEYS structure.
			' First parameter is the value of the SPI_GETSTICKYKEYS Windows constant.
			' Second parameter is the size of the STICKYKEYS structure again (8 bytes).
			' Third parameter is the address of the STICKYKEYS structure (the bank pointer).
			' Fourth parameter is not used and should be a zero.
			SystemParametersInfoA( SPI_GETSTICKYKEYS, 8, BankBuf( bank ), 0 )
			
		EndIf
	
		' Save the value of the second INT in the STICKYKEYS structure to a file.
		' This allows the state to be restored in case a program crash happens before
		' the 'RestoreStickyKeys' function can be run.
		file = WriteFile( TStickyKeys.filepath )
		If file
			WriteInt file, PeekInt( bank, 4 )
			CloseFile file
		EndIf
	
		TStickyKeys.stickykeys_flags = PeekInt( bank, 4 )
	
		' Turn off the SKF_HOTKEYACTIVE and SKF_CONFIRMHOTKEY flags
		PokeInt bank, 4, ( TStickyKeys.stickykeys_flags & SKF_HOTKEYACTIVE_MASK ) & SKF_CONFIRMHOTKEY_MASK
	
		' Set the first integer in the bank to the total size of the bank (8 bytes).
		PokeInt bank, 0, 8
	
		' - Set the STICKYKEYS structure.
		' First parameter is the value of the SPI_SETSTICKYKEYS Windows constant.
		' Second parameter is the size of the STICKYKEYS structure again (8 bytes).
		' Third parameter is the address of the STICKYKEYS structure (the bank pointer).
		' Fourth parameter is not used and should be a zero.
		SystemParametersInfoA( SPI_SETSTICKYKEYS, 8, BankBuf( bank ), 0 )
	
		'FreeBank bank - Doesn't seem to be an option to free a bank, so I guess we just let the GC clean it up.
		
		OnEnd TStickyKeys.Restore ' Set the 'TStickyKeys.Restore' function to be called automatically when the program ends.
	End Function
	
	
	Function Restore()
		' Restores the original state of the sticky keys prompt and hotkeys.
		
		' This function will be called automatically by the OnEnd handler set in TStickyKeys.Disable.
	
		Local SPI_SETSTICKYKEYS:Int = $0000003B
		
		' Create a bank to store the STICKYKEYS structure.
		Local bank:TBank = CreateBank( 8 )
		
		' Set the first integer in the bank to the total size of the bank (8 bytes).
		PokeInt bank, 0, 8
		
		' Store the sticky keys flags in the second integer in the bank.
		PokeInt bank, 4, TStickyKeys.stickykeys_flags
		
		' - Set the STICKYKEYS structure.
		' First parameter is the value of the SPI_SETSTICKYKEYS Windows constant.
		' Second parameter is the size of the STICKYKEYS structure again (8 bytes).
		' Third parameter is the address of the STICKYKEYS structure (the bank pointer).
		' Fourth parameter is not used and should be a zero.
		SystemParametersInfoA( SPI_SETSTICKYKEYS, 8, BankBuf( bank ), 0 )
	
		'FreeBank bank - Doesn't seem to be an option to free a bank, so I guess we just let the GC clean it up.
	
		' Delete the temporary file used to store the stickykeys flags.
		If FileType( TStickyKeys.filepath ) = FILETYPE_FILE Then DeleteFile TStickyKeys.filepath
		
	End Function
	
End Type


' === DEMO CODE ===


TStickyKeys.Disable ' Disable the Sticky Keys prompt and functionality.

Graphics 800, 600

DrawText "Tap your shift key five or more times to test if the Sticky Keys prompt appears.", 10, 10

DrawText "If the Sticky Keys prompt doesn't appear then the code is working OK.", 10, 30

DrawText "If the Sticky Keys prompt appears then something's gone wrong with the code.", 10, 50

DrawText "Press the Escape key or click on the close button to close this window.", 10, 70

Flip

While ( Not KeyHit ( KEY_ESCAPE ) ) And ( Not AppTerminate() )
	Delay 100
Wend

End ' The OnEnd handler set in 'TStickyKeys.Disable' will now cause the user's settings to be restored.
