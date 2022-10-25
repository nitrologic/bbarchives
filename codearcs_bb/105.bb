; ID: 105
; Author: Dragon57
; Date: 2001-10-19 09:39:10
; Title: 3D Camera Fade
; Description: Fade a specified camera in or out to a specified RGB color

Function fade(dir$,camera,secs#=2,R=0,G=0,B=0,distance#=1.00001)
; 3D Fade, V1.0 by Martin A. Parrott (parrottm@hotmail.com)
; This is a quick piece of code to do a fade in/out in BlitzBasic3D
; This is camera based, so it will not work for all circumstances
; This code is based on code originally written in Dark Basic
; Note: This code works differently on various 3D graphics cards
;       so use at your own risk and test it for usability in your
;       own program
;
; This code is free to use, but if you modify it, please send the
; changes to the above email address so I can continue to release
; updates so others can benefit.
;
; Parameters are:
; dir$ - The direction to fade, either in or out
; camera - The camera to fade
; secs# - The number of seconds for the fade
; R - The 'red' value of the color to fade to
; G - The 'green' value of the color to fade to
; B - The 'blue' value of the color to fade to
; distance# - This is how far in front of the camera to put the object
;             Make this a small amount more than the minimum camera range

  Select Lower$(dir$) ; Set a flag so we know later whether to fade in or out

    Case Lower("in")
      state=1

    Case Lower("out")
      state=0

    Default ; Return if this is called without the correct parameters
      Return

  End Select

  If camera=0 Return
  If secs#=0 Return

  Delay#=secs#*1000 ; Calculate delay in milliseconds

  camplane=CreateMesh(camera) ; Create an object to put in front of the camera
  camsurf=CreateSurface(camplane)
  AddVertex(camsurf,-10,-10,0) ; Create 4 corners of a square
  AddVertex(camsurf,-10,10,0)
  AddVertex(camsurf,10,10,0)
  AddVertex(camsurf,10,-10,0)
  AddTriangle(camsurf,0,1,2) ; Connect the dots!
  AddTriangle(camsurf,0,2,3)

  MoveEntity camplane,0,0,distance# ; Position the plane in front of the camera
  EntityAlpha camplane,state ; Make the plane visible/invisible to start with, depending on how the routine is called
  EntityColor camplane,R,G,B ; Color the plane to what the user wants

  starttime#=MilliSecs() ; Get our start time

  Select state

  Case 0
    While MilliSecs() < starttime#+Delay# ; Loop for the delay# time adjusting our alpha amount toward opacity
      newtime#=MilliSecs()
      alpha#=(newtime#-starttime#)/Delay#
      EntityAlpha camplane,alpha#
      UpdateWorld
      RenderWorld
      Flip
    Wend

  Case 1
    While MilliSecs() < starttime#+Delay# ; Loop for the delay# time adjusting our alpha amount toward invisibility
      newtime#=MilliSecs()
      alpha#=((starttime#+Delay#)-newtime#)/Delay#
      EntityAlpha camplane,alpha#
      UpdateWorld
      RenderWorld
      Flip
    Wend

  End Select

  EntityAlpha camplane,state ; Because of roundoff error, make sure our plane is totally invisible/opaque
  FreeEntity camplane ; Clean up
  
End Function

Function fadein(camera,secs#=2,R=0,G=0,B=0,distance#=1.00001)
; Semi-redundant way to call the main function. Sometimes I want to use fade and sometimes fadein as a command. ;)

  fade("in",camera,secs#,R,G,B,distance#)

End Function

Function fadeout(camera,secs#=2,R=0,G=0,B=0,distance#=1.00001)
; Semi-redundant way to call the main function. Sometimes I want to use fade and sometimes fadeout as a command. ;)

  fade("out",camera,secs#,R,G,B,distance#)

End Function

