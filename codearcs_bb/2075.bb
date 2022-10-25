; ID: 2075
; Author: ZJP
; Date: 2007-07-23 11:04:45
; Title: Blitz3dSDK - C/C++ - Newton's Itmbin Wrapper
; Description: How to use Newton's Itmbin  wrapper and B3DSDK in C/C++ Project

HINSTANCE nwDLL = LoadLibrary("nwphx.DLL");
 
typedef void(WINAPI *DLL_phWorldCreate)(int plane, char *license_key);
DLL_phWorldCreate phWorldCreate;
phWorldCreate = (DLL_phWorldCreate)GetProcAddress(nwDLL,"_phWorldCreate@8");
// Use ==> phWorldCreate(int plane, char *license_key);
 
typedef void(WINAPI *DLL_phWorldSetGravity)(float gx, float gy, float gz);
DLL_phWorldSetGravity phWorldSetGravity;
phWorldSetGravity = (DLL_phWorldSetGravity)GetProcAddress(nwDLL,"_phWorldSetGravity@12");
// Use ==> phWorldSetGravity(float gx, float gy, float gz);
 
typedef void(WINAPI *DLL_phWorldStep)(float timestep);
DLL_phWorldStep phWorldStep;
phWorldStep = (DLL_phWorldStep)GetProcAddress(nwDLL,"_phWorldStep@4");
// Use ==> phWorldStep(float timestep);
 
typedef void(WINAPI *DLL_phWorldSetSize)(float x1, float y1, float z1, float x2, float y2, float z2);
DLL_phWorldSetSize phWorldSetSize;
phWorldSetSize = (DLL_phWorldSetSize)GetProcAddress(nwDLL,"_phWorldSetSize@24");
// Use ==> phWorldSetSize(float x1, float y1, float z1, float x2, float y2, float z2);
 
typedef void(WINAPI *DLL_phWorldSetSolverModel)(int model);
DLL_phWorldSetSolverModel phWorldSetSolverModel;
phWorldSetSolverModel = (DLL_phWorldSetSolverModel)GetProcAddress(nwDLL,"_phWorldSetSolverModel@4");
// Use ==> phWorldSetSolverModel(int model);
 
typedef void(WINAPI *DLL_phWorldSetFrictionModel)(int model);
DLL_phWorldSetFrictionModel phWorldSetFrictionModel;
phWorldSetFrictionModel = (DLL_phWorldSetFrictionModel)GetProcAddress(nwDLL,"_phWorldSetFrictionModel@4");
// Use ==> phWorldSetFrictionModel(int model);
 
typedef void(WINAPI *DLL_phWorldMagnetize)(float x, float y, float z, float r, float a0, float a1, float a2, float max);
DLL_phWorldMagnetize phWorldMagnetize;
phWorldMagnetize = (DLL_phWorldMagnetize)GetProcAddress(nwDLL,"_phWorldMagnetize@32");
// Use ==> phWorldMagnetize(float x, float y, float z, float r, float a0, float a1, float a2, float max);
 
typedef void(WINAPI *DLL_phWorldDestroy)();
DLL_phWorldDestroy phWorldDestroy;
phWorldDestroy = (DLL_phWorldDestroy)GetProcAddress(nwDLL,"_phWorldDestroy@0");
// Use ==> phWorldDestroy();
 
typedef int(WINAPI *DLL_phMatCreate)();
DLL_phMatCreate phMatCreate;
phMatCreate = (DLL_phMatCreate)GetProcAddress(nwDLL,"_phMatCreate@0");
// Use ==> int result = phMatCreate();
 
typedef void(WINAPI *DLL_phMatSetCollidable)(int mat1, int mat2, int IsColl);
DLL_phMatSetCollidable phMatSetCollidable;
phMatSetCollidable = (DLL_phMatSetCollidable)GetProcAddress(nwDLL,"_phMatSetCollidable@12");
// Use ==> phMatSetCollidable(int mat1, int mat2, int IsColl);
 
typedef void(WINAPI *DLL_phMatSetFriction)(int mat1, int mat2, float stFric, float dynFric);
DLL_phMatSetFriction phMatSetFriction;
phMatSetFriction = (DLL_phMatSetFriction)GetProcAddress(nwDLL,"_phMatSetFriction@16");
// Use ==> phMatSetFriction(int mat1, int mat2, float stFric, float dynFric);
 
typedef void(WINAPI *DLL_phMatSetElasticity)(int mat1, int mat2, float elas);
DLL_phMatSetElasticity phMatSetElasticity;
phMatSetElasticity = (DLL_phMatSetElasticity)GetProcAddress(nwDLL,"_phMatSetElasticity@12");
// Use ==> phMatSetElasticity(int mat1, int mat2, float elas);
 
typedef void(WINAPI *DLL_phMatSetSoftness)(int mat1, int mat2, float soft);
DLL_phMatSetSoftness phMatSetSoftness;
phMatSetSoftness = (DLL_phMatSetSoftness)GetProcAddress(nwDLL,"_phMatSetSoftness@12");
// Use ==> phMatSetSoftness(int mat1, int mat2, float soft);
 
typedef void(WINAPI *DLL_phMatSetDefCollidable)(int IsColl);
DLL_phMatSetDefCollidable phMatSetDefCollidable;
phMatSetDefCollidable = (DLL_phMatSetDefCollidable)GetProcAddress(nwDLL,"_phMatSetDefCollidable@4");
// Use ==> phMatSetDefCollidable(int IsColl);
 
typedef void(WINAPI *DLL_phMatSetDefFriction)(float stFric, float dynFric);
DLL_phMatSetDefFriction phMatSetDefFriction;
phMatSetDefFriction = (DLL_phMatSetDefFriction)GetProcAddress(nwDLL,"_phMatSetDefFriction@8");
// Use ==> phMatSetDefFriction(float stFric, float dynFric);
 
typedef void(WINAPI *DLL_phMatSetDefElasticity)(float elas);
DLL_phMatSetDefElasticity phMatSetDefElasticity;
phMatSetDefElasticity = (DLL_phMatSetDefElasticity)GetProcAddress(nwDLL,"_phMatSetDefElasticity@4");
// Use ==> phMatSetDefElasticity(float elas);
 
typedef void(WINAPI *DLL_phMatSetDefSoftness)(float soft);
DLL_phMatSetDefSoftness phMatSetDefSoftness;
phMatSetDefSoftness = (DLL_phMatSetDefSoftness)GetProcAddress(nwDLL,"_phMatSetDefSoftness@4");
// Use ==> phMatSetDefSoftness(float soft);
 
typedef int(WINAPI *DLL_phBodyCreateNull)(float mass);
DLL_phBodyCreateNull phBodyCreateNull;
phBodyCreateNull = (DLL_phBodyCreateNull)GetProcAddress(nwDLL,"_phBodyCreateNull@4");
// Use ==> int result = phBodyCreateNull(float mass);
 
typedef int(WINAPI *DLL_phBodyCreateBox)(float dx, float dy, float dz, float mass);
DLL_phBodyCreateBox phBodyCreateBox;
phBodyCreateBox = (DLL_phBodyCreateBox)GetProcAddress(nwDLL,"_phBodyCreateBox@16");
// Use ==> int result = phBodyCreateBox(float dx, float dy, float dz, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateSphere)(float rx, float ry, float rz, float mass);
DLL_phBodyCreateSphere phBodyCreateSphere;
phBodyCreateSphere = (DLL_phBodyCreateSphere)GetProcAddress(nwDLL,"_phBodyCreateSphere@16");
// Use ==> int result = phBodyCreateSphere(float rx, float ry, float rz, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateCyl)(float r, float h, float mass);
DLL_phBodyCreateCyl phBodyCreateCyl;
phBodyCreateCyl = (DLL_phBodyCreateCyl)GetProcAddress(nwDLL,"_phBodyCreateCyl@12");
// Use ==> int result = phBodyCreateCyl(float r, float h, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateCone)(float r, float h, float mass);
DLL_phBodyCreateCone phBodyCreateCone;
phBodyCreateCone = (DLL_phBodyCreateCone)GetProcAddress(nwDLL,"_phBodyCreateCone@12");
// Use ==> int result = phBodyCreateCone(float r, float h, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateCapsule)(float r, float h, float mass);
DLL_phBodyCreateCapsule phBodyCreateCapsule;
phBodyCreateCapsule = (DLL_phBodyCreateCapsule)GetProcAddress(nwDLL,"_phBodyCreateCapsule@12");
// Use ==> int result = phBodyCreateCapsule(float r, float h, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateHull)(int *Vertices, int VertexCount, float mass);
DLL_phBodyCreateHull phBodyCreateHull;
phBodyCreateHull = (DLL_phBodyCreateHull)GetProcAddress(nwDLL,"_phBodyCreateHull@12");
// Use ==> int result = phBodyCreateHull(int *Vertices, int VertexCount, float mass);
 
typedef int(WINAPI *DLL_phBodyCreateMesh)(int *Vertices, int VertexCount, float mass);
DLL_phBodyCreateMesh phBodyCreateMesh;
phBodyCreateMesh = (DLL_phBodyCreateMesh)GetProcAddress(nwDLL,"_phBodyCreateMesh@12");
// Use ==> int result = phBodyCreateMesh(int *Vertices, int VertexCount, float mass);
 
typedef void(WINAPI *DLL_phBodyCompoundBegin)();
DLL_phBodyCompoundBegin phBodyCompoundBegin;
phBodyCompoundBegin = (DLL_phBodyCompoundBegin)GetProcAddress(nwDLL,"_phBodyCompoundBegin@0");
// Use ==> phBodyCompoundBegin();
 
typedef void(WINAPI *DLL_phBodyCompoundAddHull)(int *Vertices, int VertexCount);
DLL_phBodyCompoundAddHull phBodyCompoundAddHull;
phBodyCompoundAddHull = (DLL_phBodyCompoundAddHull)GetProcAddress(nwDLL,"_phBodyCompoundAddHull@8");
// Use ==> phBodyCompoundAddHull(int *Vertices, int VertexCount);
 
typedef void(WINAPI *DLL_phBodyCompoundAddBody)(int body);
DLL_phBodyCompoundAddBody phBodyCompoundAddBody;
phBodyCompoundAddBody = (DLL_phBodyCompoundAddBody)GetProcAddress(nwDLL,"_phBodyCompoundAddBody@4");
// Use ==> phBodyCompoundAddBody(int body);
 
typedef int(WINAPI *DLL_phBodyCompoundEnd)(float mass);
DLL_phBodyCompoundEnd phBodyCompoundEnd;
phBodyCompoundEnd = (DLL_phBodyCompoundEnd)GetProcAddress(nwDLL,"_phBodyCompoundEnd@4");
// Use ==> int result = phBodyCompoundEnd(float mass);
 
typedef void(WINAPI *DLL_phBodyCompoundExBegin)();
DLL_phBodyCompoundExBegin phBodyCompoundExBegin;
phBodyCompoundExBegin = (DLL_phBodyCompoundExBegin)GetProcAddress(nwDLL,"_phBodyCompoundExBegin@0");
// Use ==> phBodyCompoundExBegin();
 
typedef void(WINAPI *DLL_phBodyCompoundExAddHull)(int *Vertices, int VertexCount, float mass);
DLL_phBodyCompoundExAddHull phBodyCompoundExAddHull;
phBodyCompoundExAddHull = (DLL_phBodyCompoundExAddHull)GetProcAddress(nwDLL,"_phBodyCompoundExAddHull@12");
// Use ==> phBodyCompoundExAddHull(int *Vertices, int VertexCount, float mass);
 
typedef void(WINAPI *DLL_phBodyCompoundExAddHullDens)(int *Vertices, int VertexCount, float density);
DLL_phBodyCompoundExAddHullDens phBodyCompoundExAddHullDens;
phBodyCompoundExAddHullDens = (DLL_phBodyCompoundExAddHullDens)GetProcAddress(nwDLL,"_phBodyCompoundExAddHullDens@12");
// Use ==> phBodyCompoundExAddHullDens(int *Vertices, int VertexCount, float density);
 
typedef void(WINAPI *DLL_phBodyCompoundExAddBody)(int body);
DLL_phBodyCompoundExAddBody phBodyCompoundExAddBody;
phBodyCompoundExAddBody = (DLL_phBodyCompoundExAddBody)GetProcAddress(nwDLL,"_phBodyCompoundExAddBody@4");
// Use ==> phBodyCompoundExAddBody(int body);
 
typedef int(WINAPI *DLL_phBodyCompoundExEnd)();
DLL_phBodyCompoundExEnd phBodyCompoundExEnd;
phBodyCompoundExEnd = (DLL_phBodyCompoundExEnd)GetProcAddress(nwDLL,"_phBodyCompoundExEnd@0");
// Use ==> int result = phBodyCompoundExEnd();
 
typedef void(WINAPI *DLL_phBodyDestroy)(int body);
DLL_phBodyDestroy phBodyDestroy;
phBodyDestroy = (DLL_phBodyDestroy)GetProcAddress(nwDLL,"_phBodyDestroy@4");
// Use ==> phBodyDestroy(int body);
 
typedef void(WINAPI *DLL_phBodySetMat)(int body, int mat);
DLL_phBodySetMat phBodySetMat;
phBodySetMat = (DLL_phBodySetMat)GetProcAddress(nwDLL,"_phBodySetMat@8");
// Use ==> phBodySetMat(int body, int mat);
 
typedef int(WINAPI *DLL_phBodyGetMat)(int body);
DLL_phBodyGetMat phBodyGetMat;
phBodyGetMat = (DLL_phBodyGetMat)GetProcAddress(nwDLL,"_phBodyGetMat@4");
// Use ==> int result = phBodyGetMat(int body);
 
typedef float(WINAPI *DLL_phBodyGetX)(int body);
DLL_phBodyGetX phBodyGetX;
phBodyGetX = (DLL_phBodyGetX)GetProcAddress(nwDLL,"_phBodyGetX@4");
// Use ==> float result = phBodyGetX(int body);
 
typedef float(WINAPI *DLL_phBodyGetY)(int body);
DLL_phBodyGetY phBodyGetY;
phBodyGetY = (DLL_phBodyGetY)GetProcAddress(nwDLL,"_phBodyGetY@4");
// Use ==> float result = phBodyGetY(int body);
 
typedef float(WINAPI *DLL_phBodyGetZ)(int body);
DLL_phBodyGetZ phBodyGetZ;
phBodyGetZ = (DLL_phBodyGetZ)GetProcAddress(nwDLL,"_phBodyGetZ@4");
// Use ==> float result = phBodyGetZ(int body);
 
typedef void(WINAPI *DLL_phBodySetPos)(int boyd, float x, float y, float z);
DLL_phBodySetPos phBodySetPos;
phBodySetPos = (DLL_phBodySetPos)GetProcAddress(nwDLL,"_phBodySetPos@16");
// Use ==> phBodySetPos(int boyd, float x, float y, float z);
 
typedef float(WINAPI *DLL_phBodyGetPitch)(int body);
DLL_phBodyGetPitch phBodyGetPitch;
phBodyGetPitch = (DLL_phBodyGetPitch)GetProcAddress(nwDLL,"_phBodyGetPitch@4");
// Use ==> float result = phBodyGetPitch(int body);
 
typedef float(WINAPI *DLL_phBodyGetYaw)(int body);
DLL_phBodyGetYaw phBodyGetYaw;
phBodyGetYaw = (DLL_phBodyGetYaw)GetProcAddress(nwDLL,"_phBodyGetYaw@4");
// Use ==> float result = phBodyGetYaw(int body);
 
typedef float(WINAPI *DLL_phBodyGetRoll)(int body);
DLL_phBodyGetRoll phBodyGetRoll;
phBodyGetRoll = (DLL_phBodyGetRoll)GetProcAddress(nwDLL,"_phBodyGetRoll@4");
// Use ==> float result = phBodyGetRoll(int body);
 
typedef void(WINAPI *DLL_phBodySetRot)(int body, float pitch, float yaw, float roll);
DLL_phBodySetRot phBodySetRot;
phBodySetRot = (DLL_phBodySetRot)GetProcAddress(nwDLL,"_phBodySetRot@16");
// Use ==> phBodySetRot(int body, float pitch, float yaw, float roll);
 
typedef float(WINAPI *DLL_phBodyGetVelX)(int body);
DLL_phBodyGetVelX phBodyGetVelX;
phBodyGetVelX = (DLL_phBodyGetVelX)GetProcAddress(nwDLL,"_phBodyGetVelX@4");
// Use ==> float result = phBodyGetVelX(int body);
 
typedef float(WINAPI *DLL_phBodyGetVelY)(int body);
DLL_phBodyGetVelY phBodyGetVelY;
phBodyGetVelY = (DLL_phBodyGetVelY)GetProcAddress(nwDLL,"_phBodyGetVelY@4");
// Use ==> float result = phBodyGetVelY(int body);
 
typedef float(WINAPI *DLL_phBodyGetVelZ)(int body);
DLL_phBodyGetVelZ phBodyGetVelZ;
phBodyGetVelZ = (DLL_phBodyGetVelZ)GetProcAddress(nwDLL,"_phBodyGetVelZ@4");
// Use ==> float result = phBodyGetVelZ(int body);
 
typedef void(WINAPI *DLL_phBodySetVel)(int body, float vx, float vy, float vz);
DLL_phBodySetVel phBodySetVel;
phBodySetVel = (DLL_phBodySetVel)GetProcAddress(nwDLL,"_phBodySetVel@16");
// Use ==> phBodySetVel(int body, float vx, float vy, float vz);
 
typedef float(WINAPI *DLL_phBodyGetVelXAtPos)(int body, float x, float y, float z);
DLL_phBodyGetVelXAtPos phBodyGetVelXAtPos;
phBodyGetVelXAtPos = (DLL_phBodyGetVelXAtPos)GetProcAddress(nwDLL,"_phBodyGetVelXAtPos@16");
// Use ==> float result = phBodyGetVelXAtPos(int body, float x, float y, float z);
 
typedef float(WINAPI *DLL_phBodyGetVelYAtPos)(int body, float x, float y, float z);
DLL_phBodyGetVelYAtPos phBodyGetVelYAtPos;
phBodyGetVelYAtPos = (DLL_phBodyGetVelYAtPos)GetProcAddress(nwDLL,"_phBodyGetVelYAtPos@16");
// Use ==> float result = phBodyGetVelYAtPos(int body, float x, float y, float z);
 
typedef float(WINAPI *DLL_phBodyGetVelZAtPos)(int body, float x, float y, float z);
DLL_phBodyGetVelZAtPos phBodyGetVelZAtPos;
phBodyGetVelZAtPos = (DLL_phBodyGetVelZAtPos)GetProcAddress(nwDLL,"_phBodyGetVelZAtPos@16");
// Use ==> float result = phBodyGetVelZAtPos(int body, float x, float y, float z);
 
typedef float(WINAPI *DLL_phBodyGetOmegaX)(int body);
DLL_phBodyGetOmegaX phBodyGetOmegaX;
phBodyGetOmegaX = (DLL_phBodyGetOmegaX)GetProcAddress(nwDLL,"_phBodyGetOmegaX@4");
// Use ==> float result = phBodyGetOmegaX(int body);
 
typedef float(WINAPI *DLL_phBodyGetOmegaY)(int body);
DLL_phBodyGetOmegaY phBodyGetOmegaY;
phBodyGetOmegaY = (DLL_phBodyGetOmegaY)GetProcAddress(nwDLL,"_phBodyGetOmegaY@4");
// Use ==> float result = phBodyGetOmegaY(int body);
 
typedef float(WINAPI *DLL_phBodyGetOmegaZ)(int body);
DLL_phBodyGetOmegaZ phBodyGetOmegaZ;
phBodyGetOmegaZ = (DLL_phBodyGetOmegaZ)GetProcAddress(nwDLL,"_phBodyGetOmegaZ@4");
// Use ==> float result = phBodyGetOmegaZ(int body);
 
typedef void(WINAPI *DLL_phBodySetOmega)(int body, float ox, float oy, float oz);
DLL_phBodySetOmega phBodySetOmega;
phBodySetOmega = (DLL_phBodySetOmega)GetProcAddress(nwDLL,"_phBodySetOmega@16");
// Use ==> phBodySetOmega(int body, float ox, float oy, float oz);
 
typedef void(WINAPI *DLL_phBodyAddForce)(int body, float fx, float fy, float fz);
DLL_phBodyAddForce phBodyAddForce;
phBodyAddForce = (DLL_phBodyAddForce)GetProcAddress(nwDLL,"_phBodyAddForce@16");
// Use ==> phBodyAddForce(int body, float fx, float fy, float fz);
 
typedef void(WINAPI *DLL_phBodyAddRelForce)(int body, float fx, float fy, float fz);
DLL_phBodyAddRelForce phBodyAddRelForce;
phBodyAddRelForce = (DLL_phBodyAddRelForce)GetProcAddress(nwDLL,"_phBodyAddRelForce@16");
// Use ==> phBodyAddRelForce(int body, float fx, float fy, float fz);
 
typedef void(WINAPI *DLL_phBodyAddForceAtPos)(int body, float fx, float fy, float fz, float x, float y, float z);
DLL_phBodyAddForceAtPos phBodyAddForceAtPos;
phBodyAddForceAtPos = (DLL_phBodyAddForceAtPos)GetProcAddress(nwDLL,"_phBodyAddForceAtPos@28");
// Use ==> phBodyAddForceAtPos(int body, float fx, float fy, float fz, float x, float y, float z);
 
typedef void(WINAPI *DLL_phBodyAddRelForceAtRelPos)(int body, float fx, float fy, float fz, float x, float y, float z);
DLL_phBodyAddRelForceAtRelPos phBodyAddRelForceAtRelPos;
phBodyAddRelForceAtRelPos = (DLL_phBodyAddRelForceAtRelPos)GetProcAddress(nwDLL,"_phBodyAddRelForceAtRelPos@28");
// Use ==> phBodyAddRelForceAtRelPos(int body, float fx, float fy, float fz, float x, float y, float z);
 
typedef void(WINAPI *DLL_phBodyAddImpulse)(int body, float x, float y, float z, float vx, float vy, float vz);
DLL_phBodyAddImpulse phBodyAddImpulse;
phBodyAddImpulse = (DLL_phBodyAddImpulse)GetProcAddress(nwDLL,"_phBodyAddImpulse@28");
// Use ==> phBodyAddImpulse(int body, float x, float y, float z, float vx, float vy, float vz);
 
typedef void(WINAPI *DLL_phBodySetGravityMode)(int body, int mode);
DLL_phBodySetGravityMode phBodySetGravityMode;
phBodySetGravityMode = (DLL_phBodySetGravityMode)GetProcAddress(nwDLL,"_phBodySetGravityMode@8");
// Use ==> phBodySetGravityMode(int body, int mode);
 
typedef int(WINAPI *DLL_phBodyGetGravityMode)(int body);
DLL_phBodyGetGravityMode phBodyGetGravityMode;
phBodyGetGravityMode = (DLL_phBodyGetGravityMode)GetProcAddress(nwDLL,"_phBodyGetGravityMode@4");
// Use ==> int result = phBodyGetGravityMode(int body);
 
typedef void(WINAPI *DLL_phBodySetWater)(int body, float x, float y, float z, float nx, float ny, float nz, float density, float lin_damping, float ang_damping);
DLL_phBodySetWater phBodySetWater;
phBodySetWater = (DLL_phBodySetWater)GetProcAddress(nwDLL,"_phBodySetWater@40");
// Use ==> phBodySetWater(int body, float x, float y, float z, float nx, float ny, float nz, float density, float lin_damping, float ang_damping);
 
typedef void(WINAPI *DLL_phBodyDisableWater)(int body);
DLL_phBodyDisableWater phBodyDisableWater;
phBodyDisableWater = (DLL_phBodyDisableWater)GetProcAddress(nwDLL,"_phBodyDisableWater@4");
// Use ==> phBodyDisableWater(int body);
 
typedef void(WINAPI *DLL_phBodyAddTorque)(int body, float tx, float ty, float tz);
DLL_phBodyAddTorque phBodyAddTorque;
phBodyAddTorque = (DLL_phBodyAddTorque)GetProcAddress(nwDLL,"_phBodyAddTorque@16");
// Use ==> phBodyAddTorque(int body, float tx, float ty, float tz);
 
typedef void(WINAPI *DLL_phBodyAddRelTorque)(int body, float tx, float ty, float tz);
DLL_phBodyAddRelTorque phBodyAddRelTorque;
phBodyAddRelTorque = (DLL_phBodyAddRelTorque)GetProcAddress(nwDLL,"_phBodyAddRelTorque@16");
// Use ==> phBodyAddRelTorque(int body, float tx, float ty, float tz);
 
typedef void(WINAPI *DLL_phBodySetDamping)(int body, float linear, float angular);
DLL_phBodySetDamping phBodySetDamping;
phBodySetDamping = (DLL_phBodySetDamping)GetProcAddress(nwDLL,"_phBodySetDamping@12");
// Use ==> phBodySetDamping(int body, float linear, float angular);
 
typedef void(WINAPI *DLL_phBodySetContinuousCollisionMode)(int body, int mode);
DLL_phBodySetContinuousCollisionMode phBodySetContinuousCollisionMode;
phBodySetContinuousCollisionMode = (DLL_phBodySetContinuousCollisionMode)GetProcAddress(nwDLL,"_phBodySetContinuousCollisionMode@8");
// Use ==> phBodySetContinuousCollisionMode(int body, int mode);
 
typedef int(WINAPI *DLL_phBodyGetContinuousCollisionMode)(int body);
DLL_phBodyGetContinuousCollisionMode phBodyGetContinuousCollisionMode;
phBodyGetContinuousCollisionMode = (DLL_phBodyGetContinuousCollisionMode)GetProcAddress(nwDLL,"_phBodyGetContinuousCollisionMode@4");
// Use ==> int result = phBodyGetContinuousCollisionMode(int body);
 
typedef void(WINAPI *DLL_phBodySetSleep)(int body, int state);
DLL_phBodySetSleep phBodySetSleep;
phBodySetSleep = (DLL_phBodySetSleep)GetProcAddress(nwDLL,"_phBodySetSleep@8");
// Use ==> phBodySetSleep(int body, int state);
 
typedef int(WINAPI *DLL_phBodyGetSleep)(int body);
DLL_phBodyGetSleep phBodyGetSleep;
phBodyGetSleep = (DLL_phBodyGetSleep)GetProcAddress(nwDLL,"_phBodyGetSleep@4");
// Use ==> int result = phBodyGetSleep(int body);
 
typedef void(WINAPI *DLL_phBodySetAutoSleep)(int body, int state);
DLL_phBodySetAutoSleep phBodySetAutoSleep;
phBodySetAutoSleep = (DLL_phBodySetAutoSleep)GetProcAddress(nwDLL,"_phBodySetAutoSleep@8");
// Use ==> phBodySetAutoSleep(int body, int state);
 
typedef void(WINAPI *DLL_phBodySetAutoSleepTreshold)(int body, float vel, float omega, int frames);
DLL_phBodySetAutoSleepTreshold phBodySetAutoSleepTreshold;
phBodySetAutoSleepTreshold = (DLL_phBodySetAutoSleepTreshold)GetProcAddress(nwDLL,"_phBodySetAutoSleepTreshold@16");
// Use ==> phBodySetAutoSleepTreshold(int body, float vel, float omega, int frames);
 
typedef void(WINAPI *DLL_phBodySetData)(int body, int data);
DLL_phBodySetData phBodySetData;
phBodySetData = (DLL_phBodySetData)GetProcAddress(nwDLL,"_phBodySetData@8");
// Use ==> phBodySetData(int body, int data);
 
typedef int(WINAPI *DLL_phBodyGetData)(int body);
DLL_phBodyGetData phBodyGetData;
phBodyGetData = (DLL_phBodyGetData)GetProcAddress(nwDLL,"_phBodyGetData@4");
// Use ==> int result = phBodyGetData(int body);
 
typedef void(WINAPI *DLL_phBodySetEntity)(int body, int ent);
DLL_phBodySetEntity phBodySetEntity;
phBodySetEntity = (DLL_phBodySetEntity)GetProcAddress(nwDLL,"_phBodySetEntity@8");
// Use ==> phBodySetEntity(int body, int ent);
 
typedef int(WINAPI *DLL_phBodyGetEntity)(int body);
DLL_phBodyGetEntity phBodyGetEntity;
phBodyGetEntity = (DLL_phBodyGetEntity)GetProcAddress(nwDLL,"_phBodyGetEntity@4");
// Use ==> int result = phBodyGetEntity(int body);
 
typedef int(WINAPI *DLL_phBodyGetCollNum)(int body);
DLL_phBodyGetCollNum phBodyGetCollNum;
phBodyGetCollNum = (DLL_phBodyGetCollNum)GetProcAddress(nwDLL,"_phBodyGetCollNum@4");
// Use ==> int result = phBodyGetCollNum(int body);
 
typedef float(WINAPI *DLL_phBodyGetCollX)(int body, int coll);
DLL_phBodyGetCollX phBodyGetCollX;
phBodyGetCollX = (DLL_phBodyGetCollX)GetProcAddress(nwDLL,"_phBodyGetCollX@8");
// Use ==> float result = phBodyGetCollX(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollY)(int body, int coll);
DLL_phBodyGetCollY phBodyGetCollY;
phBodyGetCollY = (DLL_phBodyGetCollY)GetProcAddress(nwDLL,"_phBodyGetCollY@8");
// Use ==> float result = phBodyGetCollY(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollZ)(int body, int coll);
DLL_phBodyGetCollZ phBodyGetCollZ;
phBodyGetCollZ = (DLL_phBodyGetCollZ)GetProcAddress(nwDLL,"_phBodyGetCollZ@8");
// Use ==> float result = phBodyGetCollZ(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollNX)(int body, int coll);
DLL_phBodyGetCollNX phBodyGetCollNX;
phBodyGetCollNX = (DLL_phBodyGetCollNX)GetProcAddress(nwDLL,"_phBodyGetCollNX@8");
// Use ==> float result = phBodyGetCollNX(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollNY)(int body, int coll);
DLL_phBodyGetCollNY phBodyGetCollNY;
phBodyGetCollNY = (DLL_phBodyGetCollNY)GetProcAddress(nwDLL,"_phBodyGetCollNY@8");
// Use ==> float result = phBodyGetCollNY(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollNZ)(int body, int coll);
DLL_phBodyGetCollNZ phBodyGetCollNZ;
phBodyGetCollNZ = (DLL_phBodyGetCollNZ)GetProcAddress(nwDLL,"_phBodyGetCollNZ@8");
// Use ==> float result = phBodyGetCollNZ(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollFX)(int body, int coll);
DLL_phBodyGetCollFX phBodyGetCollFX;
phBodyGetCollFX = (DLL_phBodyGetCollFX)GetProcAddress(nwDLL,"_phBodyGetCollFX@8");
// Use ==> float result = phBodyGetCollFX(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollFY)(int body, int coll);
DLL_phBodyGetCollFY phBodyGetCollFY;
phBodyGetCollFY = (DLL_phBodyGetCollFY)GetProcAddress(nwDLL,"_phBodyGetCollFY@8");
// Use ==> float result = phBodyGetCollFY(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollFZ)(int body, int coll);
DLL_phBodyGetCollFZ phBodyGetCollFZ;
phBodyGetCollFZ = (DLL_phBodyGetCollFZ)GetProcAddress(nwDLL,"_phBodyGetCollFZ@8");
// Use ==> float result = phBodyGetCollFZ(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollNVel)(int body, int coll);
DLL_phBodyGetCollNVel phBodyGetCollNVel;
phBodyGetCollNVel = (DLL_phBodyGetCollNVel)GetProcAddress(nwDLL,"_phBodyGetCollNVel@8");
// Use ==> float result = phBodyGetCollNVel(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollTVel)(int body, int coll);
DLL_phBodyGetCollTVel phBodyGetCollTVel;
phBodyGetCollTVel = (DLL_phBodyGetCollTVel)GetProcAddress(nwDLL,"_phBodyGetCollTVel@8");
// Use ==> float result = phBodyGetCollTVel(int body, int coll);
 
typedef float(WINAPI *DLL_phBodyGetCollBVel)(int body, int coll);
DLL_phBodyGetCollBVel phBodyGetCollBVel;
phBodyGetCollBVel = (DLL_phBodyGetCollBVel)GetProcAddress(nwDLL,"_phBodyGetCollBVel@8");
// Use ==> float result = phBodyGetCollBVel(int body, int coll);
 
typedef int(WINAPI *DLL_phBodyGetCollBody)(int body, int coll);
DLL_phBodyGetCollBody phBodyGetCollBody;
phBodyGetCollBody = (DLL_phBodyGetCollBody)GetProcAddress(nwDLL,"_phBodyGetCollBody@8");
// Use ==> int result = phBodyGetCollBody(int body, int coll);
 
typedef int(WINAPI *DLL_phBodyIsCollidedWith)(int body1, int body2);
DLL_phBodyIsCollidedWith phBodyIsCollidedWith;
phBodyIsCollidedWith = (DLL_phBodyIsCollidedWith)GetProcAddress(nwDLL,"_phBodyIsCollidedWith@8");
// Use ==> int result = phBodyIsCollidedWith(int body1, int body2);
 
typedef void(WINAPI *DLL_phBodySetMass)(int body, float mass);
DLL_phBodySetMass phBodySetMass;
phBodySetMass = (DLL_phBodySetMass)GetProcAddress(nwDLL,"_phBodySetMass@8");
// Use ==> phBodySetMass(int body, float mass);
 
typedef void(WINAPI *DLL_phBodySetMassMatrix)(int body, float mass, float Ixx, float Iyy, float Izz);
DLL_phBodySetMassMatrix phBodySetMassMatrix;
phBodySetMassMatrix = (DLL_phBodySetMassMatrix)GetProcAddress(nwDLL,"_phBodySetMassMatrix@20");
// Use ==> phBodySetMassMatrix(int body, float mass, float Ixx, float Iyy, float Izz);
 
typedef float(WINAPI *DLL_phBodyGetMass)(int body);
DLL_phBodyGetMass phBodyGetMass;
phBodyGetMass = (DLL_phBodyGetMass)GetProcAddress(nwDLL,"_phBodyGetMass@4");
// Use ==> float result = phBodyGetMass(int body);
 
typedef float(WINAPI *DLL_phBodyGetMassIxx)(int body);
DLL_phBodyGetMassIxx phBodyGetMassIxx;
phBodyGetMassIxx = (DLL_phBodyGetMassIxx)GetProcAddress(nwDLL,"_phBodyGetMassIxx@4");
// Use ==> float result = phBodyGetMassIxx(int body);
 
typedef float(WINAPI *DLL_phBodyGetMassIyy)(int body);
DLL_phBodyGetMassIyy phBodyGetMassIyy;
phBodyGetMassIyy = (DLL_phBodyGetMassIyy)GetProcAddress(nwDLL,"_phBodyGetMassIyy@4");
// Use ==> float result = phBodyGetMassIyy(int body);
 
typedef float(WINAPI *DLL_phBodyGetMassIzz)(int body);
DLL_phBodyGetMassIzz phBodyGetMassIzz;
phBodyGetMassIzz = (DLL_phBodyGetMassIzz)GetProcAddress(nwDLL,"_phBodyGetMassIzz@4");
// Use ==> float result = phBodyGetMassIzz(int body);
 
typedef void(WINAPI *DLL_phBodySetMassCentre)(int body, float x, float y, float z);
DLL_phBodySetMassCentre phBodySetMassCentre;
phBodySetMassCentre = (DLL_phBodySetMassCentre)GetProcAddress(nwDLL,"_phBodySetMassCentre@16");
// Use ==> phBodySetMassCentre(int body, float x, float y, float z);
 
typedef float(WINAPI *DLL_phBodyGetMassCentreX)(int body);
DLL_phBodyGetMassCentreX phBodyGetMassCentreX;
phBodyGetMassCentreX = (DLL_phBodyGetMassCentreX)GetProcAddress(nwDLL,"_phBodyGetMassCentreX@4");
// Use ==> float result = phBodyGetMassCentreX(int body);
 
typedef float(WINAPI *DLL_phBodyGetMassCentreY)(int body);
DLL_phBodyGetMassCentreY phBodyGetMassCentreY;
phBodyGetMassCentreY = (DLL_phBodyGetMassCentreY)GetProcAddress(nwDLL,"_phBodyGetMassCentreY@4");
// Use ==> float result = phBodyGetMassCentreY(int body);
 
typedef float(WINAPI *DLL_phBodyGetMassCentreZ)(int body);
DLL_phBodyGetMassCentreZ phBodyGetMassCentreZ;
phBodyGetMassCentreZ = (DLL_phBodyGetMassCentreZ)GetProcAddress(nwDLL,"_phBodyGetMassCentreZ@4");
// Use ==> float result = phBodyGetMassCentreZ(int body);
 
typedef float(WINAPI *DLL_phBodyGetVolume)(int body);
DLL_phBodyGetVolume phBodyGetVolume;
phBodyGetVolume = (DLL_phBodyGetVolume)GetProcAddress(nwDLL,"_phBodyGetVolume@4");
// Use ==> float result = phBodyGetVolume(int body);
 
typedef void(WINAPI *DLL_phBodySetRayCasting)(int body, int raycast);
DLL_phBodySetRayCasting phBodySetRayCasting;
phBodySetRayCasting = (DLL_phBodySetRayCasting)GetProcAddress(nwDLL,"_phBodySetRayCasting@8");
// Use ==> phBodySetRayCasting(int body, int raycast);
 
typedef int(WINAPI *DLL_phBodyGetRayCasting)(int body);
DLL_phBodyGetRayCasting phBodyGetRayCasting;
phBodyGetRayCasting = (DLL_phBodyGetRayCasting)GetProcAddress(nwDLL,"_phBodyGetRayCasting@4");
// Use ==> int result = phBodyGetRayCasting(int body);
 
typedef void(WINAPI *DLL_phLevelBuildBegin)();
DLL_phLevelBuildBegin phLevelBuildBegin;
phLevelBuildBegin = (DLL_phLevelBuildBegin)GetProcAddress(nwDLL,"_phLevelBuildBegin@0");
// Use ==> phLevelBuildBegin();
 
typedef void(WINAPI *DLL_phLevelAddFace)(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3);
DLL_phLevelAddFace phLevelAddFace;
phLevelAddFace = (DLL_phLevelAddFace)GetProcAddress(nwDLL,"_phLevelAddFace@36");
// Use ==> phLevelAddFace(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3);
 
typedef int(WINAPI *DLL_phLevelBuildEnd)();
DLL_phLevelBuildEnd phLevelBuildEnd;
phLevelBuildEnd = (DLL_phLevelBuildEnd)GetProcAddress(nwDLL,"_phLevelBuildEnd@0");
// Use ==> int result = phLevelBuildEnd();
 
typedef int(WINAPI *DLL_phTerrainCreate)(int nSize, float CellWidth, int *height);
DLL_phTerrainCreate phTerrainCreate;
phTerrainCreate = (DLL_phTerrainCreate)GetProcAddress(nwDLL,"_phTerrainCreate@12");
// Use ==> int result = phTerrainCreate(int nSize, float CellWidth, int *height);
 
typedef int(WINAPI *DLL_phJointBallCreate)(float x, float y, float z, int body1, int body2);
DLL_phJointBallCreate phJointBallCreate;
phJointBallCreate = (DLL_phJointBallCreate)GetProcAddress(nwDLL,"_phJointBallCreate@20");
// Use ==> int result = phJointBallCreate(float x, float y, float z, int body1, int body2);
 
typedef void(WINAPI *DLL_phJointBallSetLimit)(int joint, float nx, float ny, float nz, float coneAngle, float twistAngle);
DLL_phJointBallSetLimit phJointBallSetLimit;
phJointBallSetLimit = (DLL_phJointBallSetLimit)GetProcAddress(nwDLL,"_phJointBallSetLimit@24");
// Use ==> phJointBallSetLimit(int joint, float nx, float ny, float nz, float coneAngle, float twistAngle);
 
typedef float(WINAPI *DLL_phJointBallGetForce)(int joint);
DLL_phJointBallGetForce phJointBallGetForce;
phJointBallGetForce = (DLL_phJointBallGetForce)GetProcAddress(nwDLL,"_phJointBallGetForce@4");
// Use ==> float result = phJointBallGetForce(int joint);
 
typedef int(WINAPI *DLL_phJointHingeCreate)(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
DLL_phJointHingeCreate phJointHingeCreate;
phJointHingeCreate = (DLL_phJointHingeCreate)GetProcAddress(nwDLL,"_phJointHingeCreate@32");
// Use ==> int result = phJointHingeCreate(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
 
typedef void(WINAPI *DLL_phJointHingeSetLimit)(int joint, float min, float max);
DLL_phJointHingeSetLimit phJointHingeSetLimit;
phJointHingeSetLimit = (DLL_phJointHingeSetLimit)GetProcAddress(nwDLL,"_phJointHingeSetLimit@12");
// Use ==> phJointHingeSetLimit(int joint, float min, float max);
 
typedef float(WINAPI *DLL_phJointHingeGetAngle)(int joint);
DLL_phJointHingeGetAngle phJointHingeGetAngle;
phJointHingeGetAngle = (DLL_phJointHingeGetAngle)GetProcAddress(nwDLL,"_phJointHingeGetAngle@4");
// Use ==> float result = phJointHingeGetAngle(int joint);
 
typedef float(WINAPI *DLL_phJointHingeGetOmega)(int joint);
DLL_phJointHingeGetOmega phJointHingeGetOmega;
phJointHingeGetOmega = (DLL_phJointHingeGetOmega)GetProcAddress(nwDLL,"_phJointHingeGetOmega@4");
// Use ==> float result = phJointHingeGetOmega(int joint);
 
typedef float(WINAPI *DLL_phJointHingeGetForce)(int joint);
DLL_phJointHingeGetForce phJointHingeGetForce;
phJointHingeGetForce = (DLL_phJointHingeGetForce)GetProcAddress(nwDLL,"_phJointHingeGetForce@4");
// Use ==> float result = phJointHingeGetForce(int joint);
 
typedef int(WINAPI *DLL_phJointSliderCreate)(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
DLL_phJointSliderCreate phJointSliderCreate;
phJointSliderCreate = (DLL_phJointSliderCreate)GetProcAddress(nwDLL,"_phJointSliderCreate@32");
// Use ==> int result = phJointSliderCreate(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
 
typedef void(WINAPI *DLL_phJointSliderSetLimit)(int joint, float min, float max);
DLL_phJointSliderSetLimit phJointSliderSetLimit;
phJointSliderSetLimit = (DLL_phJointSliderSetLimit)GetProcAddress(nwDLL,"_phJointSliderSetLimit@12");
// Use ==> phJointSliderSetLimit(int joint, float min, float max);
 
typedef float(WINAPI *DLL_phJointSliderGetPos)(int joint);
DLL_phJointSliderGetPos phJointSliderGetPos;
phJointSliderGetPos = (DLL_phJointSliderGetPos)GetProcAddress(nwDLL,"_phJointSliderGetPos@4");
// Use ==> float result = phJointSliderGetPos(int joint);
 
typedef float(WINAPI *DLL_phJointSliderGetVel)(int joint);
DLL_phJointSliderGetVel phJointSliderGetVel;
phJointSliderGetVel = (DLL_phJointSliderGetVel)GetProcAddress(nwDLL,"_phJointSliderGetVel@4");
// Use ==> float result = phJointSliderGetVel(int joint);
 
typedef float(WINAPI *DLL_phJointSliderGetForce)(int joint);
DLL_phJointSliderGetForce phJointSliderGetForce;
phJointSliderGetForce = (DLL_phJointSliderGetForce)GetProcAddress(nwDLL,"_phJointSliderGetForce@4");
// Use ==> float result = phJointSliderGetForce(int joint);
 
typedef int(WINAPI *DLL_phJointCorkCreate)(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
DLL_phJointCorkCreate phJointCorkCreate;
phJointCorkCreate = (DLL_phJointCorkCreate)GetProcAddress(nwDLL,"_phJointCorkCreate@32");
// Use ==> int result = phJointCorkCreate(float x, float y, float z, float nx, float ny, float nz, int body1, int body2);
 
typedef void(WINAPI *DLL_phJointCorkSetLimit)(float min, float max, float amin, float amax);
DLL_phJointCorkSetLimit phJointCorkSetLimit;
phJointCorkSetLimit = (DLL_phJointCorkSetLimit)GetProcAddress(nwDLL,"_phJointCorkSetLimit@20");
// Use ==> phJointCorkSetLimit(float min, float max, float amin, float amax);
 
typedef float(WINAPI *DLL_phJointCorkGetPos)(int joint);
DLL_phJointCorkGetPos phJointCorkGetPos;
phJointCorkGetPos = (DLL_phJointCorkGetPos)GetProcAddress(nwDLL,"_phJointCorkGetPos@4");
// Use ==> float result = phJointCorkGetPos(int joint);
 
typedef float(WINAPI *DLL_phJointCorkGetVel)(int joint);
DLL_phJointCorkGetVel phJointCorkGetVel;
phJointCorkGetVel = (DLL_phJointCorkGetVel)GetProcAddress(nwDLL,"_phJointCorkGetVel@4");
// Use ==> float result = phJointCorkGetVel(int joint);
 
typedef float(WINAPI *DLL_phJointCorkGetAngle)(int joint);
DLL_phJointCorkGetAngle phJointCorkGetAngle;
phJointCorkGetAngle = (DLL_phJointCorkGetAngle)GetProcAddress(nwDLL,"_phJointCorkGetAngle@4");
// Use ==> float result = phJointCorkGetAngle(int joint);
 
typedef float(WINAPI *DLL_phJointCorkGetOmega)(int joint);
DLL_phJointCorkGetOmega phJointCorkGetOmega;
phJointCorkGetOmega = (DLL_phJointCorkGetOmega)GetProcAddress(nwDLL,"_phJointCorkGetOmega@4");
// Use ==> float result = phJointCorkGetOmega(int joint);
 
typedef float(WINAPI *DLL_phJointCorkGetForce)(int joint);
DLL_phJointCorkGetForce phJointCorkGetForce;
phJointCorkGetForce = (DLL_phJointCorkGetForce)GetProcAddress(nwDLL,"_phJointCorkGetForce@4");
// Use ==> float result = phJointCorkGetForce(int joint);
 
typedef int(WINAPI *DLL_phJointUpVectorCreate)(float nx, float ny, float nz, int body);
DLL_phJointUpVectorCreate phJointUpVectorCreate;
phJointUpVectorCreate = (DLL_phJointUpVectorCreate)GetProcAddress(nwDLL,"_phJointUpVectorCreate@16");
// Use ==> int result = phJointUpVectorCreate(float nx, float ny, float nz, int body);
 
typedef void(WINAPI *DLL_phJointUpVectorSetPin)(int joint, float nx, float ny, float nz);
DLL_phJointUpVectorSetPin phJointUpVectorSetPin;
phJointUpVectorSetPin = (DLL_phJointUpVectorSetPin)GetProcAddress(nwDLL,"_phJointUpVectorSetPin@16");
// Use ==> phJointUpVectorSetPin(int joint, float nx, float ny, float nz);
 
typedef float(WINAPI *DLL_phJointUpVectorGetPinX)(int joint);
DLL_phJointUpVectorGetPinX phJointUpVectorGetPinX;
phJointUpVectorGetPinX = (DLL_phJointUpVectorGetPinX)GetProcAddress(nwDLL,"_phJointUpVectorGetPinX@4");
// Use ==> float result = phJointUpVectorGetPinX(int joint);
 
typedef float(WINAPI *DLL_phJointUpVectorGetPinY)(int joint);
DLL_phJointUpVectorGetPinY phJointUpVectorGetPinY;
phJointUpVectorGetPinY = (DLL_phJointUpVectorGetPinY)GetProcAddress(nwDLL,"_phJointUpVectorGetPinY@4");
// Use ==> float result = phJointUpVectorGetPinY(int joint);
 
typedef float(WINAPI *DLL_phJointUpVectorGetPinZ)(int joint);
DLL_phJointUpVectorGetPinZ phJointUpVectorGetPinZ;
phJointUpVectorGetPinZ = (DLL_phJointUpVectorGetPinZ)GetProcAddress(nwDLL,"_phJointUpVectorGetPinZ@4");
// Use ==> float result = phJointUpVectorGetPinZ(int joint);
 
typedef int(WINAPI *DLL_phJointUniversalCreate)(float x, float y, float z, float nx1, float ny1, float nz1, float nx2, float ny2, float nz2, int body1, int body2);
DLL_phJointUniversalCreate phJointUniversalCreate;
phJointUniversalCreate = (DLL_phJointUniversalCreate)GetProcAddress(nwDLL,"_phJointUniversalCreate@44");
// Use ==> int result = phJointUniversalCreate(float x, float y, float z, float nx1, float ny1, float nz1, float nx2, float ny2, float nz2, int body1, int body2);
 
typedef float(WINAPI *DLL_phJointUniversalGetAngle1)(int joint);
DLL_phJointUniversalGetAngle1 phJointUniversalGetAngle1;
phJointUniversalGetAngle1 = (DLL_phJointUniversalGetAngle1)GetProcAddress(nwDLL,"_phJointUniversalGetAngle1@4");
// Use ==> float result = phJointUniversalGetAngle1(int joint);
 
typedef float(WINAPI *DLL_phJointUniversalGetAngle2)(int joint);
DLL_phJointUniversalGetAngle2 phJointUniversalGetAngle2;
phJointUniversalGetAngle2 = (DLL_phJointUniversalGetAngle2)GetProcAddress(nwDLL,"_phJointUniversalGetAngle2@4");
// Use ==> float result = phJointUniversalGetAngle2(int joint);
 
typedef float(WINAPI *DLL_phJointUniversalGetOmega1)(int joint);
DLL_phJointUniversalGetOmega1 phJointUniversalGetOmega1;
phJointUniversalGetOmega1 = (DLL_phJointUniversalGetOmega1)GetProcAddress(nwDLL,"_phJointUniversalGetOmega1@4");
// Use ==> float result = phJointUniversalGetOmega1(int joint);
 
typedef float(WINAPI *DLL_phJointUniversalGetOmega2)(int joint);
DLL_phJointUniversalGetOmega2 phJointUniversalGetOmega2;
phJointUniversalGetOmega2 = (DLL_phJointUniversalGetOmega2)GetProcAddress(nwDLL,"_phJointUniversalGetOmega2@4");
// Use ==> float result = phJointUniversalGetOmega2(int joint);
 
typedef float(WINAPI *DLL_phJointUniversalGetForce)(int joint);
DLL_phJointUniversalGetForce phJointUniversalGetForce;
phJointUniversalGetForce = (DLL_phJointUniversalGetForce)GetProcAddress(nwDLL,"_phJointUniversalGetForce@4");
// Use ==> float result = phJointUniversalGetForce(int joint);
 
typedef void(WINAPI *DLL_phJointUniversalSetLimit)(int joint, float min1, float max1, float min2, float max2);
DLL_phJointUniversalSetLimit phJointUniversalSetLimit;
phJointUniversalSetLimit = (DLL_phJointUniversalSetLimit)GetProcAddress(nwDLL,"_phJointUniversalSetLimit@20");
// Use ==> phJointUniversalSetLimit(int joint, float min1, float max1, float min2, float max2);
 
typedef int(WINAPI *DLL_phJointSpringCreate)(float x1, float y1, float z1, float x2, float y2, float z2, float strong, float dampfer, int body1, int body2);
DLL_phJointSpringCreate phJointSpringCreate;
phJointSpringCreate = (DLL_phJointSpringCreate)GetProcAddress(nwDLL,"_phJointSpringCreate@40");
// Use ==> int result = phJointSpringCreate(float x1, float y1, float z1, float x2, float y2, float z2, float strong, float dampfer, int body1, int body2);
 
typedef int(WINAPI *DLL_phJointFixedCreate)(int body1, int body2);
DLL_phJointFixedCreate phJointFixedCreate;
phJointFixedCreate = (DLL_phJointFixedCreate)GetProcAddress(nwDLL,"_phJointFixedCreate@8");
// Use ==> int result = phJointFixedCreate(int body1, int body2);
 
typedef int(WINAPI *DLL_phJointSuspCreate)(float x, float y, float z, float nx1, float ny1, float nz1, float nx2, float ny2, float nz2, float k, float s, int body1, int body2);
DLL_phJointSuspCreate phJointSuspCreate;
phJointSuspCreate = (DLL_phJointSuspCreate)GetProcAddress(nwDLL,"_phJointSuspCreate@52");
// Use ==> int result = phJointSuspCreate(float x, float y, float z, float nx1, float ny1, float nz1, float nx2, float ny2, float nz2, float k, float s, int body1, int body2);
 
typedef void(WINAPI *DLL_phJointSuspSetSteer)(int joint, float steer);
DLL_phJointSuspSetSteer phJointSuspSetSteer;
phJointSuspSetSteer = (DLL_phJointSuspSetSteer)GetProcAddress(nwDLL,"_phJointSuspSetSteer@8");
// Use ==> phJointSuspSetSteer(int joint, float steer);
 
typedef void(WINAPI *DLL_phJointSuspAddTorque)(int joint, float torque);
DLL_phJointSuspAddTorque phJointSuspAddTorque;
phJointSuspAddTorque = (DLL_phJointSuspAddTorque)GetProcAddress(nwDLL,"_phJointSuspAddTorque@8");
// Use ==> phJointSuspAddTorque(int joint, float torque);
 
typedef void(WINAPI *DLL_phJointSuspSetFriction)(int joint, float min, float max);
DLL_phJointSuspSetFriction phJointSuspSetFriction;
phJointSuspSetFriction = (DLL_phJointSuspSetFriction)GetProcAddress(nwDLL,"_phJointSuspSetFriction@12");
// Use ==> phJointSuspSetFriction(int joint, float min, float max);
 
typedef void(WINAPI *DLL_phJointSuspSetLimit)(int joint, float nim, float max);
DLL_phJointSuspSetLimit phJointSuspSetLimit;
phJointSuspSetLimit = (DLL_phJointSuspSetLimit)GetProcAddress(nwDLL,"_phJointSuspSetLimit@12");
// Use ==> phJointSuspSetLimit(int joint, float nim, float max);
 
typedef float(WINAPI *DLL_phJointSuspGetPos)(int joint);
DLL_phJointSuspGetPos phJointSuspGetPos;
phJointSuspGetPos = (DLL_phJointSuspGetPos)GetProcAddress(nwDLL,"_phJointSuspGetPos@4");
// Use ==> float result = phJointSuspGetPos(int joint);
 
typedef void(WINAPI *DLL_phJointDestroy)(int joint);
DLL_phJointDestroy phJointDestroy;
phJointDestroy = (DLL_phJointDestroy)GetProcAddress(nwDLL,"_phJointDestroy@4");
// Use ==> phJointDestroy(int joint);
 
typedef void(WINAPI *DLL_phJointSetCollisionMode)(int joint, int mode);
DLL_phJointSetCollisionMode phJointSetCollisionMode;
phJointSetCollisionMode = (DLL_phJointSetCollisionMode)GetProcAddress(nwDLL,"_phJointSetCollisionMode@8");
// Use ==> phJointSetCollisionMode(int joint, int mode);
 
typedef void(WINAPI *DLL_phJointSetStiffness)(int joint, float stf);
DLL_phJointSetStiffness phJointSetStiffness;
phJointSetStiffness = (DLL_phJointSetStiffness)GetProcAddress(nwDLL,"_phJointSetStiffness@8");
// Use ==> phJointSetStiffness(int joint, float stf);
 
typedef int(WINAPI *DLL_phJointVehicleCreate)(float upx, float upy, float upz, int body);
DLL_phJointVehicleCreate phJointVehicleCreate;
phJointVehicleCreate = (DLL_phJointVehicleCreate)GetProcAddress(nwDLL,"_phJointVehicleCreate@16");
// Use ==> int result = phJointVehicleCreate(float upx, float upy, float upz, int body);
 
typedef int(WINAPI *DLL_phJointVehicleAddTire)(int veh, float dx, float dy, float dz, float nx, float ny, float nz, float mass, float width, float radius, float shock, float spring, float lenght, int mat);
DLL_phJointVehicleAddTire phJointVehicleAddTire;
phJointVehicleAddTire = (DLL_phJointVehicleAddTire)GetProcAddress(nwDLL,"_phJointVehicleAddTire@56");
// Use ==> int result = phJointVehicleAddTire(int veh, float dx, float dy, float dz, float nx, float ny, float nz, float mass, float width, float radius, float shock, float spring, float lenght, int mat);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireX)(int veh, int tire);
DLL_phJointVehicleGetTireX phJointVehicleGetTireX;
phJointVehicleGetTireX = (DLL_phJointVehicleGetTireX)GetProcAddress(nwDLL,"_phJointVehicleGetTireX@8");
// Use ==> float result = phJointVehicleGetTireX(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireY)(int veh, int tire);
DLL_phJointVehicleGetTireY phJointVehicleGetTireY;
phJointVehicleGetTireY = (DLL_phJointVehicleGetTireY)GetProcAddress(nwDLL,"_phJointVehicleGetTireY@8");
// Use ==> float result = phJointVehicleGetTireY(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireZ)(int veh, int tire);
DLL_phJointVehicleGetTireZ phJointVehicleGetTireZ;
phJointVehicleGetTireZ = (DLL_phJointVehicleGetTireZ)GetProcAddress(nwDLL,"_phJointVehicleGetTireZ@8");
// Use ==> float result = phJointVehicleGetTireZ(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTirePitch)(int veh, int tire);
DLL_phJointVehicleGetTirePitch phJointVehicleGetTirePitch;
phJointVehicleGetTirePitch = (DLL_phJointVehicleGetTirePitch)GetProcAddress(nwDLL,"_phJointVehicleGetTirePitch@8");
// Use ==> float result = phJointVehicleGetTirePitch(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireYaw)(int veh, int tire);
DLL_phJointVehicleGetTireYaw phJointVehicleGetTireYaw;
phJointVehicleGetTireYaw = (DLL_phJointVehicleGetTireYaw)GetProcAddress(nwDLL,"_phJointVehicleGetTireYaw@8");
// Use ==> float result = phJointVehicleGetTireYaw(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireRoll)(int veh, int tire);
DLL_phJointVehicleGetTireRoll phJointVehicleGetTireRoll;
phJointVehicleGetTireRoll = (DLL_phJointVehicleGetTireRoll)GetProcAddress(nwDLL,"_phJointVehicleGetTireRoll@8");
// Use ==> float result = phJointVehicleGetTireRoll(int veh, int tire);
 
typedef void(WINAPI *DLL_phJointVehicleSetTireSteer)(int veh, int tire, float steer);
DLL_phJointVehicleSetTireSteer phJointVehicleSetTireSteer;
phJointVehicleSetTireSteer = (DLL_phJointVehicleSetTireSteer)GetProcAddress(nwDLL,"_phJointVehicleSetTireSteer@12");
// Use ==> phJointVehicleSetTireSteer(int veh, int tire, float steer);
 
typedef void(WINAPI *DLL_phJointVehicleAddTireTorque)(int veh, int tire, float torque);
DLL_phJointVehicleAddTireTorque phJointVehicleAddTireTorque;
phJointVehicleAddTireTorque = (DLL_phJointVehicleAddTireTorque)GetProcAddress(nwDLL,"_phJointVehicleAddTireTorque@12");
// Use ==> phJointVehicleAddTireTorque(int veh, int tire, float torque);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireOmega)(int veh, int tire);
DLL_phJointVehicleGetTireOmega phJointVehicleGetTireOmega;
phJointVehicleGetTireOmega = (DLL_phJointVehicleGetTireOmega)GetProcAddress(nwDLL,"_phJointVehicleGetTireOmega@8");
// Use ==> float result = phJointVehicleGetTireOmega(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireLoad)(int veh, int tire);
DLL_phJointVehicleGetTireLoad phJointVehicleGetTireLoad;
phJointVehicleGetTireLoad = (DLL_phJointVehicleGetTireLoad)GetProcAddress(nwDLL,"_phJointVehicleGetTireLoad@8");
// Use ==> float result = phJointVehicleGetTireLoad(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireLateralSpeed)(int veh, int tire);
DLL_phJointVehicleGetTireLateralSpeed phJointVehicleGetTireLateralSpeed;
phJointVehicleGetTireLateralSpeed = (DLL_phJointVehicleGetTireLateralSpeed)GetProcAddress(nwDLL,"_phJointVehicleGetTireLateralSpeed@8");
// Use ==> float result = phJointVehicleGetTireLateralSpeed(int veh, int tire);
 
typedef float(WINAPI *DLL_phJointVehicleGetTireLongitudinalSpeed)(int veh, int tire);
DLL_phJointVehicleGetTireLongitudinalSpeed phJointVehicleGetTireLongitudinalSpeed;
phJointVehicleGetTireLongitudinalSpeed = (DLL_phJointVehicleGetTireLongitudinalSpeed)GetProcAddress(nwDLL,"_phJointVehicleGetTireLongitudinalSpeed@8");
// Use ==> float result = phJointVehicleGetTireLongitudinalSpeed(int veh, int tire);
 
typedef int(WINAPI *DLL_phJointVehicleGetTireAir)(int veh, int tire);
DLL_phJointVehicleGetTireAir phJointVehicleGetTireAir;
phJointVehicleGetTireAir = (DLL_phJointVehicleGetTireAir)GetProcAddress(nwDLL,"_phJointVehicleGetTireAir@8");
// Use ==> int result = phJointVehicleGetTireAir(int veh, int tire);
 
typedef int(WINAPI *DLL_phJointVehicleGetTireGrip)(int veh, int tire);
DLL_phJointVehicleGetTireGrip phJointVehicleGetTireGrip;
phJointVehicleGetTireGrip = (DLL_phJointVehicleGetTireGrip)GetProcAddress(nwDLL,"_phJointVehicleGetTireGrip@8");
// Use ==> int result = phJointVehicleGetTireGrip(int veh, int tire);
 
typedef int(WINAPI *DLL_phJointVehicleGetTireTraction)(int veh, int tire);
DLL_phJointVehicleGetTireTraction phJointVehicleGetTireTraction;
phJointVehicleGetTireTraction = (DLL_phJointVehicleGetTireTraction)GetProcAddress(nwDLL,"_phJointVehicleGetTireTraction@8");
// Use ==> int result = phJointVehicleGetTireTraction(int veh, int tire);
 
typedef void(WINAPI *DLL_phJointVehicleSetTireBrake)(int veh, int tire, float acc, float fric);
DLL_phJointVehicleSetTireBrake phJointVehicleSetTireBrake;
phJointVehicleSetTireBrake = (DLL_phJointVehicleSetTireBrake)GetProcAddress(nwDLL,"_phJointVehicleSetTireBrake@16");
// Use ==> phJointVehicleSetTireBrake(int veh, int tire, float acc, float fric);
 
typedef void(WINAPI *DLL_phJointVehicleSetTireSliding)(int veh, int tire, float sidevel, float sidek, float longvel, float longk);
DLL_phJointVehicleSetTireSliding phJointVehicleSetTireSliding;
phJointVehicleSetTireSliding = (DLL_phJointVehicleSetTireSliding)GetProcAddress(nwDLL,"_phJointVehicleSetTireSliding@24");
// Use ==> phJointVehicleSetTireSliding(int veh, int tire, float sidevel, float sidek, float longvel, float longk);
 
typedef void(WINAPI *DLL_phJointVehicleReset)(int veh);
DLL_phJointVehicleReset phJointVehicleReset;
phJointVehicleReset = (DLL_phJointVehicleReset)GetProcAddress(nwDLL,"_phJointVehicleReset@4");
// Use ==> phJointVehicleReset(int veh);
 
typedef int(WINAPI *DLL_phJointDryRollingCreate)(int body, float radius, float friction);
DLL_phJointDryRollingCreate phJointDryRollingCreate;
phJointDryRollingCreate = (DLL_phJointDryRollingCreate)GetProcAddress(nwDLL,"_phJointDryRollingCreate@12");
// Use ==> int result = phJointDryRollingCreate(int body, float radius, float friction);
 
typedef int(WINAPI *DLL_phJointDryRollingSetFriction)(int joint, float radius, float friction);
DLL_phJointDryRollingSetFriction phJointDryRollingSetFriction;
phJointDryRollingSetFriction = (DLL_phJointDryRollingSetFriction)GetProcAddress(nwDLL,"_phJointDryRollingSetFriction@12");
// Use ==> int result = phJointDryRollingSetFriction(int joint, float radius, float friction);
 
typedef int(WINAPI *DLL_phJointPlaneCreate)(int body, float x, float y, float z, float nx, float ny, float nz);
DLL_phJointPlaneCreate phJointPlaneCreate;
phJointPlaneCreate = (DLL_phJointPlaneCreate)GetProcAddress(nwDLL,"_phJointPlaneCreate@28");
// Use ==> int result = phJointPlaneCreate(int body, float x, float y, float z, float nx, float ny, float nz);
 
typedef int(WINAPI *DLL_phRayCast)(float x1, float y1, float z1, float x2, float y2, float z2);
DLL_phRayCast phRayCast;
phRayCast = (DLL_phRayCast)GetProcAddress(nwDLL,"_phRayCast@24");
// Use ==> int result = phRayCast(float x1, float y1, float z1, float x2, float y2, float z2);
 
typedef float(WINAPI *DLL_phRayGetX)();
DLL_phRayGetX phRayGetX;
phRayGetX = (DLL_phRayGetX)GetProcAddress(nwDLL,"_phRayGetX@0");
// Use ==> float result = phRayGetX();
 
typedef float(WINAPI *DLL_phRayGetY)();
DLL_phRayGetY phRayGetY;
phRayGetY = (DLL_phRayGetY)GetProcAddress(nwDLL,"_phRayGetY@0");
// Use ==> float result = phRayGetY();
 
typedef float(WINAPI *DLL_phRayGetZ)();
DLL_phRayGetZ phRayGetZ;
phRayGetZ = (DLL_phRayGetZ)GetProcAddress(nwDLL,"_phRayGetZ@0");
// Use ==> float result = phRayGetZ();
 
typedef float(WINAPI *DLL_phRayGetNX)();
DLL_phRayGetNX phRayGetNX;
phRayGetNX = (DLL_phRayGetNX)GetProcAddress(nwDLL,"_phRayGetNX@0");
// Use ==> float result = phRayGetNX();
 
typedef float(WINAPI *DLL_phRayGetNY)();
DLL_phRayGetNY phRayGetNY;
phRayGetNY = (DLL_phRayGetNY)GetProcAddress(nwDLL,"_phRayGetNY@0");
// Use ==> float result = phRayGetNY();
 
typedef float(WINAPI *DLL_phRayGetNZ)();
DLL_phRayGetNZ phRayGetNZ;
phRayGetNZ = (DLL_phRayGetNZ)GetProcAddress(nwDLL,"_phRayGetNZ@0");
// Use ==> float result = phRayGetNZ();
 
typedef int(WINAPI *DLL_phRayGetBody)();
DLL_phRayGetBody phRayGetBody;
phRayGetBody = (DLL_phRayGetBody)GetProcAddress(nwDLL,"_phRayGetBody@0");
// Use ==> int result = phRayGetBody();
