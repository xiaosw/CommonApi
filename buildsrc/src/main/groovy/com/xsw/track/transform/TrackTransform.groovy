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
        if (!isTrack) {
            return
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
                final def newJarName = "${jarName}_$_suffix"
                // Log.i("newJarName = $newJarName")
                // dest output file
                final def toFile = outputProvider.getContentLocation(newJarName,
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR)
                def from
                if (checkJarWhetherNeedModify(jarInput)) {
                    from = modifyJarFile(jarInput.file, context.getTemporaryDir())
                }
                if (null == from) {
                    from = jarInput.file
                } else {
                    saveModifiedJarForCheck(from)
                }
                // Log.i("copy from [${fromFile.absolutePath}] to [${toFile.absolutePath}]")
                FileUtils.copyFile(from, toFile)
            }

            input.directoryInputs.each { directoryInput ->
                final def classDir = directoryInput.file
                if (null != classDir) {
                    HashMap<String, File> modifyMap = new HashMap<>();
                    classDir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { classFile ->
                        def modifyFile = modifyClassFileIfNeeded(classDir, classFile, context.getTemporaryDir())
                        if (null != modifyFile) {
                            Log.i("modifyFile: ${modifyFile.absolutePath}")
                            modifyMap.put(file2FullClassName(classDir, classFile), modifyFile)
                        }
                    }
                    final def toFile = outputProvider.getContentLocation(directoryInput.name,
                            directoryInput.getContentTypes(),
                            directoryInput.getScopes(),
                            Format.DIRECTORY)
                    Log.i("directoryInput toFile: ${toFile.absolutePath}")
                    FileUtils.copyDirectory(directoryInput.file, toFile)
                    modifyMap.entrySet().each {
                        File target = new File(toFile.absolutePath + File.separator + it.getKey())
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
                if (whetherClassNeedModify(classFullName)) {
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
        final def whetherClassNeedModify = whetherClassNeedModify(fullClassName)
        if (!whetherClassNeedModify) {
            return null
        }
        byte[] sourceClassBytes = IOUtils.toByteArray(new FileInputStream(classFile))
        def modifiedClassBytes = ModifyClassUtil.modifyClass(fullClassName, sourceClassBytes)
        if (null != modifiedClassBytes) {
            File modifyFile = new File(tempDir,
                    fullClassName.concat(CLASS_SUFFIX))
            if (modifyFile.exists()) {
                modifyFile.delete()
            }
            modifyFile.createNewFile()
            FileOutputStream fos = new FileOutputStream(modifyFile)
            try {
                fos.write(sourceClassBytes)
                fos.flush()
                return modifyFile
            } catch(Exception e) {
                Log.e(e)
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
                .substring(0, absolutePath.length() - CLASS_SUFFIX.length())
    }

    private boolean whetherClassNeedModify(String fullClassName) {
        if (null == fullClassName
                || fullClassName.endsWith("BuildConfig")
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
        if (null == jarFile) {
            return null
        }
        /** 设置输出到的jar */
        def hexName = DigestUtils.md5Hex(jarFile.absolutePath)
        def optJar = new File(tempDir, hexName + jarFile.name)
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(optJar))
        /**
         * 读取原jar
         */
        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            String className

            ZipEntry zipEntry = new ZipEntry(entryName)

            jarOutputStream.putNextEntry(zipEntry)

            byte[] modifiedClassBytes = null
            byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)
            if (entryName.endsWith(CLASS_SUFFIX)) {
                className = absolutePath2FullClassName("", entryName)
                if (whetherClassNeedModify(className)) {
                    modifiedClassBytes = ModifyClassUtil.modifyClass(className,
                            sourceClassBytes)
                }
            }
            if (modifiedClassBytes == null) {
                jarOutputStream.write(sourceClassBytes);
            } else {
                jarOutputStream.write(modifiedClassBytes);
            }
            jarOutputStream.closeEntry();
        }
        Log.i("${hexName} is modified");
        jarOutputStream.close()
        file.close()
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