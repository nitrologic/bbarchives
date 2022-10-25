; ID: 3132
; Author: AdamStrange
; Date: 2014-06-15 01:16:09
; Title: osx resource file locations
; Description: osx resource folder

Local FontDir:String = GetPreviousDir(GetPreviousDir(AppFile$) ) + "resources/font/"
If FileType(FontDir) <> 2 Then FontDir = AppDir$ + "/resources/font/"
If FileType(FontDir) <> 2 Then FontDir = AppDir$ + "/font/"
Print "fontdir="+FontDir
