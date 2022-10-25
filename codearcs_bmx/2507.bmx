; ID: 2507
; Author: Nilium
; Date: 2009-06-14 01:18:16
; Title: Create a string by repeating a string
; Description: Black magic (C) to create a string by repeating another string

// repeatstring.c
#include <brl.mod/blitz.mod/blitz.h>

BBString *StringByRepeatingString(BBString const *str, int const length) {
	BBString *repString = bbStringNew(length);
	BBChar *buf = repString->buf;
	unsigned int idx = 0;
	if ( str == &bbEmptyString )
	{
		for (; idx < length; ++idx)
			buf[idx]=L' ';
	}
	else if ( str->length == 1 )
	{
		BBChar character = str->buf[0];
		for (; idx < length; ++idx)
			buf[idx] = character;
	}
	else
	{
		int slen = str->length;
		BBChar const *inpBuf = str->buf;
		for (; idx < length; ++idx)
			buf[idx] = inpBuf[idx%slen];
	}
	return repString;
}


' BlitzMax
Import "repeatstring.c"

Extern "C"
    Function StringByRepeatingString:String(str:String, length%)
End Extern
