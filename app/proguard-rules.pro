# Butterknife
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

#Picasso
-dontwarn com.squareup.okhttp.**

#Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn okio.
-dontwarn okio.**
-keep class com.squareup.okhttp3.** {
*;
}
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp.** {
*;
}

-keepattributes Annotation

-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }

#model
-keep class zmuzik.slidingpuzzle2.repo.flickr.** { *; }
-keep class zmuzik.slidingpuzzle2.common.Keys
