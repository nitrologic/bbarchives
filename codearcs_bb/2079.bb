; ID: 2079
; Author: ZJP
; Date: 2007-07-25 09:27:11
; Title: Blitz3dSDK - C/C++ - Physx's Rubux Wrapper FINAL Part 3/3  - End
; Description: How to use Physx's Rubux  wrapper and B3DSDK in C/C++ Project

// Final definition

typedef int(WINAPI *DLL_pxGetNumberAllBody)();
DLL_pxGetNumberAllBody pxGetNumberAllBody;
pxGetNumberAllBody = (DLL_pxGetNumberAllBody)GetProcAddress(hDLL,"_pxGetNumberAllBody@0");
// Use ==> int result = pxGetNumberAllBody();
 
typedef int(WINAPI *DLL_pxCreateWorld)(int plane, char *password);
DLL_pxCreateWorld pxCreateWorld;
pxCreateWorld = (DLL_pxCreateWorld)GetProcAddress(hDLL,"_pxCreateWorld@8");
// Use ==> int result = pxCreateWorld(int plane, char *password);
 
typedef void(WINAPI *DLL_pxDestroyWorld)();
DLL_pxDestroyWorld pxDestroyWorld;
pxDestroyWorld = (DLL_pxDestroyWorld)GetProcAddress(hDLL,"_pxDestroyWorld@0");
// Use ==> pxDestroyWorld();
 
typedef void(WINAPI *DLL_pxRenderPhysic)(float time, int sinc);
DLL_pxRenderPhysic pxRenderPhysic;
pxRenderPhysic = (DLL_pxRenderPhysic)GetProcAddress(hDLL,"_pxRenderPhysic@8");
// Use ==> pxRenderPhysic(float time, int sinc);
 
typedef void(WINAPI *DLL_pxSetTiming)(float maxTimeStep, int maxIter, int StepMethod);
DLL_pxSetTiming pxSetTiming;
pxSetTiming = (DLL_pxSetTiming)GetProcAddress(hDLL,"_pxSetTiming@12");
// Use ==> pxSetTiming(float maxTimeStep, int maxIter, int StepMethod);
 
typedef void(WINAPI *DLL_pxSDKSetParameter)(float param);
DLL_pxSDKSetParameter pxSDKSetParameter;
pxSDKSetParameter = (DLL_pxSDKSetParameter)GetProcAddress(hDLL,"_pxSDKSetParameter@4");
// Use ==> pxSDKSetParameter(float param);
 
typedef void(WINAPI *DLL_pxSetPause)(int pause);
DLL_pxSetPause pxSetPause;
pxSetPause = (DLL_pxSetPause)GetProcAddress(hDLL,"_pxSetPause@4");
// Use ==> pxSetPause(int pause);
 
typedef int(WINAPI *DLL_pxChekPPU)();
DLL_pxChekPPU pxChekPPU;
pxChekPPU = (DLL_pxChekPPU)GetProcAddress(hDLL,"_pxChekPPU@0");
// Use ==> int result = pxChekPPU();
 
typedef int(WINAPI *DLL_pxChekPPUMode)();
DLL_pxChekPPUMode pxChekPPUMode;
pxChekPPUMode = (DLL_pxChekPPUMode)GetProcAddress(hDLL,"_pxChekPPUMode@0");
// Use ==> int result = pxChekPPUMode();
 
typedef void(WINAPI *DLL_pxSetHardwareSimulation)(int mode);
DLL_pxSetHardwareSimulation pxSetHardwareSimulation;
pxSetHardwareSimulation = (DLL_pxSetHardwareSimulation)GetProcAddress(hDLL,"_pxSetHardwareSimulation@4");
// Use ==> pxSetHardwareSimulation(int mode);
 
typedef int(WINAPI *DLL_pxCreateScene)();
DLL_pxCreateScene pxCreateScene;
pxCreateScene = (DLL_pxCreateScene)GetProcAddress(hDLL,"_pxCreateScene@0");
// Use ==> int result = pxCreateScene();
 
typedef void(WINAPI *DLL_pxSceneChange)();
DLL_pxSceneChange pxSceneChange;
pxSceneChange = (DLL_pxSceneChange)GetProcAddress(hDLL,"_pxSceneChange@4");
// Use ==> pxSceneChange();
 
typedef int(WINAPI *DLL_pxBodySetEntity)(int entity, int body);
DLL_pxBodySetEntity pxBodySetEntity;
pxBodySetEntity = (DLL_pxBodySetEntity)GetProcAddress(hDLL,"_pxBodySetEntity@8");
// Use ==> int result = pxBodySetEntity(int entity, int body);
 
typedef void(WINAPI *DLL_pxBodyAddEntity)(int entity, int body);
DLL_pxBodyAddEntity pxBodyAddEntity;
pxBodyAddEntity = (DLL_pxBodyAddEntity)GetProcAddress(hDLL,"_pxBodyAddEntity@8");
// Use ==> pxBodyAddEntity(int entity, int body);
 
typedef float(WINAPI *DLL_pxGetAngleBetweenVec)(float v1x, float v1y, float v1z, float v2x, float v2y, float v2z);
DLL_pxGetAngleBetweenVec pxGetAngleBetweenVec;
pxGetAngleBetweenVec = (DLL_pxGetAngleBetweenVec)GetProcAddress(hDLL,"_pxGetAngleBetweenVec@24");
// Use ==> float result = pxGetAngleBetweenVec(float v1x, float v1y, float v1z, float v2x, float v2y, float v2z);

// End
