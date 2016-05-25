/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/* This is a JNI example where we use native methods to play sounds
 * using OpenSL ES. See the corresponding Java source file located at:
 *
 *   src/com/example/nativeaudio/NativeAudio/NativeAudio.java
 */

#include <assert.h>
#include <jni.h>
#include <string.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <float.h>

#include "defines.h"
#include "module1.h"
#include "module2.h"
#include "module3.h"
#include "mapping.h"

void usagepseam (void);
float PrintResults(char *pcProcessFilename,p563Results_struct *ptResults,char *pcResultFilename,char *pcAdditionalInfo);
int PostProcessMovs(int *PartitionNumber, FLOAT *PredictedMos, 	p563Results_struct *ptResults);
void floatToString(float n, char *res, int afterpoint);
float p563_MOS(int argc, char * argv[]);

float p563_MOS(int argc, char * argv[]) {
    p563Results_struct tResults = {-1};

    INT32	i;
    short int * speech_samples;     /* Array of speech samples                               */
    INT32 file_length;           /* Number of samples in speech file                      */
    INT32 num_read;				/* number of elements read from file                     */

    char* PassThroughParameters= "";
    char pSpeechFilename[100];
    char *pResultFileName="stdout";



    FILE *speech_file;

    bool RefFileSet=FALSE;
    bool PassTitlesSet = FALSE;



    int PartitionNumber;
    FLOAT PredictedMos;


    /* Open speech file and results file               */

/*	_controlfp(_EM_INEXACT|_EM_DENORMAL|_EM_UNDERFLOW,_MCW_EM); */
    if (argc < 2)	{
        usagepseam();
        exit(-1);
    }

    strcpy(pSpeechFilename,argv[1]);
    RefFileSet=TRUE;

    for (i=2; i<argc; i++)
    {
        if (!strcmp(argv[i], "-out"))
        {
            i++;
            if (i<argc) pResultFileName = argv[i];
        }
        else
        {
            if      (!RefFileSet)    { strcpy(pSpeechFilename,argv[i]); RefFileSet=TRUE;}
            else if (!PassTitlesSet) { PassThroughParameters = argv[i]; PassTitlesSet = TRUE;}
        }
    }

    /* open speech file */
    if((speech_file = fopen(pSpeechFilename, "rb")) == NULL )
    {
        printf( "Cannot open file %s\n", pSpeechFilename ) ;
        exit (-1) ;
    }

    /* Read speech samples from file */
    fseek(speech_file, 0 , SEEK_END) ;
    file_length = (ftell(speech_file))/(sizeof(short int)) ;
    fseek(speech_file, 0 , SEEK_SET) ;

    speech_samples  = (short int *)calloc(file_length, sizeof(short int)) ;
    num_read = fread(speech_samples, sizeof(short int), file_length, speech_file);

    if(num_read != file_length)
    {
        printf( "Unable to read all the data from file\n" );
        exit(-1);
    }

/*************************
*     PROCESS MODEL      *
*************************/

    module1(speech_samples, file_length, &tResults);

    module2(speech_samples, file_length, &tResults);

    module3(speech_samples, file_length, &tResults);

    PostProcessMovs( &PartitionNumber, &PredictedMos, &tResults);

    float resultadoMos = PrintResults(pSpeechFilename,&tResults,pResultFileName,PassThroughParameters);
    //p563Results_struct *ptResults = &PredictedMos;
    //float resultadoMos = ptResults->fPredictedMos;
    //printf("Resultado mos1: %f\n\n",resultadoMos);
    /* Clean up*/
    fclose(speech_file) ;
    free(speech_samples);

    return resultadoMos;
}


float PrintResults(char *pcProcessFilename,p563Results_struct *ptResults,char *pcResultFilename,char *pcAdditionalInfo)
{
    FILE *hFile=NULL;
    INT32 lWriteHeaderFlag=0;

    float resultado = 0.0;
    if (pcResultFilename!=NULL)
    {
        if(!strcmp(pcResultFilename,"stdout"))
        {
            hFile=stdout;
            lWriteHeaderFlag=1;
        }
        else
        {
            if((hFile = fopen(pcResultFilename, "r")) == NULL )
            {
                lWriteHeaderFlag=1;
            }
            else fclose(hFile);
            if ( (hFile = fopen(pcResultFilename, "a")) == NULL) /* If the file cannot be found */
            {
                printf("\n'%s' cannot be opened for writing!\n\n",pcResultFilename);
                return -1;
            }
        }

        if (lWriteHeaderFlag){
            //fprintf(hFile,"Filename\t");
            //fprintf(hFile,"MOS\n");
        }
        //fprintf(hFile, "%s\t",pcProcessFilename);

        //fprintf(hFile,"%f\t",ptResults->fPredictedMos);
        resultado=ptResults->fPredictedMos;

        if (pcAdditionalInfo!=NULL) fprintf(hFile, pcAdditionalInfo);
        fprintf(hFile,"\n");
        if(strcmp(pcResultFilename,"stdout"))
        {
            fclose(hFile);
        }
    }
    return resultado;
}


void usagepseam (void)
{
    printf ("Usage:  p563 <SpeechFile> [-out <ResultFile>] <Parameters passed through to resultfile>\n");
}



int PostProcessMovs(int *PartitionNumber, FLOAT *PredictedMos, 	p563Results_struct *ptResults)
{

    GetPartitionNumber( PartitionNumber, ptResults);
    CalculateOverallMapping( PartitionNumber,ptResults, PredictedMos);

    ptResults->lPartition=*PartitionNumber;
    ptResults->fPredictedMos=*PredictedMos;

    return 1;
}

// reverses a string 'str' of length 'len'
void reverse(char *str, int len)
{
    int i=0, j=len-1, temp;
    while (i<j)
    {
        temp = str[i];
        str[i] = str[j];
        str[j] = temp;
        i++; j--;
    }
}

// Converts a given integer x to string str[].  d is the number
// of digits required in output. If d is more than the number
// of digits in x, then 0s are added at the beginning.
int intToStr(int x, char str[], int d)
{
    int i = 0;
    while (x)
    {
        str[i++] = (x%10) + '0';
        x = x/10;
    }

    // If number of digits required is more, then
    // add 0s at the beginning
    while (i < d)
        str[i++] = '0';

    reverse(str, i);
    str[i] = '\0';
    return i;
}


void floatToString(float n, char *res, int afterpoint) {
    // Extract integer part
    int ipart = (int)n;

    // Extract floating part
    float fpart = n - (float)ipart;

    // convert integer part to string
    int i = intToStr(ipart, res, 0);

    // check for display option after point
    if (afterpoint != 0)
    {
        res[i] = '.';  // add dot

        // Get the value of fraction part upto given no.
        // of points after dot. The third parameter is needed
        // to handle cases like 233.007
        fpart = fpart * pow(10, afterpoint);

        intToStr((int)fpart, res + i + 1, afterpoint);
    }
}
/*
jstring Java_com_example_nativeaudio_P563Executer_p562(JNIEnv* env, jclass clazz,jstring jstr) {
    const char * path;
    path = (*env)->GetStringUTFChars( env, jstr , NULL ) ;
    const char * parametros[1];
    parametros[0] = path;

    char * param[1];
    param[0] = (char *)parametros[0];
    //o segundo parametro da funcao abaixo eh sempre o tamanho do vetor parametros +1.
    float result = p563_MOS((int)2,param);

    char * res;
    floatToString(result,res,(int)6);
    jstring resultado = (*env)->NewStringUTF(env,res);
    return resultado;

}*/

void float2String(float num, char * output[]){
    //double num=123412341234.123456789;
    //char output[50];
    snprintf(output,50,"%f",num);
    printf("%s",output);
}

//JNIEXPORT float JNICALL Java_com_example_nativeaudio_P563Executer_p563Float(JNIEnv* env, jclass clazz,jstring jstr) {
JNIEXPORT float JNICALL Java_br_ufla_deg_rodrigodantas_csipsimple_p563_P563Executer_p563Float(JNIEnv* env, jclass clazz,jstring jstr) {
    const char * path;
    path = (*env)->GetStringUTFChars( env, jstr , NULL ) ;
    const char * parametros[1];
    parametros[0] = path;

    char * param[1];
    param[0] = (char *)parametros[0];
    //o segundo parametro da funcao abaixo eh sempre o tamanho do vetor parametros +1.
    float result = p563_MOS((int)2,param);
/*
    char * res;
    floatToString(result,res,(int)6);
    jstring resultado = (*env)->NewStringUTF(env,res);*/
    jfloat n = (jfloat)result;
    return n;
}
//JNIEXPORT jstring JNICALL Java_com_example_nativeaudio_P563Executer_p563(JNIEnv* env, jclass clazz,jstring jstr){
JNIEXPORT jstring JNICALL Java_br_ufla_deg_rodrigodantas_csipsimple_p563_P563Executer_p563(JNIEnv* env, jclass clazz,jstring jstr){
    const char * path;
    path = (*env)->GetStringUTFChars(env,jstr,NULL);
    const char * parametros[1];
    parametros[0] = path;

    char * param[1];
    param[0] = (char *)parametros[0];
    //o segundo parametro da funcao abaixo eh sempre o tamanho do vetor parametros +1.
    float result = p563_MOS((int)2,param);

    char * valorMOS[50];
    float2String(result, valorMOS);
    //floatToString(result,res,(int)6);
    jstring resultado = (*env)->NewStringUTF(env, valorMOS);

    return resultado;
}

JNIEXPORT jstring JNICALL Java_br_ufla_deg_rodrigodantas_csipsimple_p563_P563Executer_teste(JNIEnv* env, jclass clazz,jstring jstr,jstring jstr2){

    const char * path;
    path = (*env)->GetStringUTFChars(env,jstr,NULL);
    const char * parametros[1];
    parametros[0] = path;

    char * param[1];
    param[0] = (char *)parametros[0];
    //o segundo parametro da funcao abaixo eh sempre o tamanho do vetor parametros +1.
    float result = p563_MOS((int)2,param);

    char * valorMOS;
    float2String(result, valorMOS);
    //floatToString(result,res,(int)6);
    jstring resultado = (*env)->NewStringUTF(env, valorMOS);

    return resultado;
}