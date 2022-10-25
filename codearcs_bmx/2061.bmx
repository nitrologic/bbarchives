; ID: 2061
; Author: xMicky
; Date: 2007-07-11 19:57:53
; Title: Store media encrypted
; Description: Let you store the media used by your program encrypted within a single file

Strict
'----------------------------------------------------------------------------------------------------------
'Function AddFileToBank(fileNameAndPath, filesAliasNameInBank, targetBank)
'- copies given file from HDD into targetBank
'  under a free choosable alias name, which furtherly is used to access the files data
'  supported file formats are:
'  IMAGE: .bmp, .jpg, .png
'  SOUND: .wav, .ogg
'  TEXT:  .txt
'  (what types of media files are supported is not a question of this bank-routines but depends
'   on what BMax LoadImage(), LoadSound() can read...!)
'- if targetBank =NULL: a new bank is created
'- returns targetBank 
'       or NULL, if fileNameAndPath was not found or filesAliasNameInBank was not given
Function AddFileToBank:TBank(fileNameAndPath:String, filesAliasNameInBank:String, targetBank:TBank =Null)

  filesAliasNameInBank =Trim$(filesAliasNameInBank)

  If FileType(fileNameAndPath) =1 And filesAliasNameInBank <>"" Then
    fileNameAndPath =Lower(fileNameAndPath)
    Local curFileType:Int =0
    If Instr(".bmp .jpg .png", Right$(fileNameAndPath, 4)) >0 Then
      curFileType =1
    ElseIf Instr(".wav .ogg", Right$(fileNameAndPath, 4)) >0 Then    
      curFileType =2
    ElseIf Right$(fileNameAndPath, 4) =".txt" Then
      curFileType =3
    End If

    Local targetBankStream:TBankStream =Null
    If targetBank =Null Then
      targetBank =CreateBank(0)
    End If
    targetBankStream =CreateBankStream(targetBank)

    Local curPos:Int =BankSize(targetBank)
    SeekStream(targetBankStream, curPos)

    Local tmpStream:TStream =ReadStream(fileNameAndPath)

    WriteInt(targetBankStream, curFileType)
    WriteInt(targetBankStream, Len(filesAliasNameInBank))
    WriteString(targetBankStream, filesAliasNameInBank)
    WriteInt(targetBankStream, StreamSize(tmpStream))

    CopyStream(tmpStream, targetBankStream)
    CloseStream(tmpStream) 
    CloseStream(targetBankStream) 

    Return targetBank

  Else
    Return Null
  End If

End Function
'----------------------------------------------------------------------------------------------------------
'Function LoadEncryptedBank(banksFileAndPath, useDecryption)
'- loads encrypted bank into RAM and decrypts it for further access
'- returns bank or NULL, if bank was not found
Function LoadEncryptedBank:TBank(banksFileAndPath:String, useDecryption:Int =True)

  Local curBank:TBank =Null

  If FileType(banksFileAndPath) =1 Then
    curBank =LoadBank(banksFileAndPath)

    If useDecryption Then
      'this deciphering must refer to the algorhitm used in SaveBankEncrypted(...)
      SeedRnd(7777) 
      For Local n:Int =1 To BankSize(curBank)
        Local curByte:Int =PeekByte(curBank, n -1)
        Local decrypted:Int =curByte -Rand(0, 255)
        While decrypted <0
          decrypted :+256
        Wend
        PokeByte(curBank, n -1, decrypted)
      Next
    End If
  End If

  Return curBank

End Function
'----------------------------------------------------------------------------------------------------------
'Function SaveBankEncrypted(targetBank, banksFileAndPath, useEncryption)
'- encrypts given bank and saves encrypted bank to given file on HDD
Function SaveBankEncrypted(targetBank:TBank, banksFileAndPath:String, useEncryption:Int =True)

  If useEncryption Then
    'this is just a hint for some little encoding,
    'let your phantasies fly to write your own perfect algorhitm ;)
    SeedRnd(7777) 
    For Local n:Int =1 To BankSize(targetBank)
      Local curByte:Int =PeekByte(targetBank, n -1)
      Local encrypted:Int =(curByte +Rand(0, 255)) Mod 256
      PokeByte(targetBank, n -1, encrypted)
    Next
  End If

  SaveBank(targetBank, banksFileAndPath)

End Function
'----------------------------------------------------------------------------------------------------------
'Function ReadImageFromBank(targetBank, imagesAliasName, imagesLoadingsFlags)
'- reads image with given imagesAliasName out of bank
'- returns image 
'       or NULL
'          - if bank does not exist 
'          - or imagesAliasName was not found
'          - or imagesAliasName could have not been loaded (because originally it was not an image file)
Function ReadImageFromBank:TImage(targetBank:TBank, imagesAliasName:String, imagesLoadingsFlags:Int =-1)

  imagesAliasName =Trim$(imagesAliasName)
  Local result:TImage =Null

  If targetBank <>Null And imagesAliasName <>"" Then
    Local targetBankStream:TBankStream =CreateBankStream(targetBank)
    Local curPos:Int  =0
    Local curSize:Int =0
    Local curFileType:Int =0
    While Not Eof(targetBankStream)
      curFileType =ReadInt(targetBankStream)
      curPos :+4
      Local curLen:Int =ReadInt(targetBankStream)
      curPos :+4
      Local curAlias:String =ReadString(targetBankStream, curLen)
      curPos :+curLen
      curSize =ReadInt(targetBankStream)
      curPos :+4
      If curAlias =imagesAliasName And curFileType =1 Then
        result =LoadImage(targetBankStream, imagesLoadingsFlags)
        Exit 
      Else 
        curPos :+curSize
        SeekStream(targetBankStream, curPos)
      End If
    Wend
    CloseStream(targetBankStream) 
    Return result

  Else
    Return Null
  End If

End Function
'----------------------------------------------------------------------------------------------------------
'Function ReadSoundFromBank(targetBank, soundsAliasName, flags)
'- reads sound with given soundsAliasName out of bank
'- returns sound 
'       or NULL
'          - if bank does not exist 
'          - or soundsAliasName was not found
'          - or soundsAliasName could have not been loaded (because originally it was not a sound file)
Function ReadSoundFromBank:TSound(targetBank:TBank, soundsAliasName:String, flags:Int =0)

  soundsAliasName =Trim$(soundsAliasName)
  Local result:TSound =Null

  If targetBank <>Null And soundsAliasName <>"" Then
    Local targetBankStream:TBankStream =CreateBankStream(targetBank)
    Local curPos:Int  =0
    Local curSize:Int =0
    Local curFileType:Int =0
    While Not Eof(targetBankStream)
      curFileType =ReadInt(targetBankStream)
      curPos :+4
      Local curLen:Int =ReadInt(targetBankStream)
      curPos :+4
      Local curAlias:String =ReadString(targetBankStream, curLen)
      curPos :+curLen
      curSize =ReadInt(targetBankStream)
      curPos :+4
      If curAlias =soundsAliasName And curFileType =2 Then
        result =LoadSound(targetBankStream, flags)
        Exit 
      Else 
        curPos :+curSize
        SeekStream(targetBankStream, curPos)
      End If
    Wend
    CloseStream(targetBankStream) 
    Return result

  Else
    Return Null
  End If

End Function
'----------------------------------------------------------------------------------------------------------
'Function ReadTextFromBank(targetBank, textsAliasName)
'- reads text with given textsAliasName out of bank
'- returns text as a string 
'       or ""
'          - if bank does not exist 
'          - or textsAliasName was not found
'          - or textsAliasNamecould have not been loaded (because originally it was not a text file)
Function ReadTextFromBank:String(targetBank:TBank, textsAliasName:String)

  textsAliasName =Trim$(textsAliasName)
  Local result:String =""

  If targetBank <>Null And textsAliasName <>"" Then
    Local targetBankStream:TBankStream =CreateBankStream(targetBank)
    Local curPos:Int  =0
    Local curSize:Int =0
    Local curFileType:Int =0
    While Not Eof(targetBankStream)
      curFileType =ReadInt(targetBankStream)
      curPos :+4
      Local curLen:Int =ReadInt(targetBankStream)
      curPos :+4
      Local curAlias:String =ReadString(targetBankStream, curLen)
      curPos :+curLen
      curSize =ReadInt(targetBankStream)
      curPos :+4
      If curAlias =textsAliasName And curFileType =3 Then
        result =ReadString(targetBankStream, curSize)
        Exit 
      Else 
        curPos :+curSize
        SeekStream(targetBankStream, curPos)
      End If
    Wend
    CloseStream(targetBankStream) 
    Return result

  Else
    Return ""
  End If

End Function
'----------------------------------------------------------------------------------------------------------
'Function DeleteAliasInBank(targetBank, deletedAliasNameInBank)
'- removes resource with given deletedAliasNameInBank from bank
'- returns changed bank or unchanged bank, if deletedAliasNameInBank was not found
Function DeleteAliasInBank:TBank(targetBank:TBank, deletedAliasNameInBank:String)

  deletedAliasNameInBank =Trim$(deletedAliasNameInBank)

  If targetBank <>Null And deletedAliasNameInBank <>"" Then
    Local targetBank2:TBank =CreateBank(0)
    Local targetBankStream2:TBankStream =CreateBankStream(targetBank2)

    Local targetBankStream:TBankStream =CreateBankStream(targetBank)
    Local curPos:Int  =0
    Local curSize:Int =0
    Local curFileType:Int =0
    While Not Eof(targetBankStream)
      curFileType =ReadInt(targetBankStream)
      curPos :+4
      Local curLen:Int =ReadInt(targetBankStream)
      curPos :+4
      Local curAlias:String =ReadString(targetBankStream, curLen)
      curPos :+curLen
      curSize =ReadInt(targetBankStream)
      curPos :+4
      If curAlias <>deletedAliasNameInBank Then
        WriteInt(targetBankStream2, curFileType)    
        WriteInt(targetBankStream2, curLen)       
        WriteString(targetBankStream2, curAlias)
        WriteInt(targetBankStream2, curSize)     
        CopyBytes(targetBankStream, targetBankStream2, curSize)
      Else 
        curPos :+curSize
        SeekStream(targetBankStream, curPos)
      End If
    Wend
    CloseStream(targetBankStream) 
    CloseStream(targetBankStream2)
    targetBank =Null
    Return targetBank2

  Else
    Return targetBank
  End If  

End Function
'----------------------------------------------------------------------------------------------------------
'Function IsAliasInBank(targetBank, searchedAliasNameInBank)
'- returns type of given searchedAliasNameInBank :
'  0: not within the bank
'  1: image resource
'  2: sound resource
'  3: text resource
Function IsAliasInBank:Int(targetBank:TBank, searchedAliasNameInBank:String)

  searchedAliasNameInBank =Trim$(searchedAliasNameInBank)

  If targetBank <>Null And searchedAliasNameInBank <>"" Then
    Local result:Int =0
    Local targetBankStream:TBankStream =CreateBankStream(targetBank)
    Local curPos:Int  =0
    Local curSize:Int =0
    Local curFileType:Int =0
    While Not Eof(targetBankStream)
      curFileType =ReadInt(targetBankStream)
      curPos :+4
      Local curLen:Int =ReadInt(targetBankStream)
      curPos :+4
      Local curAlias:String =ReadString(targetBankStream, curLen)
      curPos :+curLen
      curSize =ReadInt(targetBankStream)
      curPos :+4
      If curAlias =searchedAliasNameInBank Then
        result =curFileType 
        Exit 
      Else 
        curPos :+curSize
        SeekStream(targetBankStream, curPos)
      End If
    Wend
    CloseStream(targetBankStream) 
    Return result

  Else
    Return 0
  End If

End Function
'----------------------------------------------------------------------------------------------------------

'==========================================================================================================
' example how to use.....
SetGraphicsDriver GLMax2DDriver()
Graphics 800, 600, 0, 0
'---------------------------------------------------------
' TO DO just one time after the program is finalized
' => creating a bank storing the used media files
Local curBank:TBank =AddFileToBank("c:\35.jpg", "pic1")
AddFileToBank("c:\my.txt",    "txt1", curBank)
AddFileToBank("c:\donna.wav", "snd1", curBank)
AddFileToBank("c:\68.jpg",    "pic2", curBank)

'curBank =DeleteAliasInBank(curBank, "pic1")

SaveBankEncrypted(curBank, "C:\myLevel.abc")
'---------------------------------------------------------
'=========================================================
'---------------------------------------------------------
'the programm shipped with the media bank 
'can then read the stored media just as followed:
Local curBank2:TBank =LoadEncryptedBank("C:\myLevel.abc")
Local curImg:TImage  =ReadImageFromBank(curBank2, "pic1")
Local curImg2:TImage =ReadImageFromBank(curBank2, "pic2")
Local curSnd:TSound  =ReadSoundFromBank(curBank2, "snd1")

DebugLog ReadTextFromBank(curBank2, "txt1")
DebugLog IsAliasInBank(curBank2, "pic2")
DebugLog IsAliasInBank(curBank2, "picX")

curSnd.Play

While Not KeyHit(KEY_ESCAPE)

  DrawImage curImg, 100, 100
  DrawImage curImg2, 400, 400

  Flip
Wend
