; ID: 3206
; Author: BlitzSupport
; Date: 2015-05-23 12:41:27
; Title: mp3 tag reader
; Description: Reads ID3v1 tag information from mp3 files

SuperStrict

' ID3v1 tag reader

Global MP3_GENRE:String [192]

' Standard tags:

MP3_GENRE[0] = "Blues"
MP3_GENRE[1] = "Classic Rock"
MP3_GENRE[2] = "Country"
MP3_GENRE[3] = "Dance"
MP3_GENRE[4] = "Disco"
MP3_GENRE[5] = "Funk"
MP3_GENRE[6] = "Grunge"
MP3_GENRE[7] = "Hip-Hop"
MP3_GENRE[8] = "Jazz"
MP3_GENRE[9] = "Metal"
MP3_GENRE[10] = "New Age"
MP3_GENRE[11] = "Oldies"
MP3_GENRE[12] = "Other"
MP3_GENRE[13] = "Pop"
MP3_GENRE[14] = "R&B"
MP3_GENRE[15] = "Rap"
MP3_GENRE[16] = "Reggae"
MP3_GENRE[17] = "Rock"
MP3_GENRE[18] = "Techno"
MP3_GENRE[19] = "Industrial"
MP3_GENRE[20] = "Alternative"
MP3_GENRE[21] = "Ska"
MP3_GENRE[22] = "Death Metal"
MP3_GENRE[23] = "Pranks"
MP3_GENRE[24] = "Soundtrack"
MP3_GENRE[25] = "Euro-Techno"
MP3_GENRE[26] = "Ambient"
MP3_GENRE[27] = "Trip-Hop"
MP3_GENRE[28] = "Vocal"
MP3_GENRE[29] = "Jazz+Funk"
MP3_GENRE[30] = "Fusion"
MP3_GENRE[31] = "Trance"
MP3_GENRE[32] = "Classical"
MP3_GENRE[33] = "Instrumental"
MP3_GENRE[34] = "Acid"
MP3_GENRE[35] = "House"
MP3_GENRE[36] = "Game"
MP3_GENRE[37] = "Sound Clip"
MP3_GENRE[38] = "Gospel"
MP3_GENRE[39] = "Noise"
MP3_GENRE[40] = "AlternRock"
MP3_GENRE[41] = "Bass"
MP3_GENRE[42] = "Soul"
MP3_GENRE[43] = "Punk"
MP3_GENRE[44] = "Space"
MP3_GENRE[45] = "Meditative"
MP3_GENRE[46] = "Instrumental Pop"
MP3_GENRE[47] = "Instrumental Rock"
MP3_GENRE[48] = "Ethnic"
MP3_GENRE[49] = "Gothic"
MP3_GENRE[50] = "Darkwave"
MP3_GENRE[51] = "Techno-Industrial"
MP3_GENRE[52] = "Electronic"
MP3_GENRE[53] = "Pop-Folk"
MP3_GENRE[54] = "Eurodance"
MP3_GENRE[55] = "Dream"
MP3_GENRE[56] = "Southern Rock"
MP3_GENRE[57] = "Comedy"
MP3_GENRE[58] = "Cult"
MP3_GENRE[59] = "Gangsta"
MP3_GENRE[60] = "Top 40"
MP3_GENRE[61] = "Christian Rap"
MP3_GENRE[62] = "Pop/Funk"
MP3_GENRE[63] = "Jungle"
MP3_GENRE[64] = "Native American"
MP3_GENRE[65] = "Cabaret"
MP3_GENRE[66] = "New Wave"
MP3_GENRE[67] = "Psychadelic"
MP3_GENRE[68] = "Rave"
MP3_GENRE[69] = "Showtunes"
MP3_GENRE[70] = "Trailer"
MP3_GENRE[71] = "Lo-Fi"
MP3_GENRE[72] = "Tribal"
MP3_GENRE[73] = "Acid Punk"
MP3_GENRE[74] = "Acid Jazz"
MP3_GENRE[75] = "Polka"
MP3_GENRE[76] = "Retro"
MP3_GENRE[77] = "Musical"
MP3_GENRE[78] = "Rock & Roll"
MP3_GENRE[79] = "Hard Rock"

' Winamp extended tags:

MP3_GENRE[80] = "Folk"
MP3_GENRE[81] = "Folk-Rock"
MP3_GENRE[82] = "National Folk"
MP3_GENRE[83] = "Swing"
MP3_GENRE[84] = "Fast Fusion"
MP3_GENRE[85] = "Bebop"
MP3_GENRE[86] = "Latin"
MP3_GENRE[87] = "Revival"
MP3_GENRE[88] = "Celtic"
MP3_GENRE[89] = "Bluegrass"
MP3_GENRE[90] = "Avantgarde"
MP3_GENRE[91] = "Gothic Rock"
MP3_GENRE[92] = "Progressive Rock"
MP3_GENRE[93] = "Psychedelic Rock"
MP3_GENRE[94] = "Symphonic Rock"
MP3_GENRE[95] = "Slow Rock"
MP3_GENRE[96] = "Big Band"
MP3_GENRE[97] = "Chorus"
MP3_GENRE[98] = "Easy Listening"
MP3_GENRE[99] = "Acoustic"
MP3_GENRE[100] = "Humour"
MP3_GENRE[101] = "Speech"
MP3_GENRE[102] = "Chanson"
MP3_GENRE[103] = "Opera"
MP3_GENRE[104] = "Chamber Music"
MP3_GENRE[105] = "Sonata"
MP3_GENRE[106] = "Symphony"
MP3_GENRE[107] = "Booty Bass"
MP3_GENRE[108] = "Primus"
MP3_GENRE[109] = "Porn groove"
MP3_GENRE[110] = "Satire"
MP3_GENRE[111] = "Slow Jam"
MP3_GENRE[112] = "Club"
MP3_GENRE[113] = "Tango"
MP3_GENRE[114] = "Samba"
MP3_GENRE[115] = "Folklore"
MP3_GENRE[116] = "Ballad"
MP3_GENRE[117] = "Power Ballad"
MP3_GENRE[118] = "Rhythmic Soul"
MP3_GENRE[119] = "Freestyle"
MP3_GENRE[120] = "Duet"
MP3_GENRE[121] = "Punk rock"
MP3_GENRE[122] = "Drum Solo"
MP3_GENRE[123] = "A capella"
MP3_GENRE[124] = "Euro-House"
MP3_GENRE[125] = "Dance Hall"
MP3_GENRE[126] = "Goa Trance"
MP3_GENRE[127] = "Drum & Bass"
MP3_GENRE[128] = "Club-House"
MP3_GENRE[129] = "Hardcore Techno"
MP3_GENRE[130] = "Terror"
MP3_GENRE[131] = "Indie"
MP3_GENRE[132] = "BritPop"
MP3_GENRE[133] = "Afro-punk"
MP3_GENRE[134] = "Polsk Punk"
MP3_GENRE[135] = "Beat"
MP3_GENRE[136] = "Christian Gangsta Rap"
MP3_GENRE[137] = "Heavy Metal"
MP3_GENRE[138] = "Black Metal"
MP3_GENRE[139] = "Crossover"
MP3_GENRE[140] = "Contemporary Christian"
MP3_GENRE[141] = "Christian Rock"
MP3_GENRE[142] = "Merengue"
MP3_GENRE[143] = "Salsa"
MP3_GENRE[144] = "Thrash Metal"
MP3_GENRE[145] = "Anime"
MP3_GENRE[146] = "Jpop"
MP3_GENRE[147] = "Synthpop"
MP3_GENRE[148] = "Abstract"
MP3_GENRE[149] = "Art Rock"
MP3_GENRE[150] = "Baroque"
MP3_GENRE[151] = "Bhangra"
MP3_GENRE[152] = "Big Beat"
MP3_GENRE[153] = "Breakbeat"
MP3_GENRE[154] = "Chillout"
MP3_GENRE[155] = "Downtempo"
MP3_GENRE[156] = "Dub"
MP3_GENRE[157] = "EBM"
MP3_GENRE[158] = "Eclectic"
MP3_GENRE[159] = "Electro"
MP3_GENRE[160] = "Electroclash"
MP3_GENRE[161] = "Emo"
MP3_GENRE[162] = "Experimental"
MP3_GENRE[163] = "Garage"
MP3_GENRE[164] = "Global"
MP3_GENRE[165] = "IDM"
MP3_GENRE[166] = "Illbient"
MP3_GENRE[167] = "Industro-Goth"
MP3_GENRE[168] = "Jam Band"
MP3_GENRE[169] = "Krautrock"
MP3_GENRE[170] = "Leftfield"
MP3_GENRE[171] = "Lounge"
MP3_GENRE[172] = "Math Rock"
MP3_GENRE[173] = "New Romantic"
MP3_GENRE[174] = "Nu-Breakz"
MP3_GENRE[175] = "Post-Punk"
MP3_GENRE[176] = "Post-Rock"
MP3_GENRE[177] = "Psytrance"
MP3_GENRE[178] = "Shoegaze"
MP3_GENRE[179] = "Space Rock"
MP3_GENRE[180] = "Trop Rock"
MP3_GENRE[181] = "World Music"
MP3_GENRE[182] = "Neoclassical"
MP3_GENRE[183] = "Audiobook"
MP3_GENRE[184] = "Audio Theatre"
MP3_GENRE[185] = "Neue Deutsche Welle"
MP3_GENRE[186] = "Podcast"
MP3_GENRE[187] = "Indie Rock"
MP3_GENRE[188] = "G-Funk"
MP3_GENRE[189] = "Dubstep"
MP3_GENRE[190] = "Garage Rock"
MP3_GENRE[191] = "Psybient"

Function GetTags:String (f:String)

	Local tag:String
	
	Local mp3:TStream = ReadStream (f)
	
	If mp3
	
		SeekStream mp3, StreamSize (mp3) - 128
		
		If ReadString (mp3, 3) = "TAG"
		
			Local song:String		= Trim (ReadString (mp3, 30))
			Local artist:String		= Trim (ReadString (mp3, 30))
			Local album:String		= Trim (ReadString (mp3, 30))
			Local year:String		= Trim (ReadString (mp3, 4))
			Local comment:String	= Trim (ReadString (mp3, 30))

			Local genre:String
			
			Local readgenre:Int = ReadByte (mp3)

			' Safety check...
			
			If readgenre > -1 And readgenre < 192
				genre = MP3_GENRE [readgenre]
			Else
				genre = "Unknown"
			EndIf
		
			' Returning song and artist, if available (just add the
			' other strings above if you want more). Strings available:
			
			' song
			' artist
			' album
			' year
			' comment
			' genre
			
			If song
				tag = song
				If artist Then tag = tag + " by " + artist
			EndIf
			
		EndIf
		
		CloseStream mp3
		
	EndIf

	' No tag info? Return filename without extension...
		
	If Not tag Then tag = Trim (StripExt (f))

	Return tag
	
End Function

' -------------------------------------------------------
' Demo - stick some mp3s in same folder as source/exe...
' -------------------------------------------------------

Local files:String[]

files = LoadDir (CurrentDir ())

For Local t:String = EachIn files
	If Lower (ExtractExt (t)) = "mp3"
		Print GetTags (t)
	EndIf
Next
