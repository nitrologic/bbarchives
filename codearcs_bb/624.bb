; ID: 624
; Author: Vertex
; Date: 2003-03-16 04:19:55
; Title: MP3-Infos
; Description: Read the ID3v1.0 TAG

; ----------------------------------------------------------------------------- 
  Info = Get_ID3v1_Info("Music.mp3") 

  If Info <> 0 Then 
      Print "Titel:       " + Get_ID3v1_Titel$(Info) 
      Print "Interpreter: " + Get_ID3v1_Interpreter$(Info) 
      Print "Album:       " + Get_ID3v1_Album$(Info) 
      Print "Year:        " + Get_ID3v1_Year(Info) 
      Print "Comment:     " + Get_ID3v1_Comment$(Info) 
      Print "Category:    " + Get_ID3v1_Category$(Info) 
  Else 
      Print "Cant read informations" 
  EndIf 

  FreeBank Info : WaitKey : End 
; ----------------------------------------------------------------------------- 

; ----------------------------------------------------------------------------- 
  Function Get_ID3v1_Info(File$) 
     Local Stream = ReadFile(File$) 
     Local Info = CreateBank(127) 
     Local B,ID3$,TAG$ 
    
     ; Check if file exist 
     If Stream = 0 Then 
         Return False 
     EndIf 
    
     ; Check if this a ID3-Tag 
     For B = 1 To 3 
         ID3$ = ID3$ + Chr$(ReadByte(Stream)) 
     Next 
     If ID3$ <> "ID3" Then 
         CloseFile Stream 
         Return False 
     EndIf 

     ; Poke the infos in the Info-Bank 
     SeekFile Stream,FileSize(File$) - 128 
     For B = 0 To 126 
         PokeByte Info,B,ReadByte(Stream) 
     Next 
     ; Check For "TAG" 
     For B = 0 To 2 
         TAG$ = TAG$ + Chr$(PeekByte(Info,B)) 
     Next 
    
     If TAG$ <> "TAG" Then 
        CloseFile Stream 
        FreeBank Info 
        Return False 
     EndIf 
    
     ; Close the file 
     CloseFile Stream 
    
     ; Return the handle of the Info-Bank 
     Return Info 
  End Function 
; ----------------------------------------------------------------------------- 

; ----------------------------------------------------------------------------- 
  ; Read the titel 
  Function Get_ID3v1_Titel$(Info) 
      Local B,Byte,Titel$ 
      For B = 3 To 3 + 30 
          Byte = PeekByte(Info,B) 
          If Byte <> 0 Then 
              Titel$ = Titel$ + Chr$(Byte) 
          Else 
              If Len(Titel$) > 0 Then 
                  Return Titel$ 
              Else 
                  Return "Unknown" 
              EndIf 
          EndIf 
      Next 
  End Function 

  ; Read the interpreter 
  Function Get_ID3v1_Interpreter$(Info) 
      Local B,Byte,Interpreter$ 
      For B = 33 To 32 + 30 
          If Byte <> 0 Then 
              Interpreter$ = Interpreter$ + Chr$(Byte) 
          Else 
              If Len(Interpreter$) > 0 Then 
                  Return Interpreter$ 
              Else 
                  Return "Unknown" 
              EndIf 
         EndIf 
      Next 
  End Function 

  ; Read the albumname 
  Function Get_ID3v1_Album$(Info) 
      Local B,Byte,Album$ 
      For B = 63 To 63 + 30 
          Byte = PeekByte(Info,B) 
          If Byte <> 0 Then 
              Album$ = Album$ + Chr$(Byte) 
          Else 
              If Len(Album$) > 0 Then 
                  Return Album$ 
              Else 
                  Return "Unknown" 
              EndIf 
          EndIf 
      Next 
  End Function 

  ; Read the yaer 
  Function Get_ID3v1_Year(Info) 
      Local B,Byte,Yaer$ 
      For B = 93 To 93 + 4 
          Byte = PeekByte(Info,B) 
          If Byte <> 0 Then 
              Yaer$ = Yaer$ + Chr$(Byte) 
          Else 
              If Len(Yaer$) > 0 Then 
                  Return Yaer$ 
              Else 
                  Return "Unknown" 
              EndIf 
          EndIf 
      Next 
  End Function    

  ; Read the comment 
  Function Get_ID3v1_Comment$(Info) 
      Local B,Byte,Comment$ 
      For B = 97 To 97 + 30 
          Byte = PeekByte(Info,B) 
          If Byte <> 0 Then 
              Comment$ = Comment$ + Chr$(Byte) 
          Else 
              If Len(Comment$) > 0 Then 
                  Return Comment$ 
              Else 
                  Return "Unknown" 
              EndIf 
          EndIf 
      Next 
  End Function 

  ; Read the Category 
  Function Get_ID3v1_Category$(Info) 
      Local Byte = PeekByte(Info,126) 
      Select Byte 
          Case 000 : Return "Unknown" 
          Case 001 : Return "Blues" 
          Case 002 : Return "Classic Rock" 
          Case 003 : Return "Country" 
          Case 004 : Return "Dance" 
          Case 005 : Return "Disco" 
          Case 006 : Return "Funk" 
          Case 007 : Return "Grunge" 
          Case 008 : Return "Hip-Hop" 
          Case 009 : Return "Jazz" 
          Case 010 : Return "Metal" 
          Case 011 : Return "New Age" 
          Case 012 : Return "Oldies" 
          Case 013 : Return "Other" 
          Case 014 : Return "Pop" 
          Case 015 : Return "R&B" 
          Case 016 : Return "Rap" 
          Case 017 : Return "Reggae" 
          Case 018 : Return "Rock" 
          Case 019 : Return "Techno" 
          Case 020 : Return "Industrial" 
          Case 021 : Return "Alternative" 
          Case 022 : Return "Ska" 
          Case 023 : Return "Death Metal" 
          Case 024 : Return "Pranks" 
          Case 025 : Return "Soundtrack" 
          Case 026 : Return "Euro-Techno" 
          Case 027 : Return "Ambient" 
          Case 028 : Return "Trip-Hop" 
          Case 029 : Return "Vocal" 
          Case 030 : Return "Jazz+Funk" 
          Case 031 : Return "Fusion" 
          Case 032 : Return "Trance" 
          Case 033 : Return "Classical" 
          Case 034 : Return "Instrumental" 
          Case 035 : Return "Acid" 
          Case 036 : Return "House" 
          Case 037 : Return "Game" 
          Case 038 : Return "Sound Clip" 
          Case 039 : Return "Gospel" 
          Case 040 : Return "Noise" 
          Case 041 : Return "AlternRock" 
          Case 042 : Return "Bass" 
          Case 043 : Return "Soul" 
          Case 044 : Return "Punk" 
          Case 045 : Return "Space" 
          Case 046 : Return "Meditative" 
          Case 047 : Return "Instrumental Pop" 
          Case 048 : Return "Instrumental Rock" 
          Case 049 : Return "Ethnic" 
          Case 050 : Return "Gothic" 
          Case 051 : Return "Darkwave" 
          Case 052 : Return "Techno-Industrial" 
          Case 053 : Return "Electronic" 
          Case 054 : Return "Pop-Folk" 
          Case 055 : Return "Eurodance" 
          Case 056 : Return "Dream" 
          Case 057 : Return "Southern Rock" 
          Case 058 : Return "Comedy" 
          Case 059 : Return "Cult" 
          Case 060 : Return "Gangsta" 
          Case 061 : Return "Top 40" 
          Case 062 : Return "Christian Rap" 
          Case 063 : Return "Pop/Funk" 
          Case 064 : Return "Jungle" 
          Case 065 : Return "Native American" 
          Case 066 : Return "Cabaret" 
          Case 067 : Return "New Wave" 
          Case 068 : Return "Psychadelic" 
          Case 069 : Return "Rave" 
          Case 070 : Return "Showtunes" 
          Case 071 : Return "Trailer" 
          Case 072 : Return "Lo-Fi" 
          Case 073 : Return "Tribal" 
          Case 074 : Return "Acid Punk" 
          Case 075 : Return "Acid Jazz" 
          Case 076 : Return "Polka" 
          Case 077 : Return "Retro" 
          Case 078 : Return "Musical" 
          Case 079 : Return "Rock & Roll" 
          Case 080 : Return "Hard Rock" 
          ; Added by WinAmp : 
          Case 081 : Return "Folk" 
          Case 082 : Return "Folk/Rock" 
          Case 083 : Return "National Folk" 
          Case 084 : Return "Swing" 
          Case 085 : Return "Bebob" 
          Case 086 : Return "Latin" 
          Case 087 : Return "Revival" 
          Case 088 : Return "Celtic" 
          Case 089 : Return "Bluegrass" 
          Case 090 : Return "Avantgarde" 
          Case 091 : Return "Gothic Rock" 
          Case 092 : Return "Progressive Rock" 
          Case 093 : Return "Psychedelic Rock" 
          Case 094 : Return "Symphonic Rock" 
          Case 095 : Return "Slow Rock" 
          Case 096 : Return "Big Band" 
          Case 097 : Return "Chorus" 
          Case 098 : Return "Easy Listening" 
          Case 099 : Return "Acoustic" 
          Case 100 : Return "Humour" 
          Case 101 : Return "Speech" 
          Case 102 : Return "Chanson" 
          Case 103 : Return "Opera" 
          Case 104 : Return "Chamber Music" 
          Case 105 : Return "Sonata" 
          Case 106 : Return "Symphony" 
          Case 107 : Return "Booty Bass" 
          Case 108 : Return "Primus" 
          Case 109 : Return "Porn Groove" 
          Case 110 : Return "Satire" 
          Case 111 : Return "Slow Jam" 
          Case 112 : Return "Club" 
          Case 113 : Return "Tango" 
          Case 114 : Return "Samba" 
          Case 115 : Return "Folklore" 
          Case 116 : Return "Ballad" 
          Case 117 : Return "Power Ballad" 
          Case 118 : Return "Rhythmic Soul" 
          Case 119 : Return "Freestyle" 
          Case 120 : Return "Duet" 
          Case 121 : Return "Punk Rock" 
          Case 122 : Return "Drum Solo" 
          Case 123 : Return "A capella" 
          Case 124 : Return "Euro-House" 
          Case 125 : Return "Dance Hall" 
      End Select 
  End Function 
; -----------------------------------------------------------------------------
