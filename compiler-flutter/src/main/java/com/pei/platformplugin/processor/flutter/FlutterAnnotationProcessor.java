package com.pei.platformplugin.processor.flutter;

import com.google.auto.service.AutoService;
import com.pei.plaformplugin.annotation.Plugin;
import com.pei.plaformplugin.annotation.PluginMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
public class FlutterAnnotationProcessor extends AbstractProcessor {

    private static final String PLUGIN_NAME_SUFFIX = "FlutterPlugin";
    private static final String HANDLER_NAME_SUFFIX = "Handler";
    private static final String PACKAGE_NAME = "com.pei.platformplugin.flutter";

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

                TypeSpec methodCallHandlerTypeSpec = generateMethodCallHandler(typeElement, name + HANDLER_NAME_SUFFIX);
                writeToFile(methodCallHandlerTypeSpec);

                TypeSpec pluginTypeSpec = generateFlutterPlugin(typeElement, name + PLUGIN_NAME_SUFFIX, methodCallHandlerTypeSpec);
                writeToFile(pluginTypeSpec);

                return false;
            }
        }
        return false;
    }

    private boolean writeToFile(TypeSpec typeSpec) {
        JavaFile file = JavaFile.builder(PACKAGE_NAME, typeSpec).build();
        try {
            file.writeTo(processingEnv.getFiler());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TypeSpec generateMethodCallHandler(TypeElement pluginElement, String name) {
        String fieldName = "mPlugin";

        ClassName baseMethodCallHandlerType = ClassName.get("com.pei.platformplugin.flutter", "BaseMethodCallHandler");
        ClassName platformPluginType = ClassName.get(pluginElement);
        ClassName methodCallType = ClassName.get("io.flutter.plugin.common", "MethodCall");
        ClassName methodChannelResultType = ClassName.get("io.flutter.plugin.common",  "MethodChannel", "Result");
        ClassName resultCallbackAdapterType = ClassName.get("com.pei.platformplugin.flutter", "MethodChannelResultCallbackAdapter");
        ClassName nonNullType = ClassName.get("androidx.annotation", "NonNull");

        FieldSpec pluginField = FieldSpec.builder(platformPluginType, "mPlugin", Modifier.PROTECTED)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(platformPluginType, "plugin")
                .addStatement("this.$L = $L", fieldName, "plugin")
                .build();

        List<MethodSpec> methods = new ArrayList<>();

        List<ExecutableElement> elementMethods = ElementFilter.methodsIn(pluginElement.getEnclosedElements());
        for (ExecutableElement method : elementMethods) {
            PluginMethod methodAnnotation = method.getAnnotation(PluginMethod.class);
            if (methodAnnotation != null) {
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(void.class)
                        .addParameter(ParameterSpec.builder(methodCallType, "call").addAnnotation(nonNullType).build())
                        .addParameter(ParameterSpec.builder(methodChannelResultType, "result").addAnnotation(nonNullType).build())
                        .addStatement("mPlugin.$L(toArguments(call.arguments), new $T(result))", method.getSimpleName(), resultCallbackAdapterType);
                methods.add(methodSpecBuilder.build());
            }
        }

        MethodSpec.Builder onMethodCallMethodBuilder = MethodSpec.methodBuilder("onMethodCall")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterSpec.builder(methodCallType, "call").addAnnotation(nonNullType).build())
                .addParameter(ParameterSpec.builder(methodChannelResultType, "result").addAnnotation(nonNullType).build());

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                .addStatement("$T name = call.method", String.class);

        for (int i = 0; i < elementMethods.size(); i++) {
            ExecutableElement method = elementMethods.get(i);
            PluginMethod methodAnnotation = method.getAnnotation(PluginMethod.class);
            if (methodAnnotation == null) continue;
            CharSequence methodName = method.getSimpleName();
            if (i == 0) {
                codeBlockBuilder.beginControlFlow("if ($S.equals(name))", methodName);
                codeBlockBuilder.addStatement("$L(call, result)", methodName);
            } else if (i == elementMethods.size() - 1) {
                codeBlockBuilder.nextControlFlow("else");
                codeBlockBuilder.addStatement("result.notImplemented()");
            } else {
                codeBlockBuilder.nextControlFlow("else if ($S.equals(name))", methodName);
                codeBlockBuilder.addStatement("$L(call, result)", methodName);
            }
        }
        codeBlockBuilder.endControlFlow();
        onMethodCallMethodBuilder.addCode(codeBlockBuilder.build());

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(baseMethodCallHandlerType)
                .addField(pluginField)
                .addMethod(constructor)
                .addMethod(onMethodCallMethodBuilder.build())
                .addMethods(methods);

        return typeSpecBuilder.build();
    }

    private TypeSpec generateFlutterPlugin(TypeElement pluginElement, String name, TypeSpec methodCallHandlerType) {

        ClassName flutterPluginType = ClassName.get("io.flutter.embedding.engine.plugins", "FlutterPlugin");
        ClassName platformPluginType = ClassName.get(pluginElement);
        ClassName methodChannelType = ClassName.get("io.flutter.plugin.common", "MethodChannel");
        ClassName flutterPluginBinding = ClassName.get("io.flutter.embedding.engine.plugins", "FlutterPlugin", "FlutterPluginBinding");
        ClassName nonNullType = ClassName.get("androidx.annotation", "NonNull");
        ClassName overrideType = ClassName.get("java.lang", "Override");

        String pluginFieldName = "mPlugin";
        String channelFieldName = "mMethodChannel";
        String channelNameFieldName = "mMethodChannelName";

        FieldSpec pluginField = FieldSpec.builder(platformPluginType, pluginFieldName, Modifier.PROTECTED).build();
        FieldSpec channelField = FieldSpec.builder(methodChannelType, channelFieldName, Modifier.PROTECTED).build();
        FieldSpec channelNameField = FieldSpec.builder(String.class, channelNameFieldName, Modifier.PROTECTED).build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(String.class, "methodChannelName")
                .addParameter(platformPluginType, "plugin")
                .addStatement("this.$L = plugin", pluginFieldName)
                .addStatement("this.$L = methodChannelName", channelNameFieldName)
                .build();

        MethodSpec onAttachedToEngine = MethodSpec.methodBuilder("onAttachedToEngine")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(overrideType)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(flutterPluginBinding, "binding").addAnnotation(nonNullType).build())
                .addStatement("$L = new $T(binding.getBinaryMessenger(), $L)", channelFieldName, methodChannelType, channelNameFieldName)
                .addStatement("$L.setMethodCallHandler(new $N($L))", channelFieldName, methodCallHandlerType, pluginFieldName)
                .build();

        MethodSpec onDetachedFromEngine = MethodSpec.methodBuilder("onDetachedFromEngine")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(overrideType)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(flutterPluginBinding, "binding").addAnnotation(nonNullType).build())
                .addStatement("$L.setMethodCallHandler(null)", channelFieldName)
                .addStatement("$L = null", channelFieldName)
                .build();

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(flutterPluginType)
                .addField(pluginField)
                .addField(channelField)
                .addField(channelNameField)
                .addMethod(constructor)
                .addMethod(onAttachedToEngine)
                .addMethod(onDetachedFromEngine);

        return typeSpecBuilder.build();
    }
}