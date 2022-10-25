; ID: 2056
; Author: ZJP
; Date: 2007-07-06 11:44:17
; Title: Blitz3DSDK - USERLIB DECLS (DLL) TO C/C++ converter
; Description: Blitz3DSDK :  How to use the Userlib/DLL in C/C++ without a .DEF or .LIB file

; BLITZ3D DECLS to C/C++ Converter. (c) 2007 ZJP  
; BLITZ3D DLL without a .DEF or .LIB file ********************** 
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
Global TYPE_Def$ 
Global POINTEUR_Def$ 
Global PROC_Def$ 
Global Position% 
Global Var_TYPE$ 
Global bof$ 
Global ok$ 
; 
; Fichier DECLS - DECLS File <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change 
DECLS_INPUT_FILE = "blitzpx.DECLS" 
; C/C++ File<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change 
CPP_OUTPUT_FILE = "INCLUDE.CPP" ; Or .CPP 
; Lines begin with...<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change 
DECLS_FIRST_CAR = "px" 
; DLL Name<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Change 
DLL_lib = "blitzpx.DLL" 
; 
; THE C/C++ 'GOAL'.  JUST AN EXAMPLE ;-) 
; 
;    HINSTANCE hDLL = LoadLibrary("blitzpx.DLL"); 
; 
;    // Original DECLS line ==> pxBodyCreatePlane%(x#, y#, z#):"_pxBodyCreatePlane@12" 
;    typedef int(WINAPI *DLL_pxBodyCreatePlane)(float x, float y, float z); 
;    DLL_pxBodyCreatePlane pxBodyCreatePlane; 
;    pxBodyCreatePlane = (DLL_pxBodyCreatePlane)GetProcAddress(hDLL,"_pxBodyCreatePlane@12"); 
;    // Use ==> Int result = pxBodyCreatePlane(Float x, Float y, Float z); 
; 
file_in% = OpenFile(DECLS_INPUT_FILE) 
file_out% = WriteFile(CPP_OUTPUT_FILE) 
; 
WriteLine(file_out,"// BLITZ3D DECLS to C/C++ Converter. (c) 2007 ZJP zjp@laposte.net") 
WriteLine(file_out,"// BLITZ3D DLL without a .DEF or .LIB file **********************") 
WriteLine(file_out,"// PUBLIC DOMAIN") 
WriteLine(file_out,"#include ") 
WriteLine(file_out,"#include ") 
WriteLine(file_out," ") 
WriteLine(file_out,"int main(){") 
WriteLine(file_out," ") 
WriteLine(file_out,"    HINSTANCE hDLL = LoadLibrary(" + Chr(34) + DLL_lib + Chr(34) + ");") 
WriteLine(file_out," ") 
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
	
   CALLING_example = " " 
   middle = " " 
    
   ;  find the Alias 
   Found_char = Instr(line_input_file, ":", 1) 
   Alias_Dll = Mid(line_input_file, Found_char + 1) 
    
   ; Var_TYPE >> % # Or $ * 
   Found_char = Instr(line_input_file, "(", 1) 
   Var_TYPE = Mid(line_input_file, Found_char - 1, 1) 
  
   ; % Integer 
   If Var_TYPE = "%" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       TYPE_Def = "typedef int(WINAPI *DLL_" + tempo + ")(" 
       CALLING_example = "// Use ==> int result = " 
   End If 
    
   ; # Float 
   If Var_TYPE = "#" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       TYPE_Def = "typedef float(WINAPI *DLL_" + tempo + ")(" 
       CALLING_example = "// Use ==> float result = " 
   End If 
    
   ; $ STRING 
   If Var_TYPE = "$" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       TYPE_Def = "typedef char*(WINAPI *DLL_" + tempo + ")(" 
       CALLING_example = "// Use ==> char *result = " 
    End If 
    
   ; * POINTER 
   If Var_TYPE = "*" Then 
       tempo = Left(line_input_file, Found_char - 2) 
       TYPE_Def = "typedef *int(WINAPI *DLL_" + tempo + ")(" 
       CALLING_example = "// Use ==> int *result = " 
    End If 
    
   ; void 
   If Var_TYPE <> "#" And Var_TYPE <> "%" And Var_TYPE <> "$" And Var_TYPE <> "*" Then 
       tempo = Left(line_input_file, Found_char - 1) 
       TYPE_Def = "typedef void(WINAPI *DLL_" + tempo + ")(" 
       CALLING_example = "// Use ==> " 
   End If 
    
  POINTEUR_Def = "DLL_" + tempo + " " + tempo + ";" 
  PROC_Def = tempo + " = (DLL_" + tempo + ")GetProcAddress(hDLL," + Alias_Dll + ");" 
    
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
           ok = " int *" + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
		; # float 
       If Right(parameter(i), 1) = "#" Then 
           ok = " float " + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
		; $ string 
       If Right(parameter(i), 1) = "$" Then 
           ok = " char *" + Left(parameter(i), Len(parameter(i)) - 1) + "," 
       End If 
       middle = middle + ok 
       ok = "" 
   Next 
   ; remove the last ";" 
	If Len(middle)>1 Then middle=Trim(middle) 
   middle = Left(middle, Len(middle) - 1) + ");" 

.continue 

   WriteLine( file_out,"    // Original DECLS line ==> " + line_input_file) 
	WriteLine( file_out,"    " + TYPE_Def + middle) 
   WriteLine( file_out,"    " + POINTEUR_Def) 
   WriteLine( file_out,"    " + PROC_Def) 
   WriteLine( file_out,"    " + CALLING_example + tempo + "(" + Trim(middle)) 
   WriteLine( file_out," ") 

.suivant 
Wend 
; 
WriteLine( file_out,"    // Your code here !!!") 
WriteLine( file_out," ") 
WriteLine( file_out,"    FreeLibrary(hDLL);") 
WriteLine( file_out,"    return 0;") 
WriteLine( file_out,"}") 
; 
CloseFile file_in 
CloseFile file_out 
End
