; ID: 714
; Author: cbmeeks
; Date: 2003-06-09 11:28:52
; Title: ClearBank
; Description: Fills a bank with zeros

;--------------------------------------------------------
;	ClearBank(bank)
;
;		bank = bank handle
;
;		by cbmeeks of SignalDEV
;--------------------------------------------------------
Function ClearBank(bank)
	;doesn't get any easier than this people  :-)
	Local c
	For c=0 To BankSize(bank)-1
		PokeByte(bank,c,0)
	Next
End Function
