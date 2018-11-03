package com.tannotour.scaffanno.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mitnick on 2018/10/8.
 * Description 标识ViewModel
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ViewModelProvider{
    Class<?> kClass();
    String module() default "df";
}


//package com.tannotour.scaffanno.anno
//
//import kotlin.reflect.KClass
//
///**
// * Created by mitnick on 2018/10/8.
// * Description 标识数据源
// */
//@Target(AnnotationTarget.CLASS)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class ViewModelProvider(val kClass: KClass<out Any>, val module: String = "default")