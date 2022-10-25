; ID: 2605
; Author: neos300
; Date: 2009-11-02 10:57:07
; Title: Simple Compiler
; Description: A very simple compiler, with 2 commands.

Function compLine(file, command$)

firstspace=Instr(command," ",1)

If firstspace <> 0
func$=Left(command$,firstspace-1)
params$=Mid$(command$,firstspace+1)
Else
func$ = command$
EndIf

Select func$
Case "print"
WriteByte(file, 1)
WriteString(file, params$)


Case "read"
WriteByte(file, 2)
WriteString(file, params$)


End Select
End Function

Function ExecFile(file)
While Not Eof(file)
opcode = ReadByte(file)

Select opcode
Case 1
Print ReadString$(file)

Case 2
gur$ = ReadString$(file)
If FileType(gur$) = 1
redfile = ReadFile(gur$)
While Not Eof(redfile)
Print ReadLine(redfile)
Wend
CloseFile(redfile)
Else
Print "File not found!"
EndIf


End Select
Wend
End Function
