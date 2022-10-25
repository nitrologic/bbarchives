; ID: 1649
; Author: Andres
; Date: 2006-03-27 16:19:37
; Title: Data Archiver
; Description: Storing archiver not packing

Function LoadDAFile%(path$, FNCFile$ = "data.da", targetfile$ = "temp.da")
	If Not Mid$(path$, 1, 1) = "\" Then path$ = "\" + path$
	Local file% = ReadFile(FNCFile$), bank%, output%
	If file%
		FNCVersion$ = ReadString$(file%)
		While Not Eof(file%)
				tpath$ = ReadString(file%)
				isfile% = ReadByte(file%)
				size% = ReadInt(file%)
				If Lower(tpath$) = Lower(path$) And isfile% = 1
					output% = WriteFile(targetfile$)
					bank = CreateBank(10 * 1024)
					If bank% And output%
						While TN < size%
							If TN + BankSize(bank) > size% Then N = size% - TN Else N = BankSize(bank)
							N = ReadBytes(bank, file%, 0, N)
							For i = 0 To N - 1
								PokeByte(bank, i, 255 - PeekByte(bank, i))
							Next
							WriteBytes(bank, output%, 0, N)
							TN = TN + N
						Wend
						CloseFile file%
						CloseFile output%
						FreeBank bank
						Return True
					EndIf
				Else
					SeekFile(file%, FilePos(file%) + size%)
				EndIf
		Wend
		CloseFile file%
	EndIf
End Function

Function FreeDAFile(targetfile$ = "temp.da")
	If FileType(targetfile$) = 1 Then DeleteFile targetfile$
	If FileType(targetfile$) = 0 Then Return True
End Function
