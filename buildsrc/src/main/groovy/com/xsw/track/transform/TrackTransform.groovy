package com.xsw.track.transform

import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.xsw.track.config.TrackConfig
import com.xsw.track.global.TrackGlobal
import com.xsw.track.util.Log
import com.xsw.track.util.ModifyClassUtil
import com.xsw.track.util.Utils
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class TrackTransform extends Transform {

    private static final CLASS_SUFFIX = ".class"
    private static final ANNOTATION_MEASURE_TIME_MILLIS = "com.xiaosw.api.annotation.MeasureTimeMillis"

    private Project mTarget
    // android 闭包
    private AppExtension mAppExtension
    private String applicationId

    TrackTransform(Project target) {
        mTarget = target
        mAppExtension = target.extensions.getByType(AppExtension)
        applicationId = getAppPackageName()
        if (applicationId != null) {
            TrackConfig.addTrackTargetPackage(applicationId)
        }
        // LayoutFactory
        TrackConfig.addTrackTargetPackage("androidx.appcompat.app.AppCompatDelegateImpl")
        TrackConfig.addTrackTargetPackage("android.support.v7.app.AppCompatDelegateImplV9")
    }

    @Override
    String getName() {
        return "XswTrack"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        transformLocked(transformInvocation)
    }

    private void transformLocked(TransformInvocation transformInvocation) {
        def isTrack = TrackConfig.track
        Log.i("************* track transform enter: $isTrack *************")
        TrackConfig.trackTargetPackages.forEach() {
            Log.i("track target package: $it")
        }
        final def context = transformInvocation.context
        final def inputs = transformInvocation.inputs
        final def outputProvider = transformInvocation.outputProvider
        // 获取所有classpath
        def classPathList = []
        inputs.each { input ->
            input.directoryInputs.each {
                def absolutePath = it.file.absolutePath
                classPathList.add(it.file.absolutePath)
                // Log.i("class dir: $absolutePath")
            }

            input.jarInputs.each {
                def absolutePath = it.file.absolutePath
                classPathList.add(absolutePath)
                // Log.i("jar dir: $absolutePath")
            }
        }
        // xxx/platforms\android-${version}\android.jar
        def bootClasspath0 = mAppExtension.bootClasspath[0].absolutePath
        classPathList.add(bootClasspath0)

        // copy
        inputs.each { input ->
            input.jarInputs.each { jarInput ->
                def absolutePath = jarInput.file.absolutePath
                def jarName = jarInput.file.name
                def _suffix = DigestUtils.md5Hex(absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                final def newJarName = "${jarName}_${_suffix}"
                // dest output file
                final def distDir = outputProvider.getContentLocation(newJarName,
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR)
                def modifyJar
                if (checkJarWhetherNeedModify(jarInput)) {
                    modifyJar = modifyJarFile(jarInput.file, context.getTemporaryDir())
                }
                if (null == modifyJar) {
                    modifyJar = jarInput.file
                } else {
                    saveModifiedJarForCheck(modifyJar)
                }
                // Log.i("copy from [${fromFile.absolutePath}] to [${toFile.absolutePath}]")
                FileUtils.copyFile(modifyJar, distDir)
            }

            input.directoryInputs.each { directoryInput ->
                final def classDir = directoryInput.file
                if (null != classDir) {
                    HashMap<String, File> modifyMap = new HashMap<>();
                    classDir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { classFile ->
                        def modifyFile = modifyClassFileIfNeeded(classDir, classFile, context.getTemporaryDir())
                        if (null != modifyFile) {
                            Log.i("modifyFile: ${modifyFile.absolutePath}")
                            modifyMap.put(classFile.absolutePath.replace(classDir.absolutePath, ""), modifyFile)
                        }
                    }
                    final def distDir = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.getContentTypes(),
                            directoryInput.getScopes(),
                            Format.DIRECTORY)
                    FileUtils.copyDirectory(directoryInput.file, distDir)
                    modifyMap.entrySet().each {
                        File target = new File(distDir.absolutePath + it.getKey())
                        if (target.exists()) {
                            target.delete()
                        }
                        final def src = it.getValue()
                        // Log.e("src = $src, target = $target")
                        FileUtils.copyFile(src, target)
                        saveModifiedJarForCheck(src)
                        src.delete()
                    }
                }
            }
        }
    }

    private static void saveModifiedJarForCheck(File optJar) {
        File dir = TrackGlobal.global.TransformTempDir
        File checkJarFile = new File(dir, optJar.getName())
        if (checkJarFile.exists()) {
            checkJarFile.delete()
        }
        // Log.e("optJar = $optJar, checkJarFile = $checkJarFile")
        FileUtils.copyFile(optJar, checkJarFile)
    }

    private boolean checkJarWhetherNeedModify(JarInput jarInput) {
        if (null == jarInput) {
            return false
        }
        final def jarFile = jarInput.file
        if (null == jarFile || !jarFile.exists()) {
            return false
        }
        // read jar
        final JarFile readFile = new JarFile(jarFile)
        final String jarDir = jarFile.absolutePath + File.separator
        try {
            def entries = readFile.entries()
            while (entries.hasMoreElements()) {
                def jarEntry = entries.nextElement()
                def entryName = jarEntry.name
                def classFullName = absolutePath2FullClassName(jarDir, entryName)
                if (null == classFullName) {
                    continue
                }
                if (whetherClassNeedModify(classFullName, "jar")) {
                    return true
                }
            }
        } finally {
            readFile.close()
        }
        return false
    }

    private File modifyClassFileIfNeeded(File classDir, File classFile, File tempDir) {
        final def fullClassName = file2FullClassName(classDir, classFile)
        final def whetherClassNeedModify = whetherClassNeedModify(fullClassName, "dir")
        if (!whetherClassNeedModify) {
            return null
        }
        byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
        def modifiedClassBytes = ModifyClassUtil.modifyClass(fullClassName, sourceClassBytes)
        if (null != modifiedClassBytes) {
            File modifyFile = new File(tempDir, fullClassName.concat(CLASS_SUFFIX))
            if (modifyFile.exists()) {
                modifyFile.delete()
            }
            modifyFile.createNewFile()
            FileOutputStream fos = new FileOutputStream(modifyFile)
            try {
                fos.write(modifiedClassBytes)
                return modifyFile
            } catch(Exception e) {
                Log.e(e)
            } finally {
                fos.close()
            }
        }
        return null
    }

    private String file2FullClassName(File parent, File file) {
        if (null == file) {
            return null
        }
        return absolutePath2FullClassName(parent,
                file.absolutePath)
    }

    private String absolutePath2FullClassName(File ignoreParent, String absolutePath) {
        String ignore
        if (ignoreParent != null) {
            ignore = ignoreParent.absolutePath + File.separator
        } else {
            ignore = ""
        }
        return absolutePath2FullClassName(ignore, absolutePath)
    }

    private String absolutePath2FullClassName(String ignoreParent, String absolutePath) {
        if (null == absolutePath || !absolutePath.endsWith(CLASS_SUFFIX)) {
            return null
        }
        if (ignoreParent != null) {
            absolutePath = absolutePath.replace(ignoreParent, "")
        }
        return absolutePath.replace(File.separator, ".")
                .replace("/", ".")
                .substring(0, absolutePath.length() - CLASS_SUFFIX.length())
    }

    private boolean whetherClassNeedModify(String fullClassName, String from) {
        if (!TrackConfig.track) {
            return false
        }
        if (null == fullClassName
                || fullClassName.endsWith(".BuildConfig")
                || fullClassName.endsWith(".R")
                || fullClassName.contains(".R\$")) {
            return false
        }
        def trackTargetPackages = TrackConfig.trackTargetPackages
        if (trackTargetPackages.isEmpty()) {
            return false
        }
        for (String targetPackage: trackTargetPackages) {
            if (fullClassName.contains(targetPackage)) {
                return true
            }
        }
        return false
    }

    private File modifyJarFile(File jarFile, File tempDir) {
        // Log.e("---------> jarFile = $jarFile, tempDir = $tempDir")
        if (null == jarFile) {
            return null
        }
        /** 设置输出到的jar */
        def final hexName = DigestUtils.md5Hex(jarFile.absolutePath)
        def final optJar = new File(tempDir, hexName.concat("_").concat(jarFile.name))
        def final jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        /**
         * 读取原jar
         */
        def final file = new JarFile(jarFile)
        try {
            def final enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                def final jarEntry = (JarEntry) enumeration.nextElement()
                def final inputStream = file.getInputStream(jarEntry)
                def final entryName = jarEntry.getName()
                def final zipEntry = new ZipEntry(entryName)
                jarOutputStream.putNextEntry(zipEntry)

                byte[] modifiedClassBytes = null
                final def sourceClassBytes = IOUtils.toByteArray(inputStream)
                def final className = absolutePath2FullClassName("", entryName)
                if (entryName.endsWith(CLASS_SUFFIX)) {
                    if (whetherClassNeedModify(className, "jar file")) {
                        modifiedClassBytes = ModifyClassUtil.modifyClass(className,
                                sourceClassBytes)
                    }
                }
                if (modifiedClassBytes == null) {
                    jarOutputStream.write(sourceClassBytes)
                } else {
//                    def tempFile = new File("C:\\Users\\admin\\Desktop\\temp", className.concat(CLASS_SUFFIX))
//                    if (tempFile.exists()) {
//                        tempFile.delete()
//                    }
//                    tempFile.createNewFile()
//                    def os = new FileOutputStream(tempFile)
//                    os.write(modifiedClassBytes)
//                    os.close()
                    jarOutputStream.write(modifiedClassBytes)
                }
                jarOutputStream.closeEntry()
            }
            Log.e("------- ${optJar.absolutePath} ------- is modified");
        } catch (Exception e) {
            Log.e(e)
        } finally {
            if (!Utils.isNull(file)) {
                file.close()
            }
            if (!Utils.isNull(jarOutputStream)) {
                jarOutputStream.close()
            }
        }
        return optJar
    }

    /**
     * 获取应用程序包名
     * @return
     */
    private String getAppPackageName() {
        String pn = null
        try {
            def manifestFile = mAppExtension.sourceSets.main.manifest.srcFile
            pn = new XmlParser().parse(manifestFile).attribute('package')
        } catch (Exception e) {
            Log.e(e.getMessage())
        }
        return pn
    }

}