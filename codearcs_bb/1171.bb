; ID: 1171
; Author: wizzlefish
; Date: 2004-10-05 18:53:47
; Title: Simple File Save, File Load
; Description: Functions to save information to a *.dat file.

[code]
;Function SaveGame(camerahandle)
;Saves the game
;Perimeters:
;camerahandle - the handle of the camera you want to save the position of.
Function SaveGame(camerahandle)
  
  ;set up the screen
  Cls
  Locate 0,0
  ;let user choose what to save game as
  filesave = Input("Save game as - ")
  filesave = filesave + ".dat"

  ;write files to dat file
  fileout = WriteFile(filesave)
  x = EntityX(camerahandle)
  y = EntityY(camerahandle)
  z = EntityZ(camerahandle)
  ;enter any other variables included in any games here.

  x = WriteInt(fileout)
  y = WriteInt(fileout)
  z = WriteInt(fileout)
  ;for extra variables, use "WriteInt," "WriteString," and "WriteByte" accordingly.
  
  ;close file
  CloseFile(fileout)

End Function
  
;Function LoadGame(camerahandle)
;Loads a previously saved game
;Perimeters:
;camerahandle - handle of the main camera
Function LoadGame(camerahandle)

  ;set up the screen
  Cls
  Locate 0,0
  
  ;Let user load game
  fileload = Input("Load file - ")
  fileload = fileload + ".dat"

  ;Open file for reading
  filein = ReadFile(fileload)
  
  ;load info
  x = ReadInt(filein)
  y = ReadInt(filein)
  z = ReadInt(filein)
  ;read all the other data you saved
  ;make sure it is in the same order you saved it as
  
  ;close the file
  CloseFile(filein)
  
  ;position camera
  PositionEntity camerahandle, x, y, z

  SetUp() ; some function to set up everything for your game.

End Function

;(c)2004 No Enemies Games (not really)
