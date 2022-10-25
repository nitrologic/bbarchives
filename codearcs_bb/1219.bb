; ID: 1219
; Author: MPZ
; Date: 2004-12-02 08:09:43
; Title: newstart-requester with winapi
; Description: newstart-requester

; This Procedure is for free MPZ (@) from Berlin
; Version 0.1 12/2004
; 
; This works only with ME/XP/W2000 NOT WIN9x
;
; Write the following files in the blitz/userlibs 

; file with name "setupapi.decl" and the content:
; .lib "setupapi.dll"
; api_SetupPromptReboot (FileQueue%, Owner%, ScanOnly%) : "SetupPromptReboot"

; This program starts the newstart requester of WIN ME/XP/W2000


api_SetupPromptReboot (0,0,0)
End
