; ID: 676
; Author: sswift
; Date: 2003-05-08 01:58:03
; Title: Set Camera FOV
; Description: Sets the field of view of the camera with degrees instead of a seemingly  incomprehensible "zoom" value.

Function SetCameraFOV(Camera, FOV#)
   CameraZoom Camera, 1.0 / Tan(FOV#/2.0)
End Function
