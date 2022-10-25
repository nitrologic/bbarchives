; ID: 1346
; Author: Andres
; Date: 2005-04-10 05:43:19
; Title: Application protocol
; Description: Execute an application from a webbrowser with certain parameter

Function CreateProtocolRegistryFile(registryfile$, name$, protocol$, executable$)
	wf = WriteFile(registryfile$)
	If wf
		While Instr(executable$, "\", i)
			i = Instr(executable$, "\", i)
			executable$ = Mid$(executable$, 1, i) + "\" + Mid$(executable$, i + 1)
			i = i + 2
		Wend
		
		WriteLine wf, "Windows Registry Editor Version 5.00"
		WriteLine wf, ""
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "]"
		WriteLine wf, "@=" + Chr(34) + "URL:" + name$ + Chr(34)
		WriteLine wf, Chr(34) + "URL Protocol" + Chr(34) + "=" + Chr(34) + Chr(34)
		WriteLine wf, ""
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\DefaultIcon]"
		WriteLine wf, "@=" + Chr(34) + "\" + Chr(34) + executable$ + "\" + Chr(34) + Chr(34)
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell]"
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell\open]"
		WriteLine wf, "[HKEY_CLASSES_ROOT\" + protocol$ + "\shell\open\command]"
		WriteLine wf, "@=" + Chr(34) + "\" + Chr(34) + executable$ + "\" + Chr(34) + " /join %1" + Chr(34)
		
		CloseFile wf
		Return True
	Else
		Return False
	EndIf
End Function
