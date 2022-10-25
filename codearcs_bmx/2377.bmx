; ID: 2377
; Author: Otus
; Date: 2008-12-21 12:53:13
; Title: LZMA Compression
; Description: LZMA compression for BlitzMax

'lzma.bmx
SuperStrict

Rem
bbdoc: Lzma compression
End Rem
Rem
Module Otus.Lzma

ModuleInfo "Version: 1.0"
ModuleInfo "Author: Igor Pavlov (7-zip.org)"
ModuleInfo "License: Public domain"
ModuleInfo "Credit: BlitzMax interface by Jan Varho"
ModuleInfo "History: 1.01 Release"
ModuleInfo "History: Fixed interface to exactly match zlib"
ModuleInfo "History: Removed redundant wrapper"
ModuleInfo "History: Upgraded SDK to 4.65"
End Rem
Import "LzmaEnc.c"
Import "lzmasdk/LzmaUtil/Lzma86Dec.c"
Import "lzmasdk/Alloc.c"
Import "lzmasdk/Bra86.c"
Import "lzmasdk/LzmaEnc.c"
Import "lzmasdk/LzmaDec.c"
Import "lzmasdk/LzFind.c"

Extern

Rem
bbdoc: Uncompress a block of data
End Rem
Function LzmaUncompress( dest:Byte Ptr, destLen:Int Var, src:Byte Ptr, srcLen:Int Var ) = "Lzma86_Decode"

Rem
bbdoc: Compress at the compression level given using a specified dictionary size
about:
Compression level should be in the range 1-9 with 9 the maximum compression.

Valid dictionary sizes are between 2^12 and 2^27 bytes. A power of two is recommended.
The default (used in LzmaCompress and LzmaComress2) is 2^24 bytes (16 MB).
End Rem
Function LzmaCompress3( dest:Byte Ptr, destLen:Int Var, src:Byte Ptr, srcLen:Int, level:Int, dictSize:Int = LZMA_DICT_SIZE ) = "_LzmaCompress"

End Extern

' Dictionary size in bytes (16MB)
Const LZMA_DICT_SIZE:Int = $1000000

Rem
bbdoc: Compress a block of data at default compression level
end rem
Function LzmaCompress( dest:Byte Ptr, destLen:Int Var, src:Byte Ptr, srcLen:Int )
	LzmaCompress3( dest, destLen, src, srcLen, 5, LZMA_DICT_SIZE )
End Function

Rem
bbdoc: Compress a block of data at the compression level given
about:
Compression level should be in the range 1-9 with 9 the maximum compression.
End Rem
Function LzmaCompress2( dest:Byte Ptr, destLen:Int Var, src:Byte Ptr, srcLen:Int, level:Int )
	LzmaCompress3( dest, destLen, src, srcLen, level, LZMA_DICT_SIZE )
End Function

// LzmaEnc.c
// Wrapper for the Encode function without filtering

#include "lzmasdk/LzmaUtil/Lzma86Enc.c"

void _LzmaCompress( Byte *dest, size_t *destLen, const Byte *src, size_t srcLen,
    int level, UInt32 dictSize )
{
	Lzma86_Encode( dest, destLen, src, srcLen, level, dictSize, SZ_FILTER_NO );
}
