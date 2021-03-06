package com.tannotour.scaffcli.repository

import kotlin.reflect.KClass

/**
 * Created by mitnick on 2018/10/26.
 * Description
 */

@Target(AnnotationTarget.CLASS)
annotation class RepositoryWorker(val repositoryProvider: KClass<*>)
