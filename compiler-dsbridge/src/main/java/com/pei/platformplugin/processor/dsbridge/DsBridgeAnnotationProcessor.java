package com.pei.platformplugin.processor.dsbridge;

import com.google.auto.service.AutoService;
import com.pei.plaformplugin.annotation.Plugin;
import com.pei.plaformplugin.annotation.PluginMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
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
public class DsBridgeAnnotationProcessor extends AbstractProcessor {

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
                System.out.println("qualified name: " + typeElement.getQualifiedName());
                System.out.println("class name: " + typeElement.getSimpleName());
                System.out.println("super class: " + typeElement.getSuperclass());
                System.out.println("interfaces: " + typeElement.getInterfaces());
                Plugin annotation = typeElement.getAnnotation(Plugin.class);

                String name = annotation.name();
                if (name.length() == 0) {
                    //name = typeElement.getSimpleName().subSequence()
                }
                name += "Interface";

                TypeSpec typeSpec = generateJavascriptInterface(typeElement, name);
                JavaFile file = JavaFile.builder("com.pei.platformplugin.dsbridge", typeSpec).build();
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

    private TypeSpec generateJavascriptInterface(TypeElement pluginElement, String name) {
        String fieldName = "mPlugin";
        TypeName platformPluginType = ClassName.get(pluginElement);

        ClassName completionHandlerType = ClassName.get("wendu.dsbridge", "CompletionHandler");
        ClassName javascriptInterfaceType = ClassName.get("android.webkit", "JavascriptInterface");
        ClassName jsonObjectArgumentType = ClassName.get("com.pei.platformplugin", "JSONObjectArguments");
        ClassName callbackAdapterType = ClassName.get("com.pei.platformplugin.dsbridge", "CompletionHandlerCallbackAdapter");
        ClassName jsonObjectType = ClassName.get("org.json", "JSONObject");

        FieldSpec pluginField = FieldSpec.builder(platformPluginType, "mPlugin", Modifier.PRIVATE)
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addParameter(platformPluginType, "plugin")
                .addStatement("this.$L = $L", fieldName, "plugin")
                .build();

        List<MethodSpec> methods = new ArrayList<>();
        List<ExecutableElement> elementMethods = getAllMethods(pluginElement);
        for (ExecutableElement method : elementMethods) {
            PluginMethod methodAnnotation = method.getAnnotation(PluginMethod.class);
            if (methodAnnotation != null) {
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(method.getModifiers())
                        .addAnnotation(javascriptInterfaceType)
                        .returns(void.class)
                        .addParameter(Object.class, "params")
                        .addParameter(completionHandlerType, "handler")
                        .addStatement("$T args = ($T) params", jsonObjectType, jsonObjectType)
                        .addStatement("mPlugin.$L(new $T(args), new $T<>(handler))", method.getSimpleName(), jsonObjectArgumentType, callbackAdapterType);
                methods.add(methodSpecBuilder.build());
            }
        }

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addField(pluginField)
                .addMethod(constructor)
                .addMethods(methods);

        return typeSpecBuilder.build();
    }


    public List<ExecutableElement> getAllMethods(TypeElement type) {
        return ElementFilter.methodsIn(type.getEnclosedElements());
    }
}