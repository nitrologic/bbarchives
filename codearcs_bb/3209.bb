; ID: 3209
; Author: ZJP
; Date: 2015-05-30 12:44:03
; Title: USERLIB DECLS (DLL) TO CSharp (Unity 3D) converter
; Description: convert DECLS blitz3d file to standard DllImport instructions

;
; Blitz3D USERLIB DECLS (DLL) TO CSharp (Unity 3D) (c) 2015  ZJP  
; PUBLIC DOMAIN 
; 
Dim parameter$(10) 
Global i% 
Global DECLS_INPUT_FILE$ 
Global CPP_OUTPUT_FILE$ 
Global DECLS_FIRST_CAR$ 
Global line_input_file$ 
Global Found_char% 
Global Alias_Dll$ 
Global middle$ 
Global tempo_string$ 
Global car$ 
Global nb_param% 
Global DLL_lib$ 
Global CALLING_example$ 
Global tempo$ 
Global PROC_Def$ 
Global Position% 
Global Var_TYPE$ 
Global bof$ 
Global ok$ 
; 

; Fichier DECLS - DECLS File <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change this !!!
DECLS_INPUT_FILE = "Cheetah2.DECLS" 

; Lines begin with...<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change this !!!
DECLS_FIRST_CAR = "xdb" 

; DLL Name<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change this !!!
DLL_lib = "Cheetah2"


CPP_OUTPUT_FILE = DLL_lib + ".cs" 
; 
file_in%  = OpenFile(DECLS_INPUT_FILE) 
file_out% = WriteFile(CPP_OUTPUT_FILE) 
; 
WriteLine(file_out,"")
WriteLine(file_out,"using UnityEngine;")
WriteLine(file_out,"using System.Collections;")
WriteLine(file_out,"using System.Runtime.InteropServices;")
WriteLine(file_out,"")
WriteLine(file_out,"public class " + DLL_lib + " : MonoBehaviour")
WriteLine(file_out,"{")


; 
While Not Eof(file_in) 

   line_input_file$ = ReadLine$( file_in ) 

	; Remove all spaces in the input line 
	tempo="" 
	For i=1 To Len(line_input_file$) 
		car$=Mid(line_input_file$,i,1) 
		If car$<>" " Then tempo$=tempo$+car$ 
	Next 
	line_input_file$=tempo$ 

	; Select only the line began with "px" in this example 
   If Left(line_input_file, Len(DECLS_FIRST_CAR)) <> DECLS_FIRST_CAR Then Goto suivant 
	

   ;  find the Alias 
   Found_char = Instr(line_input_file, ":", 1) 
   Alias_Dll = Mid(line_input_file, Found_char + 1) 


;   CALLING_example = "[DllImport (" + Chr(34) + DLL_lib + Chr(34) + ", EntryPoint = " + Alias_Dll + ")]" + Chr(13) + Chr(9) + Chr(9) + "private static extern "
   CALLING_example = Chr(9)+ "[DllImport (" + Chr(34) + DLL_lib + Chr(34) + ", EntryPoint = " + Alias_Dll + ")] private static extern "
   middle = " " 
    
   ; Var_TYPE >> % # Or $ * 
   Found_char = Instr(line_input_file, "(", 1) 
   Var_TYPE = Mid(line_input_file, Found_char - 1, 1) 
  
   ; % Integer 
   If Var_TYPE = "%" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       CALLING_example = CALLING_example + "int " 
   End If 
    
   ; # Float 
   If Var_TYPE = "#" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       CALLING_example = CALLING_example + "float " 
   End If 
    
   ; $ STRING 
   If Var_TYPE = "$" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       CALLING_example = CALLING_example + "string " 
    End If 
    
   ; * POINTER 
   If Var_TYPE = "*" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       CALLING_example = CALLING_example + "ptr " 
    End If 
    
   ; void 
   If Var_TYPE <> "#" And Var_TYPE <> "%" And Var_TYPE <> "$" And Var_TYPE <> "*" Then 
       tempo = Left(line_input_file, Found_char - 1) 
       CALLING_example = CALLING_example + "void " 
   End If 
    
    
   ; Nbr of parameters 
   If Mid(line_input_file, Found_char + 1, 1) = ")" Then 
       middle = ");" 
       Goto continue 
   End If 
    
   ; parameters 
   ; remove the ( ) 
   tempo_string = "" 
   nb_param = 0 
   For i = Found_char + 1 To 1000 
       car = Mid(line_input_file, i, 1) 
           If car = "," Then 
               nb_param = nb_param + 1 
               parameter(nb_param) = Trim(tempo_string) 
               car = "" 
               tempo_string = "" 
           End If 
           If car = ")" Then 
               nb_param = nb_param + 1 
               parameter(nb_param) = Trim(tempo_string) 
               tempo_string = "" 
               Exit 
           End If 
       tempo_string = tempo_string + car 
   Next 
    
   ; parameters 
   middle = " " 
   For i = 1 To nb_param 
       ; % interger 
       If Right(parameter(i), 1) = "%" Then 
           ok = " int " + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
       ; * pointer 
       If Right(parameter(i), 1) = "*" Then 
           ok = " System.IntPtr " + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
		; # float 
       If Right(parameter(i), 1) = "#" Then 
           ok = " float " + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
		; $ string 
       If Right(parameter(i), 1) = "$" Then 
           ok = " string " + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
       middle = middle + ok 
       ok = "" 
   Next 
   ; remove the last ";" 
	If Len(middle)>1 Then middle=Trim(middle) 
   middle = Left(middle, Len(middle) - 1) + ");" 

.continue 

  WriteLine( file_out,Chr(9)+"// Original DECLS line ==> " + line_input_file) 
  WriteLine( file_out,CALLING_example + tempo + "(" + Trim(middle)) 

.suivant 
Wend 
; 
 
; 
WriteLine(file_out,"")
WriteLine(file_out,"")
WriteLine(file_out,Chr(9)+"void Start ()")
WriteLine(file_out,Chr(9)+"{")
WriteLine(file_out,Chr(9)+"}")
WriteLine(file_out,"}")
CloseFile file_in 
CloseFile file_out 
End
