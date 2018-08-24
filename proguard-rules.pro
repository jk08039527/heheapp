#---------------------基础部分,基本不用改动---------------------------------------------
-verbose
-dontpreverify
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-keepattributes Signature
-keepattributes *Annotation*
-printmapping proguardMapping.txt
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-optimizations !method/marking/static,!method/removal/parameter,!code/removal/advanced
#-----------------------------------------------------------------------------------



#--------------------默认保留区域----------------------------------------------------
-keep public class android.os.Bundle
-keep public class android.content.Intent
-keep public class * extends android.app.Service
-keep public class * extends android.app.Activity
-keep public class * extends android.os.IInterface
-keep public class * extends android.app.Application
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keep class android.support.** { *; }
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}

-keep public class **.R$* {
    *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
    void *(**On*Event);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}
#------------------------------------------------------------------------------------

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

