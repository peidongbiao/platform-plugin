package com.pei.platformplugin.processor.reactnative;

import com.google.auto.service.AutoService;
import com.pei.plaformplugin.annotation.Extra;
import com.pei.plaformplugin.annotation.ExtraFinder;
import com.pei.plaformplugin.annotation.Plugin;
import com.pei.plaformplugin.annotation.PluginMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

@AutoService(Plugin.class)
public class ReactNativeAnnotationProcessor extends AbstractProcessor {
    private static final String NAME_SUFFIX = "Module";
    private static final String PACKAGE_NAME = "com.pei.platformplugin.reactnative";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Plugin.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Plugin.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement typeElement = (TypeElement) element;
                System.out.println("qualified name: " + typeElement.getQualifiedName() + ", class name: " + typeElement.getSimpleName());
                Plugin annotation = typeElement.getAnnotation(Plugin.class);

                String name = annotation.name();
                if (name.length() == 0) {
                    //name = typeElement.getSimpleName().subSequence()
                }
                name += NAME_SUFFIX;

                TypeSpec typeSpec = generateMethodCallHandler(typeElement, name);
                JavaFile file = JavaFile.builder(PACKAGE_NAME, typeSpec).build();
                try {
                    file.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        return false;
    }

    private TypeSpec generateMethodCallHandler(TypeElement pluginElement, String name) {

        ClassName platformPluginType = ClassName.get(pluginElement);
        ClassName reactContextBaseJavaModuleType = ClassName.get("com.facebook.react.bridge", "ReactContextBaseJavaModule");
        ClassName reactApplicationContextType = ClassName.get("com.facebook.react.bridge", "ReactApplicationContext");
        ClassName readableMapType = ClassName.get("com.facebook.react.bridge", "ReadableMap");
        ClassName callbackType = ClassName.get("com.facebook.react.bridge", "Callback");
        ClassName promiseType = ClassName.get("com.facebook.react.bridge", "Promise");
        ClassName nonNullType = ClassName.get("androidx.annotation", "NonNull");
        ClassName reactMethodType = ClassName.get("com.facebook.react.bridge", "ReactMethod");
        ClassName callbackAdapterType = ClassName.get("com.pei.platformplugin.reactnative", "ReactNativeCallbackAdapter");
        ClassName promiseAdapterType = ClassName.get("com.pei.platformplugin.reactnative", "ReactNativePromiseAdapter");
        ClassName mapArgumentsType = ClassName.get("com.pei.platformplugin", "MapArguments");

        Plugin plugin = pluginElement.getAnnotation(Plugin.class);
        String moduleName = ExtraFinder.findString(plugin.extras(), "ReactNativeModuleName", null);
        if (moduleName == null || moduleName.length() == 0) {
            moduleName = name;
        }

        String fieldName = "mPlugin";
        FieldSpec pluginField = FieldSpec.builder(platformPluginType, fieldName, Modifier.PROTECTED)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(reactApplicationContextType, "reactContext").addAnnotation(nonNullType).build())
                .addParameter(platformPluginType, "plugin")
                .addStatement("super(reactContext)")
                .addStatement("this.$L = $L", fieldName, "plugin")
                .build();


        MethodSpec getNameMethod = MethodSpec.methodBuilder("getName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addAnnotation(nonNullType)
                .addAnnotation(Override.class)
                .addStatement("return $S", moduleName)
                .build();

        List<MethodSpec> methods = new ArrayList<>();

        List<ExecutableElement> elementMethods = ElementFilter.methodsIn(pluginElement.getEnclosedElements());
        for (ExecutableElement method : elementMethods) {
            PluginMethod pluginMethodAnnotation = method.getAnnotation(PluginMethod.class);
            if (pluginMethodAnnotation != null) {
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(method.getModifiers())
                        .addAnnotation(reactMethodType)
                        .returns(void.class)
                        .addParameter(ParameterSpec.builder(readableMapType, "params").build());
                boolean isPromise = ExtraFinder.findBoolean(pluginMethodAnnotation.value(), "promise", false);
                if (isPromise) {
                    methodSpecBuilder.addParameter(promiseType, "promise")
                    .addStatement("mPlugin.$L(new $T(params.toHashMap()), new $T(promise))", method.getSimpleName(), mapArgumentsType, promiseAdapterType);
                } else {
                    methodSpecBuilder.addParameter(ParameterSpec.builder(callbackType, "success").build())
                            .addParameter(ParameterSpec.builder(callbackType, "failure").build())
                            .addStatement("mPlugin.$L(new $T(params.toHashMap()), new $T(success, failure))", method.getSimpleName(), mapArgumentsType, callbackAdapterType);
                }
                methods.add(methodSpecBuilder.build());
            }
        }

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(moduleName)
                .addModifiers(Modifier.PUBLIC)
                .superclass(reactContextBaseJavaModuleType)
                .addField(pluginField)
                .addMethod(constructor)
                .addMethod(getNameMethod)
                .addMethods(methods);

        return typeSpecBuilder.build();
    }
}