Modified CSipSimple
==========

-CSipSimple for Android Studio Project
-This application was modified to increase functionality to evaluation voice quality based on Mean Opinion Score (MOS) and this modification is part of MS degree work of Rodrigo Dantas Nunes at Federal University of Lavras-MG, Brazil.

##change

* /res/values/wizard_sipgate_string.xml 
    
        <string  xm:lns:ns0="http://schemas.android.com/tools" /> -
        <resources xmlns:ns0="http://schemas.android.com/tools"/> +

* delete jni folder
* add jniLibs folder and  org.pjsip.pjsua to java folder
* delete about quote UtilityWrapper


##reference

* com.actionbarsherlock:actionbarsherlock:4.4.0@aar


