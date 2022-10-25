; ID: 2771
; Author: Dabhand
; Date: 2010-09-20 16:25:43
; Title: osx file attributes
; Description: Find common attributes for files. (BMX)

//findmeta.m
#include <sys/stat.h>

char * FindMeta(char *filepath)
{
        struct stat fst;
        bzero(&fst,sizeof(fst));

        if (stat(filepath,&fst) != 0) { return("");}
        
        return(ctime(&fst.st_atime));
}


'Blitzmax example

Import "findMeta.m"

Extern "C"
Function FindMeta$z(filepath:Byte Ptr)
End Extern

Print FindMeta(CurrentDir$()+"/test.app")
