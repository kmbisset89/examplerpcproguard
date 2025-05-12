
# ProGuard rules
# Based on requirements: Kotlin Metadata, Kotlin Serialization, Kotlin RPC (Reflection), Logback, keep com.jtaps.coreservice
-dontshrink
-dontoptimize
-dontobfuscate
-printmapping mapping.txt
-keep class kotlin.Metadata
-keep class kotlin.jvm.internal.** { *; }
-keepattributes *Annotation*
-flattenpackagehierarchy false

#-------------------------------------------------------------------------------
# General Kotlin Rules
#-------------------------------------------------------------------------------
# Keep essential Kotlin attributes
#-keepattributes Signature,InnerClasses,RuntimeVisibleAnnotations,AnnotationDefault,EnclosingMethod,RuntimeInvisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations,RuntimeVisibleTypeAnnotations,RuntimeInvisibleTypeAnnotations,*Annotation*,RuntimeVisibleAnnotations,SourceFile,SourceDir,Record,Synthetic,MethodParameters,Signature
-keepattributes *
# Keep Kotlin stdlib classes potentially used via reflection
-keep class kotlin.reflect.** { *; }
-keep class kotlin.jvm.internal.** { *; }

# Keep suspend functions and continuations for Coroutines
-keep class kotlin.jvm.internal.** { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlinx.coroutines.** { *; }

#-------------------------------------------------------------------------------
# Kotlinx Serialization Rules
#-------------------------------------------------------------------------------
# Keep classes annotated with @Serializable and related annotations
-keep @kotlinx.serialization.Serializable class * { *; }

#-------------------------------------------------------------------------------
# Logback / SLF4J Rules
#-------------------------------------------------------------------------------
# Keep Logback and SLF4J core classes
-keep class ch.qos.logback.** { *; }
-keep class org.slf4j.** { *; }

# Keep specific members needed by Logback/SLF4J
-keepclassmembers class ch.qos.logback.** { *; }
-keepclassmembers class org.slf4j.** { *; }

# Keep native methods if any (safety measure)
-keepclasseswithmembernames class * {
    native <methods>;
}

#-------------------------------------------------------------------------------
# Keep Specific Package: com.example.rpc
#-------------------------------------------------------------------------------
# Keep all classes, interfaces, enums, annotations and their members in the specified package
-keep class com.example.rpc.** { *; }
-keep interface com.example.rpc.** { *; }
-keep enum com.example.rpc.** { *; }
-keep @interface com.example.rpc.** { *; }

-keepclassmembers class com.example.rpc.** { *; }

-keep class mil.jtaps.exampleapp.**

-keepclassmembers interface com.example.rpc.** { *; }
-keepclassmembers enum com.example.rpc.** { *; }
-keepclassmembers @interface com.example.rpc.** { *; }
-keep @kotlinx.rpc.annotations.Rpc interface com.example.rpc.** { *; }


#-------------------------------------------------------------------------------
# Koin Rules (Based on libs.toml)
#-------------------------------------------------------------------------------
-dontwarn kotlinx.coroutines.debug.*

-keep class app.cash.sqldelight.** { *; }
-keep class ch.qos.logback.** { *; }
-keep class com.google.code.gson.** { *; }
-keep class com.google.gson.** { *; }
-keep class org.json.** { *; }

-keep class org.lwjgl.** { *; }
-keep class org.objenesis.** { *; }
-keep class okio.** { *; }
-keep class uk.co.caprica.** { *; }

-keep class kotlinx.rpc.** { *; }
# Keep them completely (no shrinking, no obfuscation, no optimisation)
-keep class **$$RpcServiceStub           { *; }
-keep class **$$RpcServiceImpl           { *; }
-keep class **$$RpcMethod*               { *; }

# If your generator uses the lowercase pattern add these too
-keep class **$$rpcServiceStub           { *; }
-keep class **$$rpcServiceImpl           { *; }
-keep class **$$rpcMethod*               { *; }

-if @kotlinx.rpc.annotations.Rpc interface **
-keep class <1>$$RpcServiceStub { *; }

-keep class org.jetbrains.** { *; }
-keep class androidx.** { *; }
-keep class androidx.datastore.preferences.** { *; }
-keep class javax.sql.** { *; }
-keep class org.sqlite.** { *; }

-keep class java.sql.** { *; }
-keep public class org.slf4j.** { *; }
-keep public class ch.** { *; }
# Keep javax.mail classes
-keep class javax.mail.** { *; }
-keep class javax.naming.** { *; }
-keep class javax.servlet.** { *; }

# Keep classes that could use javax.mail
-keep class my.package.using.mail.** { *; }

-keep class org.apache.commons.logging.** { *; }
-keep class org.apache.logging.** { *; }
-keep class org.openxmlformats.schemas.** { *; }

-keep class org.apache.log4j.** { *; }
-keep class org.apache.log.** { *; }
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }


#-------------------------------------------------------------------------------
# Suppress Warnings (Use with caution - ideally fix underlying issues)
#-------------------------------------------------------------------------------
# Suppress warnings about duplicate class definitions found in logs.
# This is often due to dependency conflicts and should ideally be resolved in Gradle.
-dontwarn org.koin.ksp.generated.**
-dontwarn com.jtaps.coreservice.network.StatusChangeNotifier

# Note: ProGuard doesn't have a specific -dontwarn for duplicate *resource* files (like MANIFEST.MF).
# These notes are informational and usually don't stop the build, but indicate packaging issues.

-dontwarn io.netty.internal.tcnative.AsyncSSLPrivateKeyMethod
-dontwarn io.netty.internal.tcnative.AsyncTask
-dontwarn io.netty.internal.tcnative.Buffer
-dontwarn io.netty.internal.tcnative.CertificateCallback
-dontwarn io.netty.internal.tcnative.CertificateCompressionAlgo
-dontwarn io.netty.internal.tcnative.CertificateVerifier
-dontwarn io.netty.internal.tcnative.Library
-dontwarn io.netty.internal.tcnative.SSL
-dontwarn io.netty.internal.tcnative.SSLContext
-dontwarn io.netty.internal.tcnative.SSLPrivateKeyMethod
-dontwarn io.netty.internal.tcnative.SSLSessionCache
-dontwarn io.netty.internal.tcnative.SessionTicketKey
-dontwarn io.netty.internal.tcnative.SniHostNameMatcher
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.apache.logging.log4j.Level
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.logging.log4j.message.MessageFactory
-dontwarn org.apache.logging.log4j.spi.ExtendedLogger
-dontwarn org.apache.logging.log4j.spi.ExtendedLoggerWrapper
-dontwarn org.bouncycastle.asn1.pkcs.PrivateKeyInfo
-dontwarn org.bouncycastle.openssl.PEMDecryptorProvider
-dontwarn org.bouncycastle.openssl.PEMEncryptedKeyPair
-dontwarn org.bouncycastle.openssl.PEMKeyPair
-dontwarn org.bouncycastle.openssl.PEMParser
-dontwarn org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
-dontwarn org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder
-dontwarn org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
-dontwarn org.bouncycastle.operator.InputDecryptorProvider
-dontwarn org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo
-dontwarn org.conscrypt.BufferAllocator
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.HandshakeListener
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ClientProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$Provider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ServerProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego

-dontwarn io.netty.**
-dontwarn io.ktor.**
-dontwarn net.codecrete.**
-dontwarn androidx.compose.ui.scene.ComposeSceneMediator$DesktopSemanticsOwnerListener
-dontwarn kotlin.jvm.internal.**
-dontwarn ch.qos.logback.**
-dontwarn org.lwjgl.**
-dontwarn com.boss.transport.**
-dontnote com.jtaps.sdk.**
-dontnote *MANIFEST.MF*
-dontwarn net.tactware.link16.**
-dontwarn com.fazecast.jSerialComm.**
-dontwarn kotlin.concurrent.atomics.**
-dontwarn io.github.oshai.kotlinlogging.**
