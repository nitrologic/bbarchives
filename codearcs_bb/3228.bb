; ID: 3228
; Author: Yue
; Date: 2015-10-06 15:16:15
; Title: Loading
; Description: Loading resources in the background. FastPointer

Global loadingFinish% = false
global mesh%
global animImage% = LoadAnimImage("Loading.png",256,64,0,9) ; Image Animation for Loading.

threadPointer% = FunctionPointer()
Goto Jump
LoadingLevel() ; Function thread.		
.Jump

trhead% = CreateThread (threadPointer%, 100)


repeat 

   cls

   X% = X% + 1
  if X% = 8> X% = 0; Repeat Frame image animating.

  DrawImage AnimImage%,100,100,X%
  
   if loadingFinish% = true then 

     Exit  ; Finish Loading.

   end if 
   
   ; NO USE RENDERWORD. 
   flip 


forever 
If IsThread(Thread) Then FreeThread(Thread%)
freeimage animImage%

Function LoadingLevel%()


    repeat 
        delay 100
       
       if   loadingFinish% = false then 


          mesh% = LoadingMesh("Mesh.b3d")

         
          loadingFinish% = true 
       End if        


    forever


End Function
