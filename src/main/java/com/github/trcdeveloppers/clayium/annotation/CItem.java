package com.github.trcdeveloppers.clayium.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface CItem {
	String registryName() default "";

	/**
	 * oreDictもつけず、registryNameとも別の名前でplateなどを追加したい場合に。
	 * 例: octuple_compressed_pure_antimatterとopa
	 * lower_snake_case.
	 */
	String materialName() default "";
	String[] oreDicts() default {};
	CShape[] shapes() default {};
}
