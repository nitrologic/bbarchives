; ID: 1935
; Author: Pantheon
; Date: 2007-02-27 01:55:56
; Title: Arbitary Code Excecution
; Description: Run machine code, stored on the heap

'/**
' * ARBITARY ( HEAP ) CODE EXECUTION
' *	
' *	  this code will store a set of machine instructions
' *   in the heap and then  excecute  them. im not  sure
' *   how  stable this  techinque would be for a  bigger
' *   program as the stack frame may be corrupted  after
' *   the shell code completes (i havent looked into it)
' *
' *   the  shell code was  written by 'xnull' and can be
' *   found  at  milw0rm.com  in the shellcode  section.
' *   once running the PC speaker will be set to beep at
' *   3585hz for 2 seconds
' *
' *   this will only run on WinXP Service Pack 2!
' *   change the commenting for service pack 1
' * 
' *   - Pantheon
' * 
' */

' this address will point to our shell code.
'
Global ShellCode:Byte Ptr

' point to memory of 35 bytes (on the heap) 
'
ShellCode = MemAlloc( 35 )

' inserts the shellcode into the array
'
ShellCode[ 00 ] = $55
ShellCode[ 01 ] = $89
ShellCode[ 02 ] = $E5
ShellCode[ 03 ] = $83
ShellCode[ 04 ] = $EC
ShellCode[ 05 ] = $18
ShellCode[ 06 ] = $C7
ShellCode[ 07 ] = $45
ShellCode[ 08 ] = $FC

ShellCode[ 09 ] = $53 ' Address for Service Pack 2
ShellCode[ 10 ] = $8A
ShellCode[ 11 ] = $83
ShellCode[ 12 ] = $7C

'ShellCode[ 09 ] = 10$' Address for Service Pack 1
'ShellCode[ 10 ] = C9$
'ShellCode[ 11 ] = EA$
'ShellCode[ 12 ] = 77$

ShellCode[ 13 ] = $C7
ShellCode[ 14 ] = $44
ShellCode[ 15 ] = $24
ShellCode[ 16 ] = $04
ShellCode[ 17 ] = $D0 ' Length $D003 = 2000 (2 seconds)
ShellCode[ 18 ] = $03 
ShellCode[ 19 ] = $00
ShellCode[ 20 ] = $00
ShellCode[ 21 ] = $C7
ShellCode[ 22 ] = $04
ShellCode[ 23 ] = $24
ShellCode[ 24 ] = $01 ' Frequency $010E = 3585
ShellCode[ 25 ] = $0E 
ShellCode[ 26 ] = $00
ShellCode[ 27 ] = $00
ShellCode[ 28 ] = $8B
ShellCode[ 29 ] = $45
ShellCode[ 30 ] = $FC
ShellCode[ 31 ] = $FF
ShellCode[ 32 ] = $D0
ShellCode[ 33 ] = $C9
ShellCode[ 34 ] = $C3

' define a function using standard C calling convention
' residing at the address of ShellCode (on the heap)
'
Global Exec( ) "C" = ShellCode

' start excecution of the shellcode
'
Exec( )

' test the stack frame
'
Print "Stack Frame Is Ok!"

' exit program
'
End
