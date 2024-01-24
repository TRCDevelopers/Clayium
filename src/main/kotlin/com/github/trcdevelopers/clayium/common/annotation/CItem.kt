package com.github.trcdevelopers.clayium.common.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class CItem(val registryName: String = "")
