; ID: 2078
; Author: ZJP
; Date: 2007-07-25 09:23:46
; Title: Blitz3dSDK - C/C++ - Physx's Rubux Wrapper Part 2
; Description: How to use Physx's Rubux  wrapper and B3DSDK in C/C++ Project

// Part 2
typedef float(WINAPI *DLL_pxWheelGetContactLonDirectionX)(int wheel);
DLL_pxWheelGetContactLonDirectionX pxWheelGetContactLonDirectionX;
pxWheelGetContactLonDirectionX = (DLL_pxWheelGetContactLonDirectionX)GetProcAddress(hDLL,"_pxWheelGetContactLonDirectionX@4");
// Use ==> float result = pxWheelGetContactLonDirectionX(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLonDirectionY)(int wheel);
DLL_pxWheelGetContactLonDirectionY pxWheelGetContactLonDirectionY;
pxWheelGetContactLonDirectionY = (DLL_pxWheelGetContactLonDirectionY)GetProcAddress(hDLL,"_pxWheelGetContactLonDirectionY@4");
// Use ==> float result = pxWheelGetContactLonDirectionY(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLonDirectionZ)(int wheel);
DLL_pxWheelGetContactLonDirectionZ pxWheelGetContactLonDirectionZ;
pxWheelGetContactLonDirectionZ = (DLL_pxWheelGetContactLonDirectionZ)GetProcAddress(hDLL,"_pxWheelGetContactLonDirectionZ@4");
// Use ==> float result = pxWheelGetContactLonDirectionZ(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetContactLonImpulse)(int wheel);
DLL_pxWheelGetContactLonImpulse pxWheelGetContactLonImpulse;
pxWheelGetContactLonImpulse = (DLL_pxWheelGetContactLonImpulse)GetProcAddress(hDLL,"_pxWheelGetContactLonImpulse@4");
// Use ==> float result = pxWheelGetContactLonImpulse(int wheel);
 
typedef int(WINAPI *DLL_pxWheelGetContactMaterial)(int wheel);
DLL_pxWheelGetContactMaterial pxWheelGetContactMaterial;
pxWheelGetContactMaterial = (DLL_pxWheelGetContactMaterial)GetProcAddress(hDLL,"_pxWheelGetContactMaterial@4");
// Use ==> int result = pxWheelGetContactMaterial(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetFrictionToSide)(int wheel);
DLL_pxWheelGetFrictionToSide pxWheelGetFrictionToSide;
pxWheelGetFrictionToSide = (DLL_pxWheelGetFrictionToSide)GetProcAddress(hDLL,"_pxWheelGetFrictionToSide@4");
// Use ==> float result = pxWheelGetFrictionToSide(int wheel);
 
typedef float(WINAPI *DLL_pxWheelGetFrictionToFront)(int wheel);
DLL_pxWheelGetFrictionToFront pxWheelGetFrictionToFront;
pxWheelGetFrictionToFront = (DLL_pxWheelGetFrictionToFront)GetProcAddress(hDLL,"_pxWheelGetFrictionToSide@4");
// Use ==> float result = pxWheelGetFrictionToFront(int wheel);
 
typedef void(WINAPI *DLL_pxWheelSetMask)(int wheel, int mask);
DLL_pxWheelSetMask pxWheelSetMask;
pxWheelSetMask = (DLL_pxWheelSetMask)GetProcAddress(hDLL,"_pxWheelSetMask@8");
// Use ==> pxWheelSetMask(int wheel, int mask);
 
typedef void(WINAPI *DLL_pxWheelSetMaskCombine)(int wheel, int mask);
DLL_pxWheelSetMaskCombine pxWheelSetMaskCombine;
pxWheelSetMaskCombine = (DLL_pxWheelSetMaskCombine)GetProcAddress(hDLL,"_pxWheelSetMaskCombine@8");
// Use ==> pxWheelSetMaskCombine(int wheel, int mask);
 
typedef void(WINAPI *DLL_pxWheelClearMask)(int wheel);
DLL_pxWheelClearMask pxWheelClearMask;
pxWheelClearMask = (DLL_pxWheelClearMask)GetProcAddress(hDLL,"_pxWheelClearMask@4");
// Use ==> pxWheelClearMask(int wheel);
 
typedef void(WINAPI *DLL_pxWheelDelete)(int wheel);
DLL_pxWheelDelete pxWheelDelete;
pxWheelDelete = (DLL_pxWheelDelete)GetProcAddress(hDLL,"_pxWheelDelete@4");
// Use ==> pxWheelDelete(int wheel);
 
typedef float(WINAPI *DLL_pxWheelSetEntity)(int entity, int wheel);
DLL_pxWheelSetEntity pxWheelSetEntity;
pxWheelSetEntity = (DLL_pxWheelSetEntity)GetProcAddress(hDLL,"_pxWheelSetEntity@8");
// Use ==> float result = pxWheelSetEntity(int entity, int wheel);
 
typedef int(WINAPI *DLL_pxJointCreateSuspFront)(int body0, int body1, float x, float y, float z);
DLL_pxJointCreateSuspFront pxJointCreateSuspFront;
pxJointCreateSuspFront = (DLL_pxJointCreateSuspFront)GetProcAddress(hDLL,"_pxJointCreateSuspFront@20");
// Use ==> int result = pxJointCreateSuspFront(int body0, int body1, float x, float y, float z);
 
typedef int(WINAPI *DLL_pxJointCreateSuspBack)(int body0, int body1, float x, float y, float z);
DLL_pxJointCreateSuspBack pxJointCreateSuspBack;
pxJointCreateSuspBack = (DLL_pxJointCreateSuspBack)GetProcAddress(hDLL,"_pxJointCreateSuspBack@20");
// Use ==> int result = pxJointCreateSuspBack(int body0, int body1, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxJointSuspSetSteerN)(int joint, float x, float y, float z);
DLL_pxJointSuspSetSteerN pxJointSuspSetSteerN;
pxJointSuspSetSteerN = (DLL_pxJointSuspSetSteerN)GetProcAddress(hDLL,"_pxJointSuspSetSteerN@16");
// Use ==> pxJointSuspSetSteerN(int joint, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxJointSuspSetTurnN)(int joint, float x, float y, float z);
DLL_pxJointSuspSetTurnN pxJointSuspSetTurnN;
pxJointSuspSetTurnN = (DLL_pxJointSuspSetTurnN)GetProcAddress(hDLL,"_pxJointSuspSetTurnN@16");
// Use ==> pxJointSuspSetTurnN(int joint, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxJointSuspSetLinLimit)(int joint, float lim);
DLL_pxJointSuspSetLinLimit pxJointSuspSetLinLimit;
pxJointSuspSetLinLimit = (DLL_pxJointSuspSetLinLimit)GetProcAddress(hDLL,"_pxJointSuspSetLinLimit@8");
// Use ==> pxJointSuspSetLinLimit(int joint, float lim);
 
typedef void(WINAPI *DLL_pxJointSuspSetLinParameter)(int joint, float spring, float rest, float damp);
DLL_pxJointSuspSetLinParameter pxJointSuspSetLinParameter;
pxJointSuspSetLinParameter = (DLL_pxJointSuspSetLinParameter)GetProcAddress(hDLL,"_pxJointSuspSetAngParameter@16");
// Use ==> pxJointSuspSetLinParameter(int joint, float spring, float rest, float damp);
 
typedef void(WINAPI *DLL_pxJointSuspSetAngLimit)(int joint, float lim);
DLL_pxJointSuspSetAngLimit pxJointSuspSetAngLimit;
pxJointSuspSetAngLimit = (DLL_pxJointSuspSetAngLimit)GetProcAddress(hDLL,"_pxJointSuspSetAngLimit@8");
// Use ==> pxJointSuspSetAngLimit(int joint, float lim);
 
typedef void(WINAPI *DLL_pxJointSuspSetAngParameter)(int joint, float spring, float rest, float damp);
DLL_pxJointSuspSetAngParameter pxJointSuspSetAngParameter;
pxJointSuspSetAngParameter = (DLL_pxJointSuspSetAngParameter)GetProcAddress(hDLL,"_pxJointSuspSetAngParameter@16");
// Use ==> pxJointSuspSetAngParameter(int joint, float spring, float rest, float damp);
 
typedef void(WINAPI *DLL_pxJointSuspSetAngle)(int joint, float angle);
DLL_pxJointSuspSetAngle pxJointSuspSetAngle;
pxJointSuspSetAngle = (DLL_pxJointSuspSetAngle)GetProcAddress(hDLL,"_pxJointSuspSetAngle@8");
// Use ==> pxJointSuspSetAngle(int joint, float angle);
 
typedef void(WINAPI *DLL_pxJointSuspSetSpeed)(int joint, float speed);
DLL_pxJointSuspSetSpeed pxJointSuspSetSpeed;
pxJointSuspSetSpeed = (DLL_pxJointSuspSetSpeed)GetProcAddress(hDLL,"_pxJointSuspSetSpeed@8");
// Use ==> pxJointSuspSetSpeed(int joint, float speed);
 
typedef void(WINAPI *DLL_pxJointSuspSetBrake)(int joint, int mode);
DLL_pxJointSuspSetBrake pxJointSuspSetBrake;
pxJointSuspSetBrake = (DLL_pxJointSuspSetBrake)GetProcAddress(hDLL,"_pxJointSuspSetBrake@8");
// Use ==> pxJointSuspSetBrake(int joint, int mode);
 
typedef void(WINAPI *DLL_pxCCDSkeletonEnable)(int mode);
DLL_pxCCDSkeletonEnable pxCCDSkeletonEnable;
pxCCDSkeletonEnable = (DLL_pxCCDSkeletonEnable)GetProcAddress(hDLL,"_pxCCDSkeletonEnable@4");
// Use ==> pxCCDSkeletonEnable(int mode);
 
typedef void(WINAPI *DLL_pxCCDSkeletonSetEpsilon)(float eps);
DLL_pxCCDSkeletonSetEpsilon pxCCDSkeletonSetEpsilon;
pxCCDSkeletonSetEpsilon = (DLL_pxCCDSkeletonSetEpsilon)GetProcAddress(hDLL,"_pxCCDSkeletonSetEpsilon@4");
// Use ==> pxCCDSkeletonSetEpsilon(float eps);
 
typedef void(WINAPI *DLL_pxBodySetCCDSkeleton)(int body, float x, float y, float z);
DLL_pxBodySetCCDSkeleton pxBodySetCCDSkeleton;
pxBodySetCCDSkeleton = (DLL_pxBodySetCCDSkeleton)GetProcAddress(hDLL,"_pxBodySetCCDSkeleton@16");
// Use ==> pxBodySetCCDSkeleton(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodySetCCDSkeletonThreshold)(int body, float thres);
DLL_pxBodySetCCDSkeletonThreshold pxBodySetCCDSkeletonThreshold;
pxBodySetCCDSkeletonThreshold = (DLL_pxBodySetCCDSkeletonThreshold)GetProcAddress(hDLL,"_pxBodySetCCDSkeletonThreshold@8");
// Use ==> pxBodySetCCDSkeletonThreshold(int body, float thres);
 
typedef void(WINAPI *DLL_pxBodySetFlagCCDSkeletonDynamic)(int body);
DLL_pxBodySetFlagCCDSkeletonDynamic pxBodySetFlagCCDSkeletonDynamic;
pxBodySetFlagCCDSkeletonDynamic = (DLL_pxBodySetFlagCCDSkeletonDynamic)GetProcAddress(hDLL,"_pxBodySetFlagCCDSkeletonDynamic@4");
// Use ==> pxBodySetFlagCCDSkeletonDynamic(int body);
 
typedef int(WINAPI *DLL_pxBodyGetCCDSkeleton)(int body);
DLL_pxBodyGetCCDSkeleton pxBodyGetCCDSkeleton;
pxBodyGetCCDSkeleton = (DLL_pxBodyGetCCDSkeleton)GetProcAddress(hDLL,"_pxBodyGetCCDSkeleton@4");
// Use ==> int result = pxBodyGetCCDSkeleton(int body);
 
typedef void(WINAPI *DLL_pxBodyDeleteCCDSkeleton)(int ccd);
DLL_pxBodyDeleteCCDSkeleton pxBodyDeleteCCDSkeleton;
pxBodyDeleteCCDSkeleton = (DLL_pxBodyDeleteCCDSkeleton)GetProcAddress(hDLL,"_pxBodyDeleteCCDSkeleton@4");
// Use ==> pxBodyDeleteCCDSkeleton(int ccd);
 
typedef int(WINAPI *DLL_pxCreateRay)();
DLL_pxCreateRay pxCreateRay;
pxCreateRay = (DLL_pxCreateRay)GetProcAddress(hDLL,"_pxCreateRay@0");
// Use ==> int result = pxCreateRay();
 
typedef void(WINAPI *DLL_pxRaySetDir)(int ray, float nx, float ny, float nz);
DLL_pxRaySetDir pxRaySetDir;
pxRaySetDir = (DLL_pxRaySetDir)GetProcAddress(hDLL,"_pxRaySetDir@16");
// Use ==> pxRaySetDir(int ray, float nx, float ny, float nz);
 
typedef void(WINAPI *DLL_pxRaySetPosition)(int ray, float x, float y, float z);
DLL_pxRaySetPosition pxRaySetPosition;
pxRaySetPosition = (DLL_pxRaySetPosition)GetProcAddress(hDLL,"_pxRaySetPosition@16");
// Use ==> pxRaySetPosition(int ray, float x, float y, float z);
 
typedef float(WINAPI *DLL_pxRayGetDistance)(int ray, int mode);
DLL_pxRayGetDistance pxRayGetDistance;
pxRayGetDistance = (DLL_pxRayGetDistance)GetProcAddress(hDLL,"_pxRayGetDistance@8");
// Use ==> float result = pxRayGetDistance(int ray, int mode);
 
typedef int(WINAPI *DLL_pxRayGetBody)(int ray, int mode);
DLL_pxRayGetBody pxRayGetBody;
pxRayGetBody = (DLL_pxRayGetBody)GetProcAddress(hDLL,"_pxRayGetBody@8");
// Use ==> int result = pxRayGetBody(int ray, int mode);
 
typedef int(WINAPI *DLL_pxRayGetMaterial)(int ray, int mode);
DLL_pxRayGetMaterial pxRayGetMaterial;
pxRayGetMaterial = (DLL_pxRayGetMaterial)GetProcAddress(hDLL,"_pxRayGetMaterial@8");
// Use ==> int result = pxRayGetMaterial(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickX)(int ray, int mode);
DLL_pxRayGetPickX pxRayGetPickX;
pxRayGetPickX = (DLL_pxRayGetPickX)GetProcAddress(hDLL,"_pxRayGetPickX@8");
// Use ==> float result = pxRayGetPickX(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickY)(int ray, int mode);
DLL_pxRayGetPickY pxRayGetPickY;
pxRayGetPickY = (DLL_pxRayGetPickY)GetProcAddress(hDLL,"_pxRayGetPickY@8");
// Use ==> float result = pxRayGetPickY(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickZ)(int ray, int mode);
DLL_pxRayGetPickZ pxRayGetPickZ;
pxRayGetPickZ = (DLL_pxRayGetPickZ)GetProcAddress(hDLL,"_pxRayGetPickZ@8");
// Use ==> float result = pxRayGetPickZ(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickNX)(int ray, int mode);
DLL_pxRayGetPickNX pxRayGetPickNX;
pxRayGetPickNX = (DLL_pxRayGetPickNX)GetProcAddress(hDLL,"_pxRayGetPickNX@8");
// Use ==> float result = pxRayGetPickNX(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickNY)(int ray, int mode);
DLL_pxRayGetPickNY pxRayGetPickNY;
pxRayGetPickNY = (DLL_pxRayGetPickNY)GetProcAddress(hDLL,"_pxRayGetPickNY@8");
// Use ==> float result = pxRayGetPickNY(int ray, int mode);
 
typedef float(WINAPI *DLL_pxRayGetPickNZ)(int ray, int mode);
DLL_pxRayGetPickNZ pxRayGetPickNZ;
pxRayGetPickNZ = (DLL_pxRayGetPickNZ)GetProcAddress(hDLL,"_pxRayGetPickNZ@8");
// Use ==> float result = pxRayGetPickNZ(int ray, int mode);
 
typedef int(WINAPI *DLL_pxRegWriteDriverPath)(char *str);
DLL_pxRegWriteDriverPath pxRegWriteDriverPath;
pxRegWriteDriverPath = (DLL_pxRegWriteDriverPath)GetProcAddress(hDLL,"_pxRegWriteDriverPath@4");
// Use ==> int result = pxRegWriteDriverPath(char *str);
 
typedef int(WINAPI *DLL_pxRegWriteString)(int RootKey, char *Path, char *Name, char *Data);
DLL_pxRegWriteString pxRegWriteString;
pxRegWriteString = (DLL_pxRegWriteString)GetProcAddress(hDLL,"_pxRegWriteString@16");
// Use ==> int result = pxRegWriteString(int RootKey, char *Path, char *Name, char *Data);
 
typedef int(WINAPI *DLL_pxRegWriteInt)(int RootKey, char *Path, char *Name, int Data);
DLL_pxRegWriteInt pxRegWriteInt;
pxRegWriteInt = (DLL_pxRegWriteInt)GetProcAddress(hDLL,"_pxRegWriteInt@16");
// Use ==> int result = pxRegWriteInt(int RootKey, char *Path, char *Name, int Data);
 
typedef int(WINAPI *DLL_pxRegReadInt)(int RootKey, char *Path, char *Name);
DLL_pxRegReadInt pxRegReadInt;
pxRegReadInt = (DLL_pxRegReadInt)GetProcAddress(hDLL,"_pxRegReadInt@12");
// Use ==> int result = pxRegReadInt(int RootKey, char *Path, char *Name);
 
typedef char*(WINAPI *DLL_pxRegReadString)(int RootKey, char *Path, char *Name);
DLL_pxRegReadString pxRegReadString;
pxRegReadString = (DLL_pxRegReadString)GetProcAddress(hDLL,"_pxRegReadString@12");
// Use ==> char *result = pxRegReadString(int RootKey, char *Path, char *Name);
 
typedef int(WINAPI *DLL_pxRegDeleteValue)(int RootKey, char *Path, char *Name);
DLL_pxRegDeleteValue pxRegDeleteValue;
pxRegDeleteValue = (DLL_pxRegDeleteValue)GetProcAddress(hDLL,"_pxRegDeleteValue@12");
// Use ==> int result = pxRegDeleteValue(int RootKey, char *Path, char *Name);
 
typedef int(WINAPI *DLL_pxRegDeleteKey)(int RootKey, char *Path, char *Name);
DLL_pxRegDeleteKey pxRegDeleteKey;
pxRegDeleteKey = (DLL_pxRegDeleteKey)GetProcAddress(hDLL,"_pxRegDeleteKey@12");
// Use ==> int result = pxRegDeleteKey(int RootKey, char *Path, char *Name);
 
typedef int(WINAPI *DLL_pxGetContacts)(int body);
DLL_pxGetContacts pxGetContacts;
pxGetContacts = (DLL_pxGetContacts)GetProcAddress(hDLL,"_pxGetContacts@4");
// Use ==> int result = pxGetContacts(int body);
 
typedef int(WINAPI *DLL_pxContactGetBody)(int body, int coll);
DLL_pxContactGetBody pxContactGetBody;
pxContactGetBody = (DLL_pxContactGetBody)GetProcAddress(hDLL,"_pxContactGetBody@8");
// Use ==> int result = pxContactGetBody(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointX)(int body, int coll);
DLL_pxContactGetPointX pxContactGetPointX;
pxContactGetPointX = (DLL_pxContactGetPointX)GetProcAddress(hDLL,"_pxContactGetPointX@8");
// Use ==> float result = pxContactGetPointX(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointY)(int body, int coll);
DLL_pxContactGetPointY pxContactGetPointY;
pxContactGetPointY = (DLL_pxContactGetPointY)GetProcAddress(hDLL,"_pxContactGetPointY@8");
// Use ==> float result = pxContactGetPointY(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointZ)(int body, int coll);
DLL_pxContactGetPointZ pxContactGetPointZ;
pxContactGetPointZ = (DLL_pxContactGetPointZ)GetProcAddress(hDLL,"_pxContactGetPointZ@8");
// Use ==> float result = pxContactGetPointZ(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointNX)(int body, int coll);
DLL_pxContactGetPointNX pxContactGetPointNX;
pxContactGetPointNX = (DLL_pxContactGetPointNX)GetProcAddress(hDLL,"_pxContactGetPointNX@8");
// Use ==> float result = pxContactGetPointNX(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointNY)(int body, int coll);
DLL_pxContactGetPointNY pxContactGetPointNY;
pxContactGetPointNY = (DLL_pxContactGetPointNY)GetProcAddress(hDLL,"_pxContactGetPointNY@8");
// Use ==> float result = pxContactGetPointNY(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetPointNZ)(int body, int coll);
DLL_pxContactGetPointNZ pxContactGetPointNZ;
pxContactGetPointNZ = (DLL_pxContactGetPointNZ)GetProcAddress(hDLL,"_pxContactGetPointNZ@8");
// Use ==> float result = pxContactGetPointNZ(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceN)(int body, int coll);
DLL_pxContactGetForceN pxContactGetForceN;
pxContactGetForceN = (DLL_pxContactGetForceN)GetProcAddress(hDLL,"_pxContactGetForceN@8");
// Use ==> float result = pxContactGetForceN(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceNX)(int body, int coll);
DLL_pxContactGetForceNX pxContactGetForceNX;
pxContactGetForceNX = (DLL_pxContactGetForceNX)GetProcAddress(hDLL,"_pxContactGetForceNX@8");
// Use ==> float result = pxContactGetForceNX(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceNY)(int body, int coll);
DLL_pxContactGetForceNY pxContactGetForceNY;
pxContactGetForceNY = (DLL_pxContactGetForceNY)GetProcAddress(hDLL,"_pxContactGetForceNY@8");
// Use ==> float result = pxContactGetForceNY(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceNZ)(int body, int coll);
DLL_pxContactGetForceNZ pxContactGetForceNZ;
pxContactGetForceNZ = (DLL_pxContactGetForceNZ)GetProcAddress(hDLL,"_pxContactGetForceNZ@8");
// Use ==> float result = pxContactGetForceNZ(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceT)(int body, int coll);
DLL_pxContactGetForceT pxContactGetForceT;
pxContactGetForceT = (DLL_pxContactGetForceT)GetProcAddress(hDLL,"_pxContactGetForceT@8");
// Use ==> float result = pxContactGetForceT(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceTX)(int body, int coll);
DLL_pxContactGetForceTX pxContactGetForceTX;
pxContactGetForceTX = (DLL_pxContactGetForceTX)GetProcAddress(hDLL,"_pxContactGetForceTX@8");
// Use ==> float result = pxContactGetForceTX(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceTY)(int body, int coll);
DLL_pxContactGetForceTY pxContactGetForceTY;
pxContactGetForceTY = (DLL_pxContactGetForceTY)GetProcAddress(hDLL,"_pxContactGetForceTY@8");
// Use ==> float result = pxContactGetForceTY(int body, int coll);
 
typedef float(WINAPI *DLL_pxContactGetForceTZ)(int body, int coll);
DLL_pxContactGetForceTZ pxContactGetForceTZ;
pxContactGetForceTZ = (DLL_pxContactGetForceTZ)GetProcAddress(hDLL,"_pxContactGetForceTZ@8");
// Use ==> float result = pxContactGetForceTZ(int body, int coll);
 
typedef int(WINAPI *DLL_pxContactEventsOnStartTouch)(int body);
DLL_pxContactEventsOnStartTouch pxContactEventsOnStartTouch;
pxContactEventsOnStartTouch = (DLL_pxContactEventsOnStartTouch)GetProcAddress(hDLL,"_pxContactEventsOnStartTouch@4");
// Use ==> int result = pxContactEventsOnStartTouch(int body);
 
typedef int(WINAPI *DLL_pxContactEventsOnEndTouch)(int body);
DLL_pxContactEventsOnEndTouch pxContactEventsOnEndTouch;
pxContactEventsOnEndTouch = (DLL_pxContactEventsOnEndTouch)GetProcAddress(hDLL,"_pxContactEventsOnEndTouch@4");
// Use ==> int result = pxContactEventsOnEndTouch(int body);
 
typedef void(WINAPI *DLL_pxMaskSet)(int body, int mask);
DLL_pxMaskSet pxMaskSet;
pxMaskSet = (DLL_pxMaskSet)GetProcAddress(hDLL,"_pxMaskSet@8");
// Use ==> pxMaskSet(int body, int mask);
 
typedef void(WINAPI *DLL_pxMaskCombineSet)(int body, int mask);
DLL_pxMaskCombineSet pxMaskCombineSet;
pxMaskCombineSet = (DLL_pxMaskCombineSet)GetProcAddress(hDLL,"_pxMaskCombineSet@8");
// Use ==> pxMaskCombineSet(int body, int mask);
 
typedef void(WINAPI *DLL_pxMaskClear)(int body);
DLL_pxMaskClear pxMaskClear;
pxMaskClear = (DLL_pxMaskClear)GetProcAddress(hDLL,"_pxMaskClear@4");
// Use ==> pxMaskClear(int body);
 
typedef void(WINAPI *DLL_pxBodySetCollisionGroup)(int body, int group);
DLL_pxBodySetCollisionGroup pxBodySetCollisionGroup;
pxBodySetCollisionGroup = (DLL_pxBodySetCollisionGroup)GetProcAddress(hDLL,"_pxBodySetCollisionGroup@8");
// Use ==> pxBodySetCollisionGroup(int body, int group);
 
typedef void(WINAPI *DLL_pxBodySetCollisionGroupPair)(int group1, int group2);
DLL_pxBodySetCollisionGroupPair pxBodySetCollisionGroupPair;
pxBodySetCollisionGroupPair = (DLL_pxBodySetCollisionGroupPair)GetProcAddress(hDLL,"_pxBodySetCollisionGroupPair@8");
// Use ==> pxBodySetCollisionGroupPair(int group1, int group2);
 
typedef void(WINAPI *DLL_pxBodySetCollisionGroupFlag)(int group1, int group2, int flag);
DLL_pxBodySetCollisionGroupFlag pxBodySetCollisionGroupFlag;
pxBodySetCollisionGroupFlag = (DLL_pxBodySetCollisionGroupFlag)GetProcAddress(hDLL,"_pxBodySetCollisionGroupFlag@12");
// Use ==> pxBodySetCollisionGroupFlag(int group1, int group2, int flag);
 
typedef void(WINAPI *DLL_pxBodySetMagnetMask)(int body, int mask);
DLL_pxBodySetMagnetMask pxBodySetMagnetMask;
pxBodySetMagnetMask = (DLL_pxBodySetMagnetMask)GetProcAddress(hDLL,"_pxBodySetMagnetMask@8");
// Use ==> pxBodySetMagnetMask(int body, int mask);
 
typedef int(WINAPI *DLL_pxBodyGetMagnetMask)(int body);
DLL_pxBodyGetMagnetMask pxBodyGetMagnetMask;
pxBodyGetMagnetMask = (DLL_pxBodyGetMagnetMask)GetProcAddress(hDLL,"_pxBodyGetMagnetMask@4");
// Use ==> int result = pxBodyGetMagnetMask(int body);
 
typedef void(WINAPI *DLL_pxBodySetFrozen)(int body, int stat);
DLL_pxBodySetFrozen pxBodySetFrozen;
pxBodySetFrozen = (DLL_pxBodySetFrozen)GetProcAddress(hDLL,"_pxBodySetFrozen@8");
// Use ==> pxBodySetFrozen(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenRotX)(int body, int stat);
DLL_pxBodySetFrozenRotX pxBodySetFrozenRotX;
pxBodySetFrozenRotX = (DLL_pxBodySetFrozenRotX)GetProcAddress(hDLL,"_pxBodySetFrozenRotX@8");
// Use ==> pxBodySetFrozenRotX(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenRotY)(int body, int stat);
DLL_pxBodySetFrozenRotY pxBodySetFrozenRotY;
pxBodySetFrozenRotY = (DLL_pxBodySetFrozenRotY)GetProcAddress(hDLL,"_pxBodySetFrozenRotY@8");
// Use ==> pxBodySetFrozenRotY(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenRotZ)(int body, int stat);
DLL_pxBodySetFrozenRotZ pxBodySetFrozenRotZ;
pxBodySetFrozenRotZ = (DLL_pxBodySetFrozenRotZ)GetProcAddress(hDLL,"_pxBodySetFrozenRotZ@8");
// Use ==> pxBodySetFrozenRotZ(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenRot)(int body, int stat);
DLL_pxBodySetFrozenRot pxBodySetFrozenRot;
pxBodySetFrozenRot = (DLL_pxBodySetFrozenRot)GetProcAddress(hDLL,"_pxBodySetFrozenRot@8");
// Use ==> pxBodySetFrozenRot(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenPosX)(int body, int stat);
DLL_pxBodySetFrozenPosX pxBodySetFrozenPosX;
pxBodySetFrozenPosX = (DLL_pxBodySetFrozenPosX)GetProcAddress(hDLL,"_pxBodySetFrozenPosX@8");
// Use ==> pxBodySetFrozenPosX(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenPosY)(int body, int stat);
DLL_pxBodySetFrozenPosY pxBodySetFrozenPosY;
pxBodySetFrozenPosY = (DLL_pxBodySetFrozenPosY)GetProcAddress(hDLL,"_pxBodySetFrozenPosY@8");
// Use ==> pxBodySetFrozenPosY(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenPosZ)(int body, int stat);
DLL_pxBodySetFrozenPosZ pxBodySetFrozenPosZ;
pxBodySetFrozenPosZ = (DLL_pxBodySetFrozenPosZ)GetProcAddress(hDLL,"_pxBodySetFrozenPosZ@8");
// Use ==> pxBodySetFrozenPosZ(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFrozenPos)(int body, int stat);
DLL_pxBodySetFrozenPos pxBodySetFrozenPos;
pxBodySetFrozenPos = (DLL_pxBodySetFrozenPos)GetProcAddress(hDLL,"_pxBodySetFrozenPos@8");
// Use ==> pxBodySetFrozenPos(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagGravity)(int body, int stat);
DLL_pxBodySetFlagGravity pxBodySetFlagGravity;
pxBodySetFlagGravity = (DLL_pxBodySetFlagGravity)GetProcAddress(hDLL,"_pxBodySetFlagGravity@8");
// Use ==> pxBodySetFlagGravity(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagCollision)(int body, int stat);
DLL_pxBodySetFlagCollision pxBodySetFlagCollision;
pxBodySetFlagCollision = (DLL_pxBodySetFlagCollision)GetProcAddress(hDLL,"_pxBodySetFlagCollision@8");
// Use ==> pxBodySetFlagCollision(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagResponse)(int body, int stat);
DLL_pxBodySetFlagResponse pxBodySetFlagResponse;
pxBodySetFlagResponse = (DLL_pxBodySetFlagResponse)GetProcAddress(hDLL,"_pxBodySetFlagResponse@8");
// Use ==> pxBodySetFlagResponse(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagContacttable)(int body, int stat);
DLL_pxBodySetFlagContacttable pxBodySetFlagContacttable;
pxBodySetFlagContacttable = (DLL_pxBodySetFlagContacttable)GetProcAddress(hDLL,"_pxBodySetFlagContacttable@8");
// Use ==> pxBodySetFlagContacttable(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagMagniteble)(int body, int stat);
DLL_pxBodySetFlagMagniteble pxBodySetFlagMagniteble;
pxBodySetFlagMagniteble = (DLL_pxBodySetFlagMagniteble)GetProcAddress(hDLL,"_pxBodySetFlagMagniteble@8");
// Use ==> pxBodySetFlagMagniteble(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagTriggertable)(int body, int stat);
DLL_pxBodySetFlagTriggertable pxBodySetFlagTriggertable;
pxBodySetFlagTriggertable = (DLL_pxBodySetFlagTriggertable)GetProcAddress(hDLL,"_pxBodySetFlagTriggertable@8");
// Use ==> pxBodySetFlagTriggertable(int body, int stat);
 
typedef void(WINAPI *DLL_pxBodySetFlagRayCast)(int body, int stat);
DLL_pxBodySetFlagRayCast pxBodySetFlagRayCast;
pxBodySetFlagRayCast = (DLL_pxBodySetFlagRayCast)GetProcAddress(hDLL,"_pxBodySetFlagRayCast@8");
// Use ==> pxBodySetFlagRayCast(int body, int stat);
 
typedef void(WINAPI *DLL_pxDeleteBody)(int num);
DLL_pxDeleteBody pxDeleteBody;
pxDeleteBody = (DLL_pxDeleteBody)GetProcAddress(hDLL,"_pxDeleteBody@4");
// Use ==> pxDeleteBody(int num);
 
typedef void(WINAPI *DLL_pxDeleteJoint)(int joint);
DLL_pxDeleteJoint pxDeleteJoint;
pxDeleteJoint = (DLL_pxDeleteJoint)GetProcAddress(hDLL,"_pxDeleteJoint@4");
// Use ==> pxDeleteJoint(int joint);
 
typedef int(WINAPI *DLL_pxJointCreateHinge)(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointCreateHinge pxJointCreateHinge;
pxJointCreateHinge = (DLL_pxJointCreateHinge)GetProcAddress(hDLL,"_pxJointCreateHinge@32");
// Use ==> int result = pxJointCreateHinge(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
 
typedef int(WINAPI *DLL_pxJointCreateSpherical)(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointCreateSpherical pxJointCreateSpherical;
pxJointCreateSpherical = (DLL_pxJointCreateSpherical)GetProcAddress(hDLL,"_pxJointCreateSpherical@32");
// Use ==> int result = pxJointCreateSpherical(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
 
typedef int(WINAPI *DLL_pxJointCreateDistance)(int body1, int body2, float p1_x, float p1_y, float p1_z, float p2_x, float p2_y, float p2_z);
DLL_pxJointCreateDistance pxJointCreateDistance;
pxJointCreateDistance = (DLL_pxJointCreateDistance)GetProcAddress(hDLL,"_pxJointCreateDistance@32");
// Use ==> int result = pxJointCreateDistance(int body1, int body2, float p1_x, float p1_y, float p1_z, float p2_x, float p2_y, float p2_z);
 
typedef int(WINAPI *DLL_pxJointCreateFixed)(int body1, int body2);
DLL_pxJointCreateFixed pxJointCreateFixed;
pxJointCreateFixed = (DLL_pxJointCreateFixed)GetProcAddress(hDLL,"_pxJointCreateFixed@8");
// Use ==> int result = pxJointCreateFixed(int body1, int body2);
 
typedef int(WINAPI *DLL_pxJointCreateCylindrical)(int body1, int body2, float x, float y, float z, float nx, float ny, float nz, float min_limit, float max_limit);
DLL_pxJointCreateCylindrical pxJointCreateCylindrical;
pxJointCreateCylindrical = (DLL_pxJointCreateCylindrical)GetProcAddress(hDLL,"_pxJointCreateCylindrical@40");
// Use ==> int result = pxJointCreateCylindrical(int body1, int body2, float x, float y, float z, float nx, float ny, float nz, float min_limit, float max_limit);
 
typedef int(WINAPI *DLL_pxJointCreatePrismatic)(int body1, int body2, float x, float y, float z, float nx, float ny, float nz, float min_limit, float max_limit);
DLL_pxJointCreatePrismatic pxJointCreatePrismatic;
pxJointCreatePrismatic = (DLL_pxJointCreatePrismatic)GetProcAddress(hDLL,"_pxJointCreatePrismatic@40");
// Use ==> int result = pxJointCreatePrismatic(int body1, int body2, float x, float y, float z, float nx, float ny, float nz, float min_limit, float max_limit);
 
typedef int(WINAPI *DLL_pxJointCreateOnLine)(int body1, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointCreateOnLine pxJointCreateOnLine;
pxJointCreateOnLine = (DLL_pxJointCreateOnLine)GetProcAddress(hDLL,"_pxJointCreateOnLine@28");
// Use ==> int result = pxJointCreateOnLine(int body1, float x, float y, float z, float nx, float ny, float nz);
 
typedef int(WINAPI *DLL_pxJointCreateInPlane)(int body1, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointCreateInPlane pxJointCreateInPlane;
pxJointCreateInPlane = (DLL_pxJointCreateInPlane)GetProcAddress(hDLL,"_pxJointCreateInPlane@28");
// Use ==> int result = pxJointCreateInPlane(int body1, float x, float y, float z, float nx, float ny, float nz);
 
typedef int(WINAPI *DLL_pxJointCreatePulley)(int body1, int body2, float distance, float stiff, float k_ratio);
DLL_pxJointCreatePulley pxJointCreatePulley;
pxJointCreatePulley = (DLL_pxJointCreatePulley)GetProcAddress(hDLL,"_pxJointCreatePulley@20");
// Use ==> int result = pxJointCreatePulley(int body1, int body2, float distance, float stiff, float k_ratio);
 
typedef int(WINAPI *DLL_pxJointCreateD6Joint)(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointCreateD6Joint pxJointCreateD6Joint;
pxJointCreateD6Joint = (DLL_pxJointCreateD6Joint)GetProcAddress(hDLL,"_pxJointCreateD6Joint@32");
// Use ==> int result = pxJointCreateD6Joint(int body1, int body2, float x, float y, float z, float nx, float ny, float nz);
 
typedef void(WINAPI *DLL_pxD6JointSetPosMotion)(int joint, int xMot, int yMot, int zMot);
DLL_pxD6JointSetPosMotion pxD6JointSetPosMotion;
pxD6JointSetPosMotion = (DLL_pxD6JointSetPosMotion)GetProcAddress(hDLL,"_pxD6JointSetPosMotion@16");
// Use ==> pxD6JointSetPosMotion(int joint, int xMot, int yMot, int zMot);
 
typedef void(WINAPI *DLL_pxD6JointSetAngleMotion)(int joint, int twistMot, int swing1Mot, int swing2Mot);
DLL_pxD6JointSetAngleMotion pxD6JointSetAngleMotion;
pxD6JointSetAngleMotion = (DLL_pxD6JointSetAngleMotion)GetProcAddress(hDLL,"_pxD6JointSetAngleMotion@16");
// Use ==> pxD6JointSetAngleMotion(int joint, int twistMot, int swing1Mot, int swing2Mot);
 
typedef void(WINAPI *DLL_pxD6JointSetLinearLimit)(int joint, float lim);
DLL_pxD6JointSetLinearLimit pxD6JointSetLinearLimit;
pxD6JointSetLinearLimit = (DLL_pxD6JointSetLinearLimit)GetProcAddress(hDLL,"_pxD6JointSetLinearLimit@8");
// Use ==> pxD6JointSetLinearLimit(int joint, float lim);
 
typedef void(WINAPI *DLL_pxD6JointSetSwingLimit)(int joint, float swing1Lim, float swing2Lim);
DLL_pxD6JointSetSwingLimit pxD6JointSetSwingLimit;
pxD6JointSetSwingLimit = (DLL_pxD6JointSetSwingLimit)GetProcAddress(hDLL,"_pxD6JointSetSwingLimit@12");
// Use ==> pxD6JointSetSwingLimit(int joint, float swing1Lim, float swing2Lim);
 
typedef void(WINAPI *DLL_pxD6JointSetTwistLimit)(int joint, float lowLim, float heighLim);
DLL_pxD6JointSetTwistLimit pxD6JointSetTwistLimit;
pxD6JointSetTwistLimit = (DLL_pxD6JointSetTwistLimit)GetProcAddress(hDLL,"_pxD6JointSetTwistLimit@12");
// Use ==> pxD6JointSetTwistLimit(int joint, float lowLim, float heighLim);
 
typedef void(WINAPI *DLL_pxJointHingeSetLimit)(int joint, float min, float max);
DLL_pxJointHingeSetLimit pxJointHingeSetLimit;
pxJointHingeSetLimit = (DLL_pxJointHingeSetLimit)GetProcAddress(hDLL,"_pxJointHingeSetLimit@12");
// Use ==> pxJointHingeSetLimit(int joint, float min, float max);
 
typedef void(WINAPI *DLL_pxJointHingeSetSpring)(int joint, float spr, float targetVal);
DLL_pxJointHingeSetSpring pxJointHingeSetSpring;
pxJointHingeSetSpring = (DLL_pxJointHingeSetSpring)GetProcAddress(hDLL,"_pxJointHingeSetSpring@12");
// Use ==> pxJointHingeSetSpring(int joint, float spr, float targetVal);
 
typedef void(WINAPI *DLL_pxJointSphericalSetLimitAngle)(int joint, float angle, float hardn, float restit);
DLL_pxJointSphericalSetLimitAngle pxJointSphericalSetLimitAngle;
pxJointSphericalSetLimitAngle = (DLL_pxJointSphericalSetLimitAngle)GetProcAddress(hDLL,"_pxJointSphericalSetLimitAngle@16");
// Use ==> pxJointSphericalSetLimitAngle(int joint, float angle, float hardn, float restit);
 
typedef void(WINAPI *DLL_pxJointSphericalSetLimitTwist)(int joint, float mintwist, float maxtwist, float spr, float damp, float targetVal);
DLL_pxJointSphericalSetLimitTwist pxJointSphericalSetLimitTwist;
pxJointSphericalSetLimitTwist = (DLL_pxJointSphericalSetLimitTwist)GetProcAddress(hDLL,"_pxJointSphericalSetLimitTwist@24");
// Use ==> pxJointSphericalSetLimitTwist(int joint, float mintwist, float maxtwist, float spr, float damp, float targetVal);
 
typedef void(WINAPI *DLL_pxJointSphericalSetLimitSpring)(int joint, float spr, float damp, float targetVal);
DLL_pxJointSphericalSetLimitSpring pxJointSphericalSetLimitSpring;
pxJointSphericalSetLimitSpring = (DLL_pxJointSphericalSetLimitSpring)GetProcAddress(hDLL,"_pxJointSphericalSetLimitSpring@16");
// Use ==> pxJointSphericalSetLimitSpring(int joint, float spr, float damp, float targetVal);
 
typedef void(WINAPI *DLL_pxJointDistanceSetPoint)(int joint, float mindist, float maxdist);
DLL_pxJointDistanceSetPoint pxJointDistanceSetPoint;
pxJointDistanceSetPoint = (DLL_pxJointDistanceSetPoint)GetProcAddress(hDLL,"_pxJointDistanceSetPoint@12");
// Use ==> pxJointDistanceSetPoint(int joint, float mindist, float maxdist);
 
typedef void(WINAPI *DLL_pxJointDistanceSetSpring)(int joint, float spr, float damp);
DLL_pxJointDistanceSetSpring pxJointDistanceSetSpring;
pxJointDistanceSetSpring = (DLL_pxJointDistanceSetSpring)GetProcAddress(hDLL,"_pxJointDistanceSetSpring@12");
// Use ==> pxJointDistanceSetSpring(int joint, float spr, float damp);
 
typedef void(WINAPI *DLL_pxJointAddLimitPlane)(int joint, float x, float y, float z, float nx, float ny, float nz);
DLL_pxJointAddLimitPlane pxJointAddLimitPlane;
pxJointAddLimitPlane = (DLL_pxJointAddLimitPlane)GetProcAddress(hDLL,"_pxJointAddLimitPlane@28");
// Use ==> pxJointAddLimitPlane(int joint, float x, float y, float z, float nx, float ny, float nz);
 
typedef void(WINAPI *DLL_pxJointPulleySetAnchor)(int joint, float x1, float y1, float z1, float x2, float y2, float z2);
DLL_pxJointPulleySetAnchor pxJointPulleySetAnchor;
pxJointPulleySetAnchor = (DLL_pxJointPulleySetAnchor)GetProcAddress(hDLL,"_pxJointPulleySetAnchor@28");
// Use ==> pxJointPulleySetAnchor(int joint, float x1, float y1, float z1, float x2, float y2, float z2);
 
typedef void(WINAPI *DLL_pxJointPulleySetLocalAttachBody)(int joint, float x1, float y1, float z1, float x2, float y2, float z2);
DLL_pxJointPulleySetLocalAttachBody pxJointPulleySetLocalAttachBody;
pxJointPulleySetLocalAttachBody = (DLL_pxJointPulleySetLocalAttachBody)GetProcAddress(hDLL,"_pxJointPulleySetLocalAttachBody@28");
// Use ==> pxJointPulleySetLocalAttachBody(int joint, float x1, float y1, float z1, float x2, float y2, float z2);
 
typedef void(WINAPI *DLL_pxJointHingeSetMotor)(int joint, float force, float velTarget);
DLL_pxJointHingeSetMotor pxJointHingeSetMotor;
pxJointHingeSetMotor = (DLL_pxJointHingeSetMotor)GetProcAddress(hDLL,"_pxJointHingeSetMotor@12");
// Use ==> pxJointHingeSetMotor(int joint, float force, float velTarget);
 
typedef void(WINAPI *DLL_pxJointSetBreakable)(int joint, float force, float torque);
DLL_pxJointSetBreakable pxJointSetBreakable;
pxJointSetBreakable = (DLL_pxJointSetBreakable)GetProcAddress(hDLL,"_pxJointSetBreakable@12");
// Use ==> pxJointSetBreakable(int joint, float force, float torque);
 
typedef int(WINAPI *DLL_pxJointIsBroken)(int joint);
DLL_pxJointIsBroken pxJointIsBroken;
pxJointIsBroken = (DLL_pxJointIsBroken)GetProcAddress(hDLL,"_pxJointIsBroken@4");
// Use ==> int result = pxJointIsBroken(int joint);
 
typedef void(WINAPI *DLL_pxJointHingeSetCollision)(int joint);
DLL_pxJointHingeSetCollision pxJointHingeSetCollision;
pxJointHingeSetCollision = (DLL_pxJointHingeSetCollision)GetProcAddress(hDLL,"_pxJointHingeSetCollision@4");
// Use ==> pxJointHingeSetCollision(int joint);
 
typedef void(WINAPI *DLL_pxJointSphericalSetCollision)(int joint);
DLL_pxJointSphericalSetCollision pxJointSphericalSetCollision;
pxJointSphericalSetCollision = (DLL_pxJointSphericalSetCollision)GetProcAddress(hDLL,"_pxJointSphericalSetCollision@4");
// Use ==> pxJointSphericalSetCollision(int joint);
 
typedef void(WINAPI *DLL_pxJointDistanceSetCollision)(int joint);
DLL_pxJointDistanceSetCollision pxJointDistanceSetCollision;
pxJointDistanceSetCollision = (DLL_pxJointDistanceSetCollision)GetProcAddress(hDLL,"_pxJointDistanceSetCollision@4");
// Use ==> pxJointDistanceSetCollision(int joint);
 
typedef void(WINAPI *DLL_pxJointCylindricalSetCollision)(int joint);
DLL_pxJointCylindricalSetCollision pxJointCylindricalSetCollision;
pxJointCylindricalSetCollision = (DLL_pxJointCylindricalSetCollision)GetProcAddress(hDLL,"_pxJointCylindricalSetCollision@4");
// Use ==> pxJointCylindricalSetCollision(int joint);
 
typedef void(WINAPI *DLL_pxSetGravity)(float gx, float gy, float gz);
DLL_pxSetGravity pxSetGravity;
pxSetGravity = (DLL_pxSetGravity)GetProcAddress(hDLL,"_pxSetGravity@12");
// Use ==> pxSetGravity(float gx, float gy, float gz);
 
typedef void(WINAPI *DLL_pxBodySetMass)(int num, float mass);
DLL_pxBodySetMass pxBodySetMass;
pxBodySetMass = (DLL_pxBodySetMass)GetProcAddress(hDLL,"_pxBodySetMass@8");
// Use ==> pxBodySetMass(int num, float mass);
 
typedef void(WINAPI *DLL_pxBodySetCMassLocalPosition)(int num, float x, float y, float z);
DLL_pxBodySetCMassLocalPosition pxBodySetCMassLocalPosition;
pxBodySetCMassLocalPosition = (DLL_pxBodySetCMassLocalPosition)GetProcAddress(hDLL,"_pxBodySetCMassLocalPosition@16");
// Use ==> pxBodySetCMassLocalPosition(int num, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodySetCMassGlobalPosition)(int num, float x, float y, float z);
DLL_pxBodySetCMassGlobalPosition pxBodySetCMassGlobalPosition;
pxBodySetCMassGlobalPosition = (DLL_pxBodySetCMassGlobalPosition)GetProcAddress(hDLL,"_pxBodySetCMassGlobalPosition@16");
// Use ==> pxBodySetCMassGlobalPosition(int num, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodySetMassSpaceInertiaTensor)(int num, float x, float y, float z);
DLL_pxBodySetMassSpaceInertiaTensor pxBodySetMassSpaceInertiaTensor;
pxBodySetMassSpaceInertiaTensor = (DLL_pxBodySetMassSpaceInertiaTensor)GetProcAddress(hDLL,"_pxBodySetMassSpaceInertiaTensor@16");
// Use ==> pxBodySetMassSpaceInertiaTensor(int num, float x, float y, float z);
 
typedef float(WINAPI *DLL_pxBodyGetCMassGlobalPositionX)(int body);
DLL_pxBodyGetCMassGlobalPositionX pxBodyGetCMassGlobalPositionX;
pxBodyGetCMassGlobalPositionX = (DLL_pxBodyGetCMassGlobalPositionX)GetProcAddress(hDLL,"_pxBodyGetCMassGlobalPositionX@4");
// Use ==> float result = pxBodyGetCMassGlobalPositionX(int body);
 
typedef float(WINAPI *DLL_pxBodyGetCMassGlobalPositionY)(int body);
DLL_pxBodyGetCMassGlobalPositionY pxBodyGetCMassGlobalPositionY;
pxBodyGetCMassGlobalPositionY = (DLL_pxBodyGetCMassGlobalPositionY)GetProcAddress(hDLL,"_pxBodyGetCMassGlobalPositionY@4");
// Use ==> float result = pxBodyGetCMassGlobalPositionY(int body);
 
typedef float(WINAPI *DLL_pxBodyGetCMassGlobalPositionZ)(int body);
DLL_pxBodyGetCMassGlobalPositionZ pxBodyGetCMassGlobalPositionZ;
pxBodyGetCMassGlobalPositionZ = (DLL_pxBodyGetCMassGlobalPositionZ)GetProcAddress(hDLL,"_pxBodyGetCMassGlobalPositionZ@4");
// Use ==> float result = pxBodyGetCMassGlobalPositionZ(int body);
 
typedef float(WINAPI *DLL_pxBodyGetCMassLocalPositionX)(int body);
DLL_pxBodyGetCMassLocalPositionX pxBodyGetCMassLocalPositionX;
pxBodyGetCMassLocalPositionX = (DLL_pxBodyGetCMassLocalPositionX)GetProcAddress(hDLL,"_pxBodyGetCMassLocalPositionX@4");
// Use ==> float result = pxBodyGetCMassLocalPositionX(int body);
 
typedef float(WINAPI *DLL_pxBodyGetCMassLocalPositionY)(int body);
DLL_pxBodyGetCMassLocalPositionY pxBodyGetCMassLocalPositionY;
pxBodyGetCMassLocalPositionY = (DLL_pxBodyGetCMassLocalPositionY)GetProcAddress(hDLL,"_pxBodyGetCMassLocalPositionY@4");
// Use ==> float result = pxBodyGetCMassLocalPositionY(int body);
 
typedef float(WINAPI *DLL_pxBodyGetCMassLocalPositionZ)(int body);
DLL_pxBodyGetCMassLocalPositionZ pxBodyGetCMassLocalPositionZ;
pxBodyGetCMassLocalPositionZ = (DLL_pxBodyGetCMassLocalPositionZ)GetProcAddress(hDLL,"_pxBodyGetCMassLocalPositionZ@4");
// Use ==> float result = pxBodyGetCMassLocalPositionZ(int body);
 
typedef float(WINAPI *DLL_pxBodyGetMass)(int body);
DLL_pxBodyGetMass pxBodyGetMass;
pxBodyGetMass = (DLL_pxBodyGetMass)GetProcAddress(hDLL,"_pxBodyGetMass@4");
// Use ==> float result = pxBodyGetMass(int body);
 
typedef void(WINAPI *DLL_pxBodySetMyForce)(int body, float lx, float ly, float lz);
DLL_pxBodySetMyForce pxBodySetMyForce;
pxBodySetMyForce = (DLL_pxBodySetMyForce)GetProcAddress(hDLL,"_pxBodySetMyForce@16");
// Use ==> pxBodySetMyForce(int body, float lx, float ly, float lz);
 
typedef void(WINAPI *DLL_pxBodyAddForce)(int num, float vx, float vy, float vz, int mode);
DLL_pxBodyAddForce pxBodyAddForce;
pxBodyAddForce = (DLL_pxBodyAddForce)GetProcAddress(hDLL,"_pxBodyAddForce@20");
// Use ==> pxBodyAddForce(int num, float vx, float vy, float vz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddForceAtPos)(int num, float vx, float vy, float vz, float px, float py, float pz, int mode);
DLL_pxBodyAddForceAtPos pxBodyAddForceAtPos;
pxBodyAddForceAtPos = (DLL_pxBodyAddForceAtPos)GetProcAddress(hDLL,"_pxBodyAddForceAtPos@32");
// Use ==> pxBodyAddForceAtPos(int num, float vx, float vy, float vz, float px, float py, float pz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddForceAtLocalPos)(int body, float vx, float vy, float vz, float px, float py, float pz, int mode);
DLL_pxBodyAddForceAtLocalPos pxBodyAddForceAtLocalPos;
pxBodyAddForceAtLocalPos = (DLL_pxBodyAddForceAtLocalPos)GetProcAddress(hDLL,"_pxBodyAddForceAtLocalPos@32");
// Use ==> pxBodyAddForceAtLocalPos(int body, float vx, float vy, float vz, float px, float py, float pz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddLocalForce)(int num, float vx, float vy, float vz, int mode);
DLL_pxBodyAddLocalForce pxBodyAddLocalForce;
pxBodyAddLocalForce = (DLL_pxBodyAddLocalForce)GetProcAddress(hDLL,"_pxBodyAddLocalForce@20");
// Use ==> pxBodyAddLocalForce(int num, float vx, float vy, float vz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddLocalForceAtPos)(int num, float vx, float vy, float vz, float px, float py, float pz, int mode);
DLL_pxBodyAddLocalForceAtPos pxBodyAddLocalForceAtPos;
pxBodyAddLocalForceAtPos = (DLL_pxBodyAddLocalForceAtPos)GetProcAddress(hDLL,"_pxBodyAddLocalForceAtPos@32");
// Use ==> pxBodyAddLocalForceAtPos(int num, float vx, float vy, float vz, float px, float py, float pz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddLocalForceAtLocalPos)(int body, float vx, float vy, float vz, float px, float py, float pz, int mode);
DLL_pxBodyAddLocalForceAtLocalPos pxBodyAddLocalForceAtLocalPos;
pxBodyAddLocalForceAtLocalPos = (DLL_pxBodyAddLocalForceAtLocalPos)GetProcAddress(hDLL,"_pxBodyAddLocalForceAtLocalPos@32");
// Use ==> pxBodyAddLocalForceAtLocalPos(int body, float vx, float vy, float vz, float px, float py, float pz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddTorque)(int body, float vx, float vy, float vz, int mode);
DLL_pxBodyAddTorque pxBodyAddTorque;
pxBodyAddTorque = (DLL_pxBodyAddTorque)GetProcAddress(hDLL,"_pxBodyAddTorque@20");
// Use ==> pxBodyAddTorque(int body, float vx, float vy, float vz, int mode);
 
typedef void(WINAPI *DLL_pxBodyAddLocalTorque)(int body, float vx, float vy, float vz, int mode);
DLL_pxBodyAddLocalTorque pxBodyAddLocalTorque;
pxBodyAddLocalTorque = (DLL_pxBodyAddLocalTorque)GetProcAddress(hDLL,"_pxBodyAddLocalTorque@20");
// Use ==> pxBodyAddLocalTorque(int body, float vx, float vy, float vz, int mode);
 
typedef void(WINAPI *DLL_pxBodySetAngularSpeed)(int body, float ax, float ay, float az);
DLL_pxBodySetAngularSpeed pxBodySetAngularSpeed;
pxBodySetAngularSpeed = (DLL_pxBodySetAngularSpeed)GetProcAddress(hDLL,"_pxBodySetAngularSpeed@16");
// Use ==> pxBodySetAngularSpeed(int body, float ax, float ay, float az);
 
typedef void(WINAPI *DLL_pxBodySetLinearSpeed)(int body, float lx, float ly, float lz);
DLL_pxBodySetLinearSpeed pxBodySetLinearSpeed;
pxBodySetLinearSpeed = (DLL_pxBodySetLinearSpeed)GetProcAddress(hDLL,"_pxBodySetLinearSpeed@16");
// Use ==> pxBodySetLinearSpeed(int body, float lx, float ly, float lz);
 
typedef void(WINAPI *DLL_pxBodySetLocalLinearSpeed)(int body, float lx, float ly, float lz);
DLL_pxBodySetLocalLinearSpeed pxBodySetLocalLinearSpeed;
pxBodySetLocalLinearSpeed = (DLL_pxBodySetLocalLinearSpeed)GetProcAddress(hDLL,"_pxBodySetLocalLinearSpeed@16");
// Use ==> pxBodySetLocalLinearSpeed(int body, float lx, float ly, float lz);
 
typedef void(WINAPI *DLL_pxBodySetLocalAngularSpeed)(int body, float lx, float ly, float lz);
DLL_pxBodySetLocalAngularSpeed pxBodySetLocalAngularSpeed;
pxBodySetLocalAngularSpeed = (DLL_pxBodySetLocalAngularSpeed)GetProcAddress(hDLL,"_pxBodySetLocalAngularSpeed@16");
// Use ==> pxBodySetLocalAngularSpeed(int body, float lx, float ly, float lz);
 
typedef void(WINAPI *DLL_pxBodySetAngularDamping)(int body, float angDamp);
DLL_pxBodySetAngularDamping pxBodySetAngularDamping;
pxBodySetAngularDamping = (DLL_pxBodySetAngularDamping)GetProcAddress(hDLL,"_pxBodySetAngularDamping@8");
// Use ==> pxBodySetAngularDamping(int body, float angDamp);
 
typedef void(WINAPI *DLL_pxBodySetLinearDamping)(int body, float linDamp);
DLL_pxBodySetLinearDamping pxBodySetLinearDamping;
pxBodySetLinearDamping = (DLL_pxBodySetLinearDamping)GetProcAddress(hDLL,"_pxBodySetLinearDamping@8");
// Use ==> pxBodySetLinearDamping(int body, float linDamp);
 
typedef void(WINAPI *DLL_pxBodySetAngularMomentum)(int body, float ax, float ay, float az);
DLL_pxBodySetAngularMomentum pxBodySetAngularMomentum;
pxBodySetAngularMomentum = (DLL_pxBodySetAngularMomentum)GetProcAddress(hDLL,"_pxBodySetAngularMomentum@16");
// Use ==> pxBodySetAngularMomentum(int body, float ax, float ay, float az);
 
typedef void(WINAPI *DLL_pxBodySetLinearMomentum)(int body, float lx, float ly, float lz);
DLL_pxBodySetLinearMomentum pxBodySetLinearMomentum;
pxBodySetLinearMomentum = (DLL_pxBodySetLinearMomentum)GetProcAddress(hDLL,"_pxBodySetLinearMomentum@16");
// Use ==> pxBodySetLinearMomentum(int body, float lx, float ly, float lz);
 
typedef void(WINAPI *DLL_pxBodySetMaxAngularSpeed)(int body, float speed);
DLL_pxBodySetMaxAngularSpeed pxBodySetMaxAngularSpeed;
pxBodySetMaxAngularSpeed = (DLL_pxBodySetMaxAngularSpeed)GetProcAddress(hDLL,"_pxBodySetMaxAngularSpeed@8");
// Use ==> pxBodySetMaxAngularSpeed(int body, float speed);
 
typedef void(WINAPI *DLL_pxBodySetSleepEnergyThreshold)(int body, float tres);
DLL_pxBodySetSleepEnergyThreshold pxBodySetSleepEnergyThreshold;
pxBodySetSleepEnergyThreshold = (DLL_pxBodySetSleepEnergyThreshold)GetProcAddress(hDLL,"_pxBodySetSleepEnergyThreshold@8");
// Use ==> pxBodySetSleepEnergyThreshold(int body, float tres);
 
typedef void(WINAPI *DLL_pxBodySetSleepAngularVelocity)(int body, float tres);
DLL_pxBodySetSleepAngularVelocity pxBodySetSleepAngularVelocity;
pxBodySetSleepAngularVelocity = (DLL_pxBodySetSleepAngularVelocity)GetProcAddress(hDLL,"_pxBodySetSleepAngularVelocity@8");
// Use ==> pxBodySetSleepAngularVelocity(int body, float tres);
 
typedef void(WINAPI *DLL_pxBodySetSleepLinearVelocity)(int body, float tres);
DLL_pxBodySetSleepLinearVelocity pxBodySetSleepLinearVelocity;
pxBodySetSleepLinearVelocity = (DLL_pxBodySetSleepLinearVelocity)GetProcAddress(hDLL,"_pxBodySetSleepLinearVelocity@8");
// Use ==> pxBodySetSleepLinearVelocity(int body, float tres);
 
typedef void(WINAPI *DLL_pxBodySetSleepWakeUp)(int body, float wakeCounterValue);
DLL_pxBodySetSleepWakeUp pxBodySetSleepWakeUp;
pxBodySetSleepWakeUp = (DLL_pxBodySetSleepWakeUp)GetProcAddress(hDLL,"_pxBodySetSleepWakeUp@8");
// Use ==> pxBodySetSleepWakeUp(int body, float wakeCounterValue);
 
typedef void(WINAPI *DLL_pxBodySetSleepPut)(int body);
DLL_pxBodySetSleepPut pxBodySetSleepPut;
pxBodySetSleepPut = (DLL_pxBodySetSleepPut)GetProcAddress(hDLL,"_pxBodySetSleepPut@4");
// Use ==> pxBodySetSleepPut(int body);
 
typedef void(WINAPI *DLL_pxBodySetSolverIterationCount)(int body, int itercount);
DLL_pxBodySetSolverIterationCount pxBodySetSolverIterationCount;
pxBodySetSolverIterationCount = (DLL_pxBodySetSolverIterationCount)GetProcAddress(hDLL,"_pxBodySetSolverIterationCount@8");
// Use ==> pxBodySetSolverIterationCount(int body, int itercount);
 
typedef void(WINAPI *DLL_pxBodySetBodyName)(int body, char *name);
DLL_pxBodySetBodyName pxBodySetBodyName;
pxBodySetBodyName = (DLL_pxBodySetBodyName)GetProcAddress(hDLL,"_pxBodySetBodyName@8");
// Use ==> pxBodySetBodyName(int body, char *name);
 
typedef void(WINAPI *DLL_pxBodySetBodyEntity)(int body, int entity);
DLL_pxBodySetBodyEntity pxBodySetBodyEntity;
pxBodySetBodyEntity = (DLL_pxBodySetBodyEntity)GetProcAddress(hDLL,"_pxBodySetBodyEntity@8");
// Use ==> pxBodySetBodyEntity(int body, int entity);
 
typedef void(WINAPI *DLL_pxBodySetBodyUserData)(int body, int userdata);
DLL_pxBodySetBodyUserData pxBodySetBodyUserData;
pxBodySetBodyUserData = (DLL_pxBodySetBodyUserData)GetProcAddress(hDLL,"_pxBodySetBodyUserData@8");
// Use ==> pxBodySetBodyUserData(int body, int userdata);
 
typedef void(WINAPI *DLL_pxMoveBodyToPoint)(int body, float maxspeed, float x, float y, float z);
DLL_pxMoveBodyToPoint pxMoveBodyToPoint;
pxMoveBodyToPoint = (DLL_pxMoveBodyToPoint)GetProcAddress(hDLL,"_pxMoveBodyToPoint@20");
// Use ==> pxMoveBodyToPoint(int body, float maxspeed, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxAddBodytoBody)(int body1, int body2);
DLL_pxAddBodytoBody pxAddBodytoBody;
pxAddBodytoBody = (DLL_pxAddBodytoBody)GetProcAddress(hDLL,"_pxAddBodytoBody@8");
// Use ==> pxAddBodytoBody(int body1, int body2);
 
typedef int(WINAPI *DLL_pxCopyBody)(int body);
DLL_pxCopyBody pxCopyBody;
pxCopyBody = (DLL_pxCopyBody)GetProcAddress(hDLL,"_pxCopyBody@4");
// Use ==> int result = pxCopyBody(int body);
 
typedef int(WINAPI *DLL_pxCreateMaterial)();
DLL_pxCreateMaterial pxCreateMaterial;
pxCreateMaterial = (DLL_pxCreateMaterial)GetProcAddress(hDLL,"_pxCreateMaterial@0");
// Use ==> int result = pxCreateMaterial();
 
typedef void(WINAPI *DLL_pxMaterialSetToBody)(int body, int mat);
DLL_pxMaterialSetToBody pxMaterialSetToBody;
pxMaterialSetToBody = (DLL_pxMaterialSetToBody)GetProcAddress(hDLL,"_pxMaterialSetToBody@8");
// Use ==> pxMaterialSetToBody(int body, int mat);
 
typedef void(WINAPI *DLL_pxMaterialSetRestitution)(int mat, float rest);
DLL_pxMaterialSetRestitution pxMaterialSetRestitution;
pxMaterialSetRestitution = (DLL_pxMaterialSetRestitution)GetProcAddress(hDLL,"_pxMaterialSetRestitution@8");
// Use ==> pxMaterialSetRestitution(int mat, float rest);
 
typedef void(WINAPI *DLL_pxMaterialSetRestitutionCombineMode)(int mat, int mode);
DLL_pxMaterialSetRestitutionCombineMode pxMaterialSetRestitutionCombineMode;
pxMaterialSetRestitutionCombineMode = (DLL_pxMaterialSetRestitutionCombineMode)GetProcAddress(hDLL,"_pxMaterialSetRestitutionCombineMode@8");
// Use ==> pxMaterialSetRestitutionCombineMode(int mat, int mode);
 
typedef void(WINAPI *DLL_pxMaterialSetStFriction)(int mat, float fric);
DLL_pxMaterialSetStFriction pxMaterialSetStFriction;
pxMaterialSetStFriction = (DLL_pxMaterialSetStFriction)GetProcAddress(hDLL,"_pxMaterialSetStFriction@8");
// Use ==> pxMaterialSetStFriction(int mat, float fric);
 
typedef void(WINAPI *DLL_pxMaterialSetDyFriction)(int mat, float fric);
DLL_pxMaterialSetDyFriction pxMaterialSetDyFriction;
pxMaterialSetDyFriction = (DLL_pxMaterialSetDyFriction)GetProcAddress(hDLL,"_pxMaterialSetDyFriction@8");
// Use ==> pxMaterialSetDyFriction(int mat, float fric);
 
typedef void(WINAPI *DLL_pxMaterialSetFrictionV)(int mat, float sfric, float dfric);
DLL_pxMaterialSetFrictionV pxMaterialSetFrictionV;
pxMaterialSetFrictionV = (DLL_pxMaterialSetFrictionV)GetProcAddress(hDLL,"_pxMaterialSetFrictionV@12");
// Use ==> pxMaterialSetFrictionV(int mat, float sfric, float dfric);
 
typedef void(WINAPI *DLL_pxMaterialSetFrictionCombineMode)(int mat, int mode);
DLL_pxMaterialSetFrictionCombineMode pxMaterialSetFrictionCombineMode;
pxMaterialSetFrictionCombineMode = (DLL_pxMaterialSetFrictionCombineMode)GetProcAddress(hDLL,"_pxMaterialSetFrictionCombineMode@8");
// Use ==> pxMaterialSetFrictionCombineMode(int mat, int mode);
 
typedef int(WINAPI *DLL_pxCreateAnisotripicMaterial)(float nx, float ny, float nz);
DLL_pxCreateAnisotripicMaterial pxCreateAnisotripicMaterial;
pxCreateAnisotripicMaterial = (DLL_pxCreateAnisotripicMaterial)GetProcAddress(hDLL,"_pxCreateAnisotripicMaterial@12");
// Use ==> int result = pxCreateAnisotripicMaterial(float nx, float ny, float nz);
 
typedef void(WINAPI *DLL_pxMaterialSetFlagStrongFriction)(int mat);
DLL_pxMaterialSetFlagStrongFriction pxMaterialSetFlagStrongFriction;
pxMaterialSetFlagStrongFriction = (DLL_pxMaterialSetFlagStrongFriction)GetProcAddress(hDLL,"_pxMaterialSetFlagStrongFriction@4");
// Use ==> pxMaterialSetFlagStrongFriction(int mat);
 
typedef int(WINAPI *DLL_pxGetMaterial)(int body);
DLL_pxGetMaterial pxGetMaterial;
pxGetMaterial = (DLL_pxGetMaterial)GetProcAddress(hDLL,"_pxGetMaterial@4");
// Use ==> int result = pxGetMaterial(int body);
 
typedef void(WINAPI *DLL_pxBodySetPosition)(int num, float pos_x, float pos_y, float pos_z);
DLL_pxBodySetPosition pxBodySetPosition;
pxBodySetPosition = (DLL_pxBodySetPosition)GetProcAddress(hDLL,"_pxBodySetPosition@16");
// Use ==> pxBodySetPosition(int num, float pos_x, float pos_y, float pos_z);
 
typedef void(WINAPI *DLL_pxBodySetRotation)(int num, float pitch, float yaw, float roll);
DLL_pxBodySetRotation pxBodySetRotation;
pxBodySetRotation = (DLL_pxBodySetRotation)GetProcAddress(hDLL,"_pxBodySetRotation@16");
// Use ==> pxBodySetRotation(int num, float pitch, float yaw, float roll);
 
typedef float(WINAPI *DLL_pxBodyGetPositionX)(int num);
DLL_pxBodyGetPositionX pxBodyGetPositionX;
pxBodyGetPositionX = (DLL_pxBodyGetPositionX)GetProcAddress(hDLL,"_pxBodyGetPositionX@4");
// Use ==> float result = pxBodyGetPositionX(int num);
 
typedef float(WINAPI *DLL_pxBodyGetPositionY)(int num);
DLL_pxBodyGetPositionY pxBodyGetPositionY;
pxBodyGetPositionY = (DLL_pxBodyGetPositionY)GetProcAddress(hDLL,"_pxBodyGetPositionY@4");
// Use ==> float result = pxBodyGetPositionY(int num);
 
typedef float(WINAPI *DLL_pxBodyGetPositionZ)(int num);
DLL_pxBodyGetPositionZ pxBodyGetPositionZ;
pxBodyGetPositionZ = (DLL_pxBodyGetPositionZ)GetProcAddress(hDLL,"_pxBodyGetPositionZ@4");
// Use ==> float result = pxBodyGetPositionZ(int num);
 
typedef float(WINAPI *DLL_pxBodyGetRotationPitch)(int num);
DLL_pxBodyGetRotationPitch pxBodyGetRotationPitch;
pxBodyGetRotationPitch = (DLL_pxBodyGetRotationPitch)GetProcAddress(hDLL,"_pxBodyGetRotationPitch@4");
// Use ==> float result = pxBodyGetRotationPitch(int num);
 
typedef float(WINAPI *DLL_pxBodyGetRotationYaw)(int num);
DLL_pxBodyGetRotationYaw pxBodyGetRotationYaw;
pxBodyGetRotationYaw = (DLL_pxBodyGetRotationYaw)GetProcAddress(hDLL,"_pxBodyGetRotationYaw@4");
// Use ==> float result = pxBodyGetRotationYaw(int num);
 
typedef float(WINAPI *DLL_pxBodyGetRotationRoll)(int num);
DLL_pxBodyGetRotationRoll pxBodyGetRotationRoll;
pxBodyGetRotationRoll = (DLL_pxBodyGetRotationRoll)GetProcAddress(hDLL,"_pxBodyGetRotationRoll@4");
// Use ==> float result = pxBodyGetRotationRoll(int num);
 
typedef float(WINAPI *DLL_pxBodyGetAngularSpeed)(int num);
DLL_pxBodyGetAngularSpeed pxBodyGetAngularSpeed;
pxBodyGetAngularSpeed = (DLL_pxBodyGetAngularSpeed)GetProcAddress(hDLL,"_pxBodyGetAngularSpeed@4");
// Use ==> float result = pxBodyGetAngularSpeed(int num);
 
typedef float(WINAPI *DLL_pxBodyGetAngularSpeedX)(int num);
DLL_pxBodyGetAngularSpeedX pxBodyGetAngularSpeedX;
pxBodyGetAngularSpeedX = (DLL_pxBodyGetAngularSpeedX)GetProcAddress(hDLL,"_pxBodyGetAngularSpeedX@4");
// Use ==> float result = pxBodyGetAngularSpeedX(int num);
 
typedef float(WINAPI *DLL_pxBodyGetAngularSpeedY)(int num);
DLL_pxBodyGetAngularSpeedY pxBodyGetAngularSpeedY;
pxBodyGetAngularSpeedY = (DLL_pxBodyGetAngularSpeedY)GetProcAddress(hDLL,"_pxBodyGetAngularSpeedY@4");
// Use ==> float result = pxBodyGetAngularSpeedY(int num);
 
typedef float(WINAPI *DLL_pxBodyGetAngularSpeedZ)(int num);
DLL_pxBodyGetAngularSpeedZ pxBodyGetAngularSpeedZ;
pxBodyGetAngularSpeedZ = (DLL_pxBodyGetAngularSpeedZ)GetProcAddress(hDLL,"_pxBodyGetAngularSpeedZ@4");
// Use ==> float result = pxBodyGetAngularSpeedZ(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearSpeed)(int num);
DLL_pxBodyGetLinearSpeed pxBodyGetLinearSpeed;
pxBodyGetLinearSpeed = (DLL_pxBodyGetLinearSpeed)GetProcAddress(hDLL,"_pxBodyGetLinearSpeed@4");
// Use ==> float result = pxBodyGetLinearSpeed(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearSpeedX)(int num);
DLL_pxBodyGetLinearSpeedX pxBodyGetLinearSpeedX;
pxBodyGetLinearSpeedX = (DLL_pxBodyGetLinearSpeedX)GetProcAddress(hDLL,"_pxBodyGetLinearSpeedX@4");
// Use ==> float result = pxBodyGetLinearSpeedX(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearSpeedY)(int num);
DLL_pxBodyGetLinearSpeedY pxBodyGetLinearSpeedY;
pxBodyGetLinearSpeedY = (DLL_pxBodyGetLinearSpeedY)GetProcAddress(hDLL,"_pxBodyGetLinearSpeedY@4");
// Use ==> float result = pxBodyGetLinearSpeedY(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearSpeedZ)(int num);
DLL_pxBodyGetLinearSpeedZ pxBodyGetLinearSpeedZ;
pxBodyGetLinearSpeedZ = (DLL_pxBodyGetLinearSpeedZ)GetProcAddress(hDLL,"_pxBodyGetLinearSpeedZ@4");
// Use ==> float result = pxBodyGetLinearSpeedZ(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLocalLinearSpeedX)(int body);
DLL_pxBodyGetLocalLinearSpeedX pxBodyGetLocalLinearSpeedX;
pxBodyGetLocalLinearSpeedX = (DLL_pxBodyGetLocalLinearSpeedX)GetProcAddress(hDLL,"_pxBodyGetLocalLinearSpeedX@4");
// Use ==> float result = pxBodyGetLocalLinearSpeedX(int body);
 
typedef float(WINAPI *DLL_pxBodyGetLocalLinearSpeedY)(int body);
DLL_pxBodyGetLocalLinearSpeedY pxBodyGetLocalLinearSpeedY;
pxBodyGetLocalLinearSpeedY = (DLL_pxBodyGetLocalLinearSpeedY)GetProcAddress(hDLL,"_pxBodyGetLocalLinearSpeedY@4");
// Use ==> float result = pxBodyGetLocalLinearSpeedY(int body);
 
typedef float(WINAPI *DLL_pxBodyGetLocalLinearSpeedZ)(int body);
DLL_pxBodyGetLocalLinearSpeedZ pxBodyGetLocalLinearSpeedZ;
pxBodyGetLocalLinearSpeedZ = (DLL_pxBodyGetLocalLinearSpeedZ)GetProcAddress(hDLL,"_pxBodyGetLocalLinearSpeedZ@4");
// Use ==> float result = pxBodyGetLocalLinearSpeedZ(int body);
 
typedef float(WINAPI *DLL_pxBodyGetLinearVecSpeedX)(int num);
DLL_pxBodyGetLinearVecSpeedX pxBodyGetLinearVecSpeedX;
pxBodyGetLinearVecSpeedX = (DLL_pxBodyGetLinearVecSpeedX)GetProcAddress(hDLL,"_pxBodyGetLinearVecSpeedX@4");
// Use ==> float result = pxBodyGetLinearVecSpeedX(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearVecSpeedY)(int num);
DLL_pxBodyGetLinearVecSpeedY pxBodyGetLinearVecSpeedY;
pxBodyGetLinearVecSpeedY = (DLL_pxBodyGetLinearVecSpeedY)GetProcAddress(hDLL,"_pxBodyGetLinearVecSpeedY@4");
// Use ==> float result = pxBodyGetLinearVecSpeedY(int num);
 
typedef float(WINAPI *DLL_pxBodyGetLinearVecSpeedZ)(int num);
DLL_pxBodyGetLinearVecSpeedZ pxBodyGetLinearVecSpeedZ;
pxBodyGetLinearVecSpeedZ = (DLL_pxBodyGetLinearVecSpeedZ)GetProcAddress(hDLL,"_pxBodyGetLinearVecSpeedZ@4");
// Use ==> float result = pxBodyGetLinearVecSpeedZ(int num);
 
typedef void(WINAPI *DLL_pxBodyGetLocalPointSpeed)(int body, float x, float y, float z);
DLL_pxBodyGetLocalPointSpeed pxBodyGetLocalPointSpeed;
pxBodyGetLocalPointSpeed = (DLL_pxBodyGetLocalPointSpeed)GetProcAddress(hDLL,"_pxBodyGetLocalPointSpeed@12");
// Use ==> pxBodyGetLocalPointSpeed(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodyGetLocalPointSpeedX)(int body, float x, float y, float z);
DLL_pxBodyGetLocalPointSpeedX pxBodyGetLocalPointSpeedX;
pxBodyGetLocalPointSpeedX = (DLL_pxBodyGetLocalPointSpeedX)GetProcAddress(hDLL,"_pxBodyGetLocalPointSpeedX@12");
// Use ==> pxBodyGetLocalPointSpeedX(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodyGetLocalPointSpeedY)(int body, float x, float y, float z);
DLL_pxBodyGetLocalPointSpeedY pxBodyGetLocalPointSpeedY;
pxBodyGetLocalPointSpeedY = (DLL_pxBodyGetLocalPointSpeedY)GetProcAddress(hDLL,"_pxBodyGetLocalPointSpeedY@12");
// Use ==> pxBodyGetLocalPointSpeedY(int body, float x, float y, float z);
 
typedef void(WINAPI *DLL_pxBodyGetLocalPointSpeedZ)(int body, float x, float y, float z);
DLL_pxBodyGetLocalPointSpeedZ pxBodyGetLocalPointSpeedZ;
pxBodyGetLocalPointSpeedZ = (DLL_pxBodyGetLocalPointSpeedZ)GetProcAddress(hDLL,"_pxBodyGetLocalPointSpeedZ@12");
// Use ==> pxBodyGetLocalPointSpeedZ(int body, float x, float y, float z);
 
typedef float(WINAPI *DLL_pxBodyGetAngularDamping)(int body);
DLL_pxBodyGetAngularDamping pxBodyGetAngularDamping;
pxBodyGetAngularDamping = (DLL_pxBodyGetAngularDamping)GetProcAddress(hDLL,"_pxBodyGetAngularDamping@4");
// Use ==> float result = pxBodyGetAngularDamping(int body);
 
typedef float(WINAPI *DLL_pxBodyGetLinearDamping)(int body);
DLL_pxBodyGetLinearDamping pxBodyGetLinearDamping;
pxBodyGetLinearDamping = (DLL_pxBodyGetLinearDamping)GetProcAddress(hDLL,"_pxBodyGetLinearDamping@4");
// Use ==> float result = pxBodyGetLinearDamping(int body);
 
typedef float(WINAPI *DLL_pxBodyGetAngularMomentum)(int body);
DLL_pxBodyGetAngularMomentum pxBodyGetAngularMomentum;
pxBodyGetAngularMomentum = (DLL_pxBodyGetAngularMomentum)GetProcAddress(hDLL,"_pxBodyGetAngularMomentum@4");
// Use ==> float result = pxBodyGetAngularMomentum(int body);
 
typedef float(WINAPI *DLL_pxBodyGetLinearMomentum)(int body);
DLL_pxBodyGetLinearMomentum pxBodyGetLinearMomentum;
pxBodyGetLinearMomentum = (DLL_pxBodyGetLinearMomentum)GetProcAddress(hDLL,"_pxBodyGetLinearMomentum@4");
// Use ==> float result = pxBodyGetLinearMomentum(int body);
 
typedef float(WINAPI *DLL_pxBodyGetMaxAngularVelocity)(int body);
DLL_pxBodyGetMaxAngularVelocity pxBodyGetMaxAngularVelocity;
pxBodyGetMaxAngularVelocity = (DLL_pxBodyGetMaxAngularVelocity)GetProcAddress(hDLL,"_pxBodyGetMaxAngularVelocity@4");
// Use ==> float result = pxBodyGetMaxAngularVelocity(int body);
 
typedef char*(WINAPI *DLL_pxGetBodyName)(int body);
DLL_pxGetBodyName pxGetBodyName;
pxGetBodyName = (DLL_pxGetBodyName)GetProcAddress(hDLL,"_pxGetBodyName@4");
// Use ==> char *result = pxGetBodyName(int body);
 
typedef int(WINAPI *DLL_pxGetBodyEntity)(int body);
DLL_pxGetBodyEntity pxGetBodyEntity;
pxGetBodyEntity = (DLL_pxGetBodyEntity)GetProcAddress(hDLL,"_pxGetBodyEntity@4");
// Use ==> int result = pxGetBodyEntity(int body);
 
typedef int(WINAPI *DLL_pxGetBodyUserData)(int body);
DLL_pxGetBodyUserData pxGetBodyUserData;
pxGetBodyUserData = (DLL_pxGetBodyUserData)GetProcAddress(hDLL,"_pxGetBodyUserData@4");
// Use ==> int result = pxGetBodyUserData(int body);
 
typedef float(WINAPI *DLL_pxBodyGetSleepAngularVelocity)(int body);
DLL_pxBodyGetSleepAngularVelocity pxBodyGetSleepAngularVelocity;
pxBodyGetSleepAngularVelocity = (DLL_pxBodyGetSleepAngularVelocity)GetProcAddress(hDLL,"_pxBodyGetSleepAngularVelocity@4");
// Use ==> float result = pxBodyGetSleepAngularVelocity(int body);
 
typedef float(WINAPI *DLL_pxBodyGetSleepLinearVelocity)(int body);
DLL_pxBodyGetSleepLinearVelocity pxBodyGetSleepLinearVelocity;
pxBodyGetSleepLinearVelocity = (DLL_pxBodyGetSleepLinearVelocity)GetProcAddress(hDLL,"_pxBodyGetSleepLinearVelocity@4");
// Use ==> float result = pxBodyGetSleepLinearVelocity(int body);
 
typedef int(WINAPI *DLL_pxBodyIsDynamic)(int body);
DLL_pxBodyIsDynamic pxBodyIsDynamic;
pxBodyIsDynamic = (DLL_pxBodyIsDynamic)GetProcAddress(hDLL,"_pxBodyIsDynamic@4");
// Use ==> int result = pxBodyIsDynamic(int body);
 
typedef int(WINAPI *DLL_pxBodyIsSleeping)(int body);
DLL_pxBodyIsSleeping pxBodyIsSleeping;
pxBodyIsSleeping = (DLL_pxBodyIsSleeping)GetProcAddress(hDLL,"_pxBodyIsSleeping@4");
// Use ==> int result = pxBodyIsSleeping(int body);

// End of Part 2
