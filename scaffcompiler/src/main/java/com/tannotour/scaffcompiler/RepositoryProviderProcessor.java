package com.tannotour.scaffcompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.tannotour.scaffanno.anno.RepositoryProvider;
import com.tannotour.scaffanno.table.IRepositoryProviderTable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.tannotour.scaffanno.anno.RepositoryProvider")
public class RepositoryProviderProcessor extends AbstractProcessor {

    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
        // 获得被该注解声明的元素
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RepositoryProvider.class);
        HashMap<String, String> pathMap = new HashMap<>();

        Element actionElement = processingEnv.getElementUtils().getTypeElement(RepositoryProvider.class.getName());
        TypeMirror actionType = actionElement.asType();

        for (Element element : elements) {
            if(element.getKind() != ElementKind.CLASS){
                continue;
            }
            RepositoryProvider routePath = element.getAnnotation(RepositoryProvider.class);
            String className = "";
            String providerClassName = element.toString();
            String moduleName = routePath.module();
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
                if(annotationMirror.getAnnotationType().equals(actionType)){
                    for( Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
                        if("kClass".equals(entry.getKey().getSimpleName().toString())){
                            className = entry.getValue().toString().replace(".class", "");
                            break;
                        }
                    }
                }
            }
            pathMap.put(moduleName + "--" + className, providerClassName);
        }
        /* 将路由表写入项目文件 */
        generateTable(pathMap);
        return true;
    }

    private void generateTable(HashMap<String, String> pathTable){
        HashMap<String, HashMap<String, String>> buffer = new HashMap<>();
        for(Map.Entry entry: pathTable.entrySet()){
            String[] groupKey = entry.getKey().toString().split("--");
            if(groupKey.length != 2){
                continue;
            }
            if(!buffer.containsKey(groupKey[0])){
                buffer.put(groupKey[0], new HashMap<String, String>());
            }
            buffer.get(groupKey[0]).put(groupKey[1], entry.getValue().toString());
        }
        for(Map.Entry<String, HashMap<String, String>> entry: buffer.entrySet()){
            generateTable(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 在项目中动态生成映射表
     * @param pathTable 映射表
     */
    private void generateTable(String module, HashMap<String, String> pathTable){
        ClassName hashMap = ClassName.get("java.util", "HashMap");
        ClassName string = ClassName.get("java.lang", "String");
        TypeName mapOfString2String = ParameterizedTypeName.get(hashMap, string, string);
        MethodSpec.Builder routes = MethodSpec.methodBuilder("methods")
                .addModifiers(Modifier.PUBLIC)
                .returns(mapOfString2String)
                .addStatement("HashMap<String, String> route = new HashMap<String, String>()");
        for(Map.Entry entry: pathTable.entrySet()){
            routes.addStatement("route.put($S, $S)", entry.getKey().toString(), entry.getValue().toString());
        }
        routes.addStatement("return route");
        TypeSpec routeTable = TypeSpec.classBuilder("RepositoryProviderTable")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(IRepositoryProviderTable.class)
                .addMethod(routes.build())
                .build();
        JavaFile javaFile = JavaFile.builder("com.tannotour.smartscaff." + module + ".repository", routeTable).build();
        try{
            javaFile.writeTo(mFiler);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}